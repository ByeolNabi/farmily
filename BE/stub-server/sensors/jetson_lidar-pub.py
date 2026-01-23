import paho.mqtt.client as mqtt
import time
import math
from message_builder import MessageBuilder

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
TOPIC = "farmily/jetson/lidar/pos"
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
builder = MessageBuilder(device_id="jetson_lidar")

try:
    while True:
        # 7. 1초마다 LiDAR 위치 데이터 생성 및 발행
        current_time = time.time()
        
        # 가상의 이동 경로 생성 (원을 그리며 이동)
        # 중심(10, 10), 반지름 5
        radius = 5.0
        speed = 0.2  # 회전 속도
        
        x = 10.0 + radius * math.cos(current_time * speed)
        y = 10.0 + radius * math.sin(current_time * speed)
        
        # 차량이 바라보는 방향 (접선 방향)
        theta_rad = (current_time * speed) + (math.pi / 2)
        theta = math.degrees(theta_rad) % 360

        payload_data = {
            "device_id": "farmily",
            "x": round(x, 2),
            "y": round(y, 2),
            "theta": round(theta, 1),
            "map_id": "ssafy_room_A",
            "timestamp": current_time
        }

        # 메시지 생성
        message = builder.create_telemetry(payload_data)
        
        # 발행
        client.publish(TOPIC, message)
        print(f"메시지 전송: {TOPIC} \n -> {message}")
        
        time.sleep(1)
except KeyboardInterrupt:
    print("종료 중...")
    client.loop_stop()
    client.disconnect()