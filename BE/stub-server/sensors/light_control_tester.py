"""
Light Control Service 테스트 스크립트
조도 기반 식물등 제어 시나리오를 시뮬레이션합니다.

테스트 순서:
1. 낮은 조도 센서 데이터 전송 (여러 번) → FastAPI에서 LOW_LIGHT 감지
2. 로봇 위치 데이터 전송 (Station 근처) → FastAPI에서 ARRIVED 감지 → LIGHT_ON
"""
import paho.mqtt.client as mqtt
import json
import time
import sys
import uuid
from datetime import datetime

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
TRANSPORT = "websockets"
PATH = "/mqtt"

# 토픽 정의
TOPIC_SENSOR = "farmily/raspi/sensor/all"
TOPIC_JETSON = "farmily/jetson/lidar/pos"

# 연결 성공 시 호출되는 콜백
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"\n✅ MQTT 브로커 연결 성공! ({BROKER_HOST}:{BROKER_PORT})")
    else:
        print(f"\n❌ 연결 실패 (코드: {rc})")

# 클라이언트 설정
client = mqtt.Client(transport=TRANSPORT)
client.tls_set()
client.ws_set_options(path=PATH)
client.on_connect = on_connect

# 브로커 연결
print(f"🔗 {BROKER_HOST}에 연결 시도 중...")
try:
    client.connect(BROKER_HOST, BROKER_PORT, 60)
except Exception as e:
    print(f"❌ 연결 오류 발생: {e}")
    sys.exit(1)

client.loop_start()
time.sleep(1)


def create_sensor_message(temp, humidity, illuminance, soil):
    """센서 telemetry 메시지 생성"""
    return json.dumps({
        "header": {
            "msg_id": str(uuid.uuid4()),
            "type": "telemetry",
            "device_id": "raspi_sensors",
            "timestamp": datetime.now().isoformat()
        },
        "payload": {
            "temperature": temp,
            "humidity": humidity,
            "illuminance": illuminance,
            "soil_moisture": soil
        }
    }, ensure_ascii=False)


def create_jetson_pos_message(x, y, theta=0.0):
    """Jetson 로봇 위치 telemetry 메시지 생성"""
    return json.dumps({
        "header": {
            "msg_id": str(uuid.uuid4()),
            "type": "telemetry",
            "device_id": "jetson_lidar",
            "timestamp": datetime.now().isoformat()
        },
        "payload": {
            "device_id": "farmily",
            "x": x,
            "y": y,
            "theta": theta,
            "map_id": "ssafy_room_A",
            "timestamp": time.time()
        }
    }, ensure_ascii=False)


def send_message(topic, message):
    """메시지 전송"""
    info = client.publish(topic, message)
    info.wait_for_publish()
    if info.rc == mqtt.MQTT_ERR_SUCCESS:
        print(f"  📤 전송 완료: {topic}")
    else:
        print(f"  ❌ 전송 실패 (코드: {info.rc})")


def print_menu():
    print("\n" + "="*50)
    print("   🌱 Light Control Service 테스트")
    print("="*50)
    print("1. 🌙 낮은 조도 센서 데이터 전송 (500 lux)")
    print("2. ☀️ 높은 조도 센서 데이터 전송 (10000 lux)")
    print("3. 🚗 로봇 위치 - 멀리 (10, 10)")
    print("4. 🏠 로봇 위치 - Station 도착 (0.1, 0.1)")
    print("5. 🔄 시나리오 자동 실행 (낮은 조도 → 로봇 도착)")
    print("q. ❌ 종료")
    print("-" * 50)


try:
    while True:
        print_menu()
        choice = input("선택하세요 > ").strip().lower()

        if choice == '1':
            print("\n🌙 낮은 조도 데이터 전송 중...")
            message = create_sensor_message(
                temp=25.0,
                humidity=60.0,
                illuminance=500.0,  # 낮은 조도!
                soil=45.0
            )
            send_message(TOPIC_SENSOR, message)
            print(f"  💡 조도: 500 lux (기준값보다 낮음)")
            
        elif choice == '2':
            print("\n☀️ 높은 조도 데이터 전송 중...")
            message = create_sensor_message(
                temp=25.0,
                humidity=60.0,
                illuminance=10000.0,  # 높은 조도
                soil=45.0
            )
            send_message(TOPIC_SENSOR, message)
            print(f"  💡 조도: 10000 lux (충분함)")
            
        elif choice == '3':
            print("\n🚗 로봇 위치 (멀리) 전송 중...")
            message = create_jetson_pos_message(x=10.0, y=10.0)
            send_message(TOPIC_JETSON, message)
            print(f"  📍 위치: (10.0, 10.0) - Station에서 멀리")
            
        elif choice == '4':
            print("\n🏠 로봇 위치 (Station 도착) 전송 중...")
            message = create_jetson_pos_message(x=0.1, y=0.1)
            send_message(TOPIC_JETSON, message)
            print(f"  📍 위치: (0.1, 0.1) - Station 도착!")
            
        elif choice == '5':
            print("\n🔄 시나리오 자동 실행")
            print("="*50)
            
            # Step 1: 낮은 조도 데이터 여러 번 전송
            print("\n[Step 1] 낮은 조도 데이터 전송 (5회, 1초 간격)")
            print("  → FastAPI 로그: '[LightControl] LOW LIGHT DETECTED!' 확인")
            for i in range(5):
                message = create_sensor_message(25, 60, 500, 45)
                send_message(TOPIC_SENSOR, message)
                time.sleep(1)
            
            print("\n[Step 2] 로봇 귀환 중 위치 전송")
            print("  → FastAPI 로그: MOVE_TO 커맨드 발행 확인")
            message = create_jetson_pos_message(x=5.0, y=5.0)
            send_message(TOPIC_JETSON, message)
            time.sleep(1)
            
            print("\n[Step 3] 로봇 Station 도착")
            print("  → FastAPI 로그: '🎯 ROBOT ARRIVED!' 및 CONTROL_LIGHT 확인")
            message = create_jetson_pos_message(x=0.1, y=0.1)
            send_message(TOPIC_JETSON, message)
            
            print("\n✅ 시나리오 완료!")
            print("  FastAPI 서버 로그에서 상태 변화를 확인하세요.")
            
        elif choice == 'q':
            print("\n👋 프로그램을 종료합니다.")
            break
        else:
            print("\n⚠️ 잘못된 입력입니다.")

except KeyboardInterrupt:
    print("\n\n👋 프로그램을 종료합니다.")
finally:
    client.loop_stop()
    client.disconnect()
