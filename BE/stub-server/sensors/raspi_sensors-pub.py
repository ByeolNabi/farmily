import paho.mqtt.client as mqtt
import time
from message_builder import MessageBuilder

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
TOPIC = "farmily/raspi/sensor/all"
TRANSPORT = "websockets"
PATH = "/mqtt"

# --- 상식적인 센서 데이터 범위 설정 ---
# 각 센서의 최소값(min)과 최대값(max)을 정의합니다.
SENSOR_RANGES = {
    "temperature": {"min": 20.0, "max": 25.0},      # 요청하신 범위 (20~25도)
    "humidity": {"min": 40.0, "max": 60.0},         # 일반적인 적정 습도 (%)
    "illuminance": {"min": 7000.0, "max": 12000.0},   # 조도 (Lux)
    "soil_moisture": {"min": 30.0, "max": 60.0}     # 토양 수분 (%)
}

# --- 센서 값 증감 방향 관리 ---
# 1이면 증가, -1이면 감소
sensor_directions = {
    "temperature": 1,
    "humidity": 1,
    "illuminance": 1,
    "soil_moisture": 1
}

# 연결 성공 시 호출되는 콜백
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"연결 성공! (코드: {rc})")
    else:
        print(f"연결 실패 (코드: {rc})")

# 1. 클라이언트 인스턴스 생성
client = mqtt.Client(transport=TRANSPORT)

# 2. SSL/TLS 설정
client.tls_set()

# 3. 웹소켓 경로 설정
client.ws_set_options(path=PATH)

# 4. 콜백 함수 등록
client.on_connect = on_connect

# 5. 브로커 연결
print(f"{BROKER_HOST}에 연결 시도 중...")
client.connect(BROKER_HOST, BROKER_PORT, 60)

# 6. 네트워크 루프 시작
client.loop_start()

# 메시지 빌더 인스턴스 생성
builder = MessageBuilder(device_id="raspi_sensors")

# 초기 센서 값 (범위의 중간값 등으로 시작)
sensor_data = {
    "temperature": 20.0,
    "humidity": 40.0,
    "illuminance": 800.0,
    "soil_moisture": 30.0
}

try:
    while True:
        # 7. 센서 데이터 업데이트 (Ping-Pong 로직)
        for key in sensor_data:
            # 현재 값에 방향 * 0.1을 더함
            new_value = sensor_data[key] + (0.1 * sensor_directions[key])
            
            # 범위 체크 및 방향 전환
            if new_value >= SENSOR_RANGES[key]["max"]:
                new_value = SENSOR_RANGES[key]["max"]
                sensor_directions[key] = -1  # 감소 모드로 전환
            elif new_value <= SENSOR_RANGES[key]["min"]:
                new_value = SENSOR_RANGES[key]["min"]
                sensor_directions[key] = 1   # 증가 모드로 전환
            
            # 소수점 첫째 자리까지만 유지
            sensor_data[key] = round(new_value, 1)

        # 메시지 생성 (요청하신 포맷 유지)
        message = builder.create_telemetry(sensor_data)
        
        # 발행
        client.publish(TOPIC, message)
        print(f"메시지 전송: {message}")
        
        time.sleep(1)

except KeyboardInterrupt:
    print("종료 중...")
    client.loop_stop()
    client.disconnect()