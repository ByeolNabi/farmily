import paho.mqtt.client as mqtt

BROKER = "localhost"
PORT = 1883
TOPICS = [("home/livingroom/#", 0)]  # 거실의 모든 센서 구독

def on_connect(client, userdata, flags, rc):
    print(f"[Android Mock] Connected with result code {rc}")
    client.subscribe(TOPICS)

def on_message(client, userdata, msg):
    print(f"[Android UI Update] Topic: {msg.topic}, Data: {msg.payload.decode()} received.")

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

print("[Android Mock] Starting...")
try:
    client.connect(BROKER, PORT, 60)
    client.loop_forever()
except KeyboardInterrupt:
    print("[Android Mock] Disconnected")
except Exception as e:
    print(f"[Android Mock] Error: {e}")
