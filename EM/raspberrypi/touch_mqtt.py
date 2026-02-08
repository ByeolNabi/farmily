import time
import json
import uuid
import datetime
import paho.mqtt.client as mqtt
import board
import digitalio

# =====================
# Touch sensor
# =====================
TOUCH_GPIO = 27  # BCM
touch = digitalio.DigitalInOut(getattr(board, f"D{TOUCH_GPIO}"))
touch.direction = digitalio.Direction.INPUT

def read_touch() -> int:
    return 1 if touch.value else 0

# =====================
# MQTT (WSS)S
# =====================
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
PATH = "/mqtt"
TOPIC = "farmily/devices/device_1/event"
TRANSPORT = "websockets"
DEVICE_ID = "raspi_touch"

def on_connect(client, userdata, flags, rc):
    print("MQTT Connect success" if rc == 0 else f"MQTT Connect Fail rc={rc}")

def build_message(payload: dict) -> str:
    return json.dumps({
        "header": {
            "msg_id": str(uuid.uuid4()),
            "type": "event",
            "device_id": DEVICE_ID,
            "timestamp": datetime.datetime.now().isoformat()
        },
        "payload": payload
    }, ensure_ascii=False)

# =====================
# start
# =====================
client = mqtt.Client(transport=TRANSPORT)
client.tls_set()
client.ws_set_options(path=PATH)
client.on_connect = on_connect

print("Try Broker Connecting")
client.connect(BROKER_HOST, BROKER_PORT, 60)
client.loop_start()

SEND_INTERVAL_SEC = 3.0
last_sent_ts = 0.0


try:
    while True:
        touch_val = read_touch()

        now = time.time()

        # touch==1 10sec transfer
        if touch_val == 1 and (now - last_sent_ts) >= SEND_INTERVAL_SEC:
            message = build_message({"event": "TOUCH_DETECTED"})
            info = client.publish(TOPIC, message)
            last_sent_ts = now
            print(f"touch event sent (rc={info.rc})")

        time.sleep(0.05)

except KeyboardInterrupt:
    print("finish")

finally:
    try:
        client.loop_stop()
        client.disconnect()
    except Exception:
        pass

    try:
        touch.deinit()
    except Exception:
        pass
