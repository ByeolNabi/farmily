import paho.mqtt.client as mqtt
import time
from message_builder import MessageBuilder

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
TOPIC = "farmily/raspi/sensor/all"
TRANSPORT = "websockets"  # 웹소켓 방식 설정
PATH = "/mqtt"            # Nginx에서 설정한 경로

# 연결 성공 시 호출되는 콜백
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"연결 성공! (코드: {rc})")
    else:
        print(f"연결 실패 (코드: {rc})")

# 1. 클라이언트 인스턴스 생성 (Websockets 설정 필수)
client = mqtt.Client(transport=TRANSPORT)

# 2. SSL/TLS 설정 (443 포트 접속을 위해 필수)
# 기본적으로 시스템의 CA 인증서를 사용하도록 설정
client.tls_set()

# 3. 웹소켓 경로 설정
client.ws_set_options(path=PATH)

# 4. 콜백 함수 등록
client.on_connect = on_connect

# 5. 브로커 연결
print(f"{BROKER_HOST}에 연결 시도 중...")
client.connect(BROKER_HOST, BROKER_PORT, 60)

# 6. 네트워크 루프 시작 (비동기 방식)
client.loop_start()

# 메시지 빌더 인스턴스 생성
builder = MessageBuilder(device_id="raspi_sensors")

# 초기 센서 값
sensor_data = {
    "temperature": 25.0,
    "humidity": 60.0,
    "illuminance": 1000.0,
    "soil_moisture": 50.0
}

try:
    while True:
        # 7. 1초마다 센서 데이터 변경 및 발행
        
        # 값 변경 (0.1씩 증가)
        for key in sensor_data:
            sensor_data[key] = round(sensor_data[key] + 0.1, 1)

        # 메시지 생성
        message = builder.create_telemetry(sensor_data)
        
        # 발행
        client.publish(TOPIC, message)
        print(f"메시지 전송: {message}")
        
        time.sleep(1)
except KeyboardInterrupt:
    print("종료 중...")
    client.loop_stop()
    client.disconnect()