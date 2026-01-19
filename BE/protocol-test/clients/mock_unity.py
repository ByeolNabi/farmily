import paho.mqtt.client as mqtt

BROKER = "localhost"
PORT = 1883
TOPICS = [("home/livingroom/lux", 0)]  # Unity는 예시로 조도만 구독한다고 가정

def on_connect(client, userdata, flags, rc):
    print(f"[Unity Mock] Connected with result code {rc}")
    client.subscribe(TOPICS)

def on_message(client, userdata, msg):
    print(f"[Unity 3D Scene] Adjusting Light Intensity based on Topic: {msg.topic}, Value: {msg.payload.decode()}")

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

print("[Unity Mock] Starting...")
try:
    client.connect(BROKER, PORT, 60)
    client.loop_forever()
except KeyboardInterrupt:
    print("[Unity Mock] Disconnected")
except Exception as e:
    print(f"[Unity Mock] Error: {e}")
