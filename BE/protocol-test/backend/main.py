from fastapi import FastAPI
import paho.mqtt.client as mqtt
import threading
import time

app = FastAPI()

# MQTT 설정
BROKER = "localhost"
PORT = 1883
mqtt_client = mqtt.Client()

# FastAPI가 시작될 때 MQTT 연결
@app.on_event("startup")
async def startup_event():
    try:
        mqtt_client.connect(BROKER, PORT, 60)
        mqtt_client.loop_start()  # 백그라운드에서 MQTT 메시지 처리
        print("[FastAPI] Connected to MQTT Broker")
    except Exception as e:
        print(f"[FastAPI] MQTT Connection Error: {e}")

@app.on_event("shutdown")
async def shutdown_event():
    mqtt_client.loop_stop()
    print("[FastAPI] MQTT Disconnected")

@app.get("/")
def read_root():
    return {"message": "FastAPI with MQTT is running"}

# 예시: Android/Unity에게 특정 이벤트를 푸시하고 싶을 때 사용
@app.post("/api/command/{command_type}")
def send_command_to_clients(command_type: str):
    """
    Android나 Unity로 명령을 내릴 때 사용합니다.
    HTTP 요청을 받아서 MQTT 토픽으로 발행(Publish)합니다.
    """
    topic = "home/command"
    payload = f"Server Command: {command_type}"
    
    mqtt_client.publish(topic, payload)
    return {"status": "published", "topic": topic, "payload": payload}

# 예시: 서버가 센서 데이터를 구독(Subscribe)해야 한다면?
# 실제로는 별도의 스레드나 프로세스에서 처리하거나, 
# paho-mqtt의 on_message 콜백을 등록하여 DB에 저장하는 로직을 추가합니다.
def on_connect(client, userdata, flags, rc):
    print(f"[FastAPI] Connected with result code {rc}")
    # 연결이 되면 구독을 신청합니다.
    client.subscribe("home/livingroom/#")

def on_message(client, userdata, msg):
    print(f"[FastAPI Server Log] Received from {msg.topic}: {msg.payload.decode()}")

mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message
