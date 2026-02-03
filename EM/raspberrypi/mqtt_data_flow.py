import time
import json
import uuid
import datetime
from smbus2 import SMBus
import paho.mqtt.client as mqtt
import board
import adafruit_dht
import spidev

# =====================
# DHT
# =====================
DHT_GPIO = 17
DHT_TYPE = "DHT11"  # or "DHT22"

last_temp = None
last_hum = None
_dht_init_ok = False
_dht_err_printed = False

USE_PULSEIO = False

try:
    if DHT_TYPE.upper() == "DHT22":
        dht = adafruit_dht.DHT22(getattr(board, f"D{DHT_GPIO}"), use_pulseio=USE_PULSEIO)
    else:
        dht = adafruit_dht.DHT11(getattr(board, f"D{DHT_GPIO}"), use_pulseio=USE_PULSEIO)
    _dht_init_ok = True
    print("DHT init OK", flush=True)
except Exception as e:
    dht = None
    print(f"DHT init FAIL: {repr(e)}", flush=True)

def read_dht():
    global last_temp, last_hum, _dht_err_printed
    if dht is None:
        return last_temp, last_hum

    try:
        t = dht.temperature
        h = dht.humidity

        if t is None or h is None:
            return last_temp, last_hum

        last_temp = round(float(t), 1)
        last_hum = round(float(h), 1)
        return last_temp, last_hum

    except Exception as e:
        if not _dht_err_printed:
            print(f"DHT read FAIL: {repr(e)}", flush=True)
            _dht_err_printed = True
        return last_temp, last_hum


# =====================
# BH1750
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

def read_lux():
    data = BUS.read_i2c_block_data(BH1750_ADDR, CONT_H_RES_MODE, 2)
    raw = (data[0] << 8) | data[1]
    return round(raw / 1.2, 1)

# =====================
# SOIL via MCP3008
# =====================

SOIL_CH = 0 
SPI_BUS = 0
SPI_DEV = 0          

spi = spidev.SpiDev()
spi.open(SPI_BUS, SPI_DEV)
spi.max_speed_hz = 1350000


def read_soil_raw_10bit():
    # soil_adc.value: 0.0 ~ 1.0
    r = spi.xfer2([1, (8 + SOIL_CH) << 4, 0])
    return ((r[1] & 3) << 8) | r[2]

def clamp(v, lo, hi):
    return lo if v < lo else hi if v > hi else v

SOIL_DRY = 900
SOIL_WET = 350


# =====================
# MQTT (WSS)
# =====================
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
PATH = "/mqtt"
TOPIC = "farmily/raspi/sensor/all"
TRANSPORT = "websockets"
DEVICE_ID = "raspi_sensors"

def on_connect(client, userdata, flags, rc):
    print("MQTT Connect success" if rc == 0 else f"MQTT Connect Fail rc={rc}", flush=True)

def build_msg(payload: dict) -> str:
    return json.dumps({
        "header": {
            "msg_id": str(uuid.uuid4()),
            "type": "telemetry",
            "device_id": DEVICE_ID,
            "timestamp": datetime.datetime.now().isoformat()
        },
        "payload": payload
    }, ensure_ascii=False)


# =====================
# start
# =====================
init_bh1750()

client = mqtt.Client(transport=TRANSPORT)
client.tls_set()
client.ws_set_options(path=PATH)
client.on_connect = on_connect

print("Try Broker Connecting", flush=True)
client.connect(BROKER_HOST, BROKER_PORT, 60)
client.loop_start()

try:
    while True:
        lux = read_lux()
        temp, hum = read_dht()
        soil = read_soil_raw_10bit()
        soil_reverse = 1023 - soil

        payload = {
            "temperature": temp,
            "humidity": hum,
            "illuminance": lux,
            "soil": soil
        }

        info = client.publish(TOPIC, build_msg(payload))
        # publish 
        if info.rc != 0:
            print(f"Publish FAIL rc={info.rc}", flush=True)

        print(f"temp={temp} hum={hum} lux={lux} soil={soil_reverse}", flush=True)
        time.sleep(2)

except KeyboardInterrupt:
    print("finish", flush=True)

finally:
    try:
        client.loop_stop()
        client.disconnect()
    except Exception:
        pass

    try:
        BUS.close()
    except Exception:
        pass

    try:
        if dht is not None:
            dht.exit()
    except Exception:
        pass

    try:
        spi.close()
    except Exception:
        pass
