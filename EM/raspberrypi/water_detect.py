import time
import json
import uuid
import datetime
from collections import deque

import paho.mqtt.client as mqtt

# =====================
# MQTT (WSS)
# =====================
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
WS_PATH = "/mqtt"
TRANSPORT = "websockets"

SUB_TOPIC = "farmily/raspi/sensor/all"
PUB_TOPIC = "farmily/raspi/event/water"

DEVICE_ID = "raspi_sensors"  

# =====================
# Water detection (Queue)
# =====================
QSIZE = 10
FRONT_N = 3
BACK_N = 3

DELTA_TH = 100
MIN_BASELINE = 200
COOLDOWN_SEC = 90

q = deque(maxlen=QSIZE)
last_event_ts = 0

def mean(xs):
    return sum(xs) / len(xs)

def build_event():
    return json.dumps({
        "header": {
            "msg_id": str(uuid.uuid4()),
            "type": "event",
            "device_id": DEVICE_ID,
            "timestamp": datetime.datetime.now().isoformat()
        },
        "payload": {
            "event": "WATER_DETECTED"
        }
    }, ensure_ascii=False)

def extract_soil(msg: dict):
    try:
        return msg["payload"]["soil_moisture"]
    except Exception:
        return None

def detect_watering(soil_value: float):
    global last_event_ts

    q.append(float(soil_value))

    if len(q) < QSIZE:
        return False, None, None, None

    lst = list(q)
    before_avg = mean(lst[:FRONT_N])
    after_avg = mean(lst[-BACK_N:])
    delta = after_avg - before_avg

    now = time.time()

    if (now - last_event_ts) < COOLDOWN_SEC:
        return False, before_avg, after_avg, delta

    if after_avg < MIN_BASELINE:
        return False, before_avg, after_avg, delta

    if delta >= DELTA_TH:
        last_event_ts = now
        return True, before_avg, after_avg, delta

    return False, before_avg, after_avg, delta

# =====================
# MQTT callbacks
# =====================
def on_connect(client, userdata, flags, rc):
    print("MQTT Connect success" if rc == 0 else f"MQTT Connect Fail rc={rc}", flush=True)
    if rc == 0:
        client.subscribe(SUB_TOPIC)
        print(f"Subscribed: {SUB_TOPIC}", flush=True)

def on_message(client, userdata, message):
    try:
        data = json.loads(message.payload.decode("utf-8"))
    except Exception:
        return

    soil = extract_soil(data)
    if soil is None:
        return

    try:
        soil = float(soil)
    except Exception:
        return

    watering, b, a, d = detect_watering(soil)

    # log
    if b is None:
        print(f"soil={soil:.0f} (warming up queue {len(q)}/{QSIZE})", flush=True)
    else:
        print(f"soil={soil:.0f} before={b:.1f} after={a:.1f} delta={d:.1f} watering={watering}", flush=True)

    # event
    if watering:
        info = client.publish(PUB_TOPIC, build_event())
        if info.rc != 0:
            print(f"Publish FAIL rc={info.rc}", flush=True)
        else:
            print(f"[PUBLISHED] WATER_DETECTED", flush=True)

# =====================
# main
# =====================
def main():
    client = mqtt.Client(transport=TRANSPORT)
    client.tls_set()
    client.ws_set_options(path=WS_PATH)

    client.on_connect = on_connect
    client.on_message = on_message

    print("Try Broker Connecting", flush=True)
    client.connect(BROKER_HOST, BROKER_PORT, 60)

    try:
        client.loop_forever()
    except KeyboardInterrupt:
        print("finish", flush=True)
    finally:
        try:
            client.disconnect()
        except Exception:
            pass

if __name__ == "__main__":
    main()
