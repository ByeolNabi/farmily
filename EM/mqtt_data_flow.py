import time
import json
import uuid
import datetime
from smbus2 import SMBus
import spidev
import paho.mqtt.client as mqtt
import board
import adafruit_dht

# =====================
# DHT 
# =====================

DHT_GPIO = 17     
DHT_TYPE = "DHT11" 

# DHT 
if DHT_TYPE.upper() == "DHT22":
    dht = adafruit_dht.DHT22(getattr(board, f"D{DHT_GPIO}"))
else:
    dht = adafruit_dht.DHT11(getattr(board, f"D{DHT_GPIO}"))

last_temp = None
last_hum = None

def read_dht():
    global last_temp, last_hum
    try:
        t = dht.temperature
        h = dht.humidity
        if t is None or h is None:
            return last_temp, last_hum
        last_temp = round(float(t), 1)
        last_hum = round(float(h), 1)
        return last_temp, last_hum
    except Exception:
        # DHT RuntimeError
        return last_temp, last_hum


# =====================
# MQTT option (WSS)
# =====================
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
PATH = "/mqtt"
TOPIC = "farmily/raspi/sensor/all"
TRANSPORT = "websockets"

DEVICE_ID = "raspi_sensors"

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("MQTT Connect success")
    else:
        print(f"MQTT Connect Fail rc={rc}")

# =====================
# BH1750 (I2C)
# =====================
BUS = SMBus(1)
BH1750_ADDR = 0x23

POWER_ON = 0x01
RESET = 0x07
CONT_H_RES_MODE = 0x10

def init_bh1750():
    BUS.write_byte(BH1750_ADDR, POWER_ON)
    BUS.write_byte(BH1750_ADDR, RESET)
    BUS.write_byte(BH1750_ADDR, CONT_H_RES_MODE)
    time.sleep(0.2)

def read_lux() -> float:
    data = BUS.read_i2c_block_data(BH1750_ADDR, CONT_H_RES_MODE, 2)
    raw = (data[0] << 8) | data[1]
    return round(raw / 1.2, 1)

# =====================
# soil sensor 
# =====================
spi = spidev.SpiDev()
spi.open(0, 0)                 # bus=0, device=0 (CE0)
spi.max_speed_hz = 100000

SOIL_CH_CMD = 0x80

def read_soil_raw() -> int:
    r = spi.xref2([1, SOIL_CH_CMD, 0])
    return int(r[2])

# =====================
# message builder
# =====================
def build_telemetry_message(device_id: str, payload: dict) -> str:
    msg = {
        "header": {
            "msg_id": str(uuid.uuid4()),
            "type": "telemetry",
            "device_id": device_id,
            "timestamp": datetime.datetime.now().isoformat()
        },
        "payload": payload
    }
    return json.dumps(msg, ensure_ascii=False)

# =====================
# start
# =====================
init_bh1750()

client = mqtt.Client(transport=TRANSPORT)
client.tls_set()
client.ws_set_options(path=PATH)
client.on_connect = on_connect

print("Try Broker Connecting")
client.connect(BROKER_HOST, BROKER_PORT, 60)
client.loop_start()

try:
    while True:
        lux = read_lux()
        soil_raw = read_soil_raw()

        temp, hum = read_dht() 

        payload = {
            "temperature": temp,   
            "humidity": hum,
            "illuminance": lux,
            "soil_raw": soil_raw
        }

        message = build_telemetry_message(DEVICE_ID, payload)
        client.publish(TOPIC, message)

        print(f"lux={lux} soil_raw={soil_raw} temp={temp} hum={hum}")
        time.sleep(1)

except KeyboardInterrupt:
    print("finish")

finally:
    client.loop_stop()
    client.disconnect()
    spi.close()
    try:
        dht.exit()
    except Exception:
        pass
