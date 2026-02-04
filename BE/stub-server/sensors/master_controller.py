import paho.mqtt.client as mqtt
import time
import sys
import io
from message_builder import MessageBuilder

# Windows 터미널 한글 깨짐 방지
if sys.stdout.encoding != 'utf-8':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
TRANSPORT = "websockets"
PATH = "/mqtt"

# 토픽 정의
TOPIC_EVENT = "farmily/devices/device_1/event"
TOPIC_COMMAND = "farmily/devices/device_1/command"
TOPIC_SENSOR = "farmily/raspi/sensor/all"
TOPIC_JETSON = "farmily/jetson/lidar/pos"

# --- MQTT 연결 설정 ---
def on_connect(client, userdata, flags, rc, properties=None):
    if rc == 0:
        print(f"\n✅ MQTT 브로커 연결 성공! ({BROKER_HOST}:{BROKER_PORT})")
    else:
        print(f"\n❌ 연결 실패 (코드: {rc})")

# Paho MQTT v2.0 이상 대응 (CallbackAPIVersion 명시)
try:
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, transport=TRANSPORT)
except AttributeError:
    # 이전 버전 대응
    client = mqtt.Client(transport=TRANSPORT)

client.tls_set()
client.ws_set_options(path=PATH)
client.on_connect = on_connect

print(f"🔗 {BROKER_HOST}에 연결 시도 중...")
try:
    client.connect(BROKER_HOST, BROKER_PORT, 60)
except Exception as e:
    print(f"❌ 연결 오류 발생: {e}")
    sys.exit(1)

client.loop_start()

# --- 빌더 인스턴스 ---
# 사용자가 보낸 예시 코드의 device_id 반영
builder_raspi = MessageBuilder(device_id="raspi_sensors")
builder_station = MessageBuilder(device_id="raspi_station_led")
builder_jetson = MessageBuilder(device_id="jetson_lidar")


# --- 기능 함수들 ---

def send_mqtt(topic, message, desc=""):
    info = client.publish(topic, message)
    info.wait_for_publish()
    if info.rc == mqtt.MQTT_ERR_SUCCESS:
        print(f"📤 [전송 완료] {desc}")
        print(f"📄 내용: {message}")
    else:
        print(f"❌ [전송 실패] {desc}")

def menu_events():
    while True:
        print("\n--- [1] 이벤트 발생 (Events) ---")
        print("1. 💧 물 감지 (WATER)")
        print("2. 👋 쓰다듬기 (TOUCH)")
        print("b. 🔙 뒤로가기")
        choice = input("선택 > ").strip().lower()

        if choice == '1':
            msg = builder_raspi.create_event("WATER")
            send_mqtt(TOPIC_EVENT, msg, "WATER Event")
        elif choice == '2':
            msg = builder_raspi.create_event("TOUCH")
            send_mqtt(TOPIC_EVENT, msg, "TOUCH Event")
        elif choice == 'b':
            break
        else:
            print("⚠️ 잘못된 선택")

def menu_controls():
    while True:
        print("\n--- [2] 장치 제어 (Controls) ---")
        print("1. 🌕 조명 켜기 (5초)")
        print("2. 🌑 조명 끄기")
        print("3. ⏱️ 조명 켜기 (시간 지정)")
        print("b. 🔙 뒤로가기")
        choice = input("선택 > ").strip().lower()

        if choice == '1':
            msg = builder_station.create_control_light("ON", 5)
            send_mqtt(TOPIC_COMMAND, msg, "Light ON (5s)")
        elif choice == '2':
            msg = builder_station.create_control_light("OFF", 0)
            send_mqtt(TOPIC_COMMAND, msg, "Light OFF")
        elif choice == '3':
            try:
                sec = int(input("지속 시간(초): "))
                msg = builder_station.create_control_light("ON", sec)
                send_mqtt(TOPIC_COMMAND, msg, f"Light ON ({sec}s)")
            except ValueError:
                print("⚠️ 숫자만 입력하세요")
        elif choice == 'b':
            break
        else:
            print("⚠️ 잘못된 선택")

def menu_scenarios():

    while True:

        print("\n--- [3] 시나리오 테스트 (Simulation) ---")

        print("1. 🌙 낮은 조도 데이터 전송 (500 lux)")

        print("2. ☀️ 높은 조도 데이터 전송 (10000 lux)")

        print("3. 🚗 로봇 이동 (Station과 멀리)")

        print("4. 🏠 로봇 도착 (Station 근처)")

        print("5. 🔄 시나리오 자동 실행 (낮은 조도 → 로봇 도착)")

        print("b. 🔙 뒤로가기")

        choice = input("선택 > ").strip().lower()



        if choice == '1':

            data = {"temperature": 25.0, "humidity": 60.0, "illuminance": 500.0, "soil_moisture": 45.0}

            msg = builder_raspi.create_telemetry(data)

            send_mqtt(TOPIC_SENSOR, msg, "Sensor: Low Light (500 lux)")

        elif choice == '2':

            data = {"temperature": 25.0, "humidity": 60.0, "illuminance": 10000.0, "soil_moisture": 45.0}

            msg = builder_raspi.create_telemetry(data)

            send_mqtt(TOPIC_SENSOR, msg, "Sensor: High Light (10000 lux)")

        elif choice == '3':

            data = {

                "device_id": "farmily", "x": 10.0, "y": 10.0, "theta": 0.0,

                "map_id": "ssafy_room_A", "timestamp": time.time()

            }

            msg = builder_jetson.create_telemetry(data)

            send_mqtt(TOPIC_JETSON, msg, "Robot: Far (10, 10)")

        elif choice == '4':

            data = {

                "device_id": "farmily", "x": 0.1, "y": 0.1, "theta": 0.0,

                "map_id": "ssafy_room_A", "timestamp": time.time()

            }

            msg = builder_jetson.create_telemetry(data)

            send_mqtt(TOPIC_JETSON, msg, "Robot: Arrived (0.1, 0.1)")

        elif choice == '5':

            print("\n🔄 시나리오 자동 실행 시작")

            print("="*40)

            

            # Step 1: 낮은 조도 데이터 여러 번 전송

            print("\n[Step 1] 낮은 조도 데이터 전송 (3회, 1초 간격)")

            for i in range(3):

                data = {"temperature": 25.0, "humidity": 60.0, "illuminance": 500.0, "soil_moisture": 45.0}

                msg = builder_raspi.create_telemetry(data)

                send_mqtt(TOPIC_SENSOR, msg, f"Sensor: Low Light ({i+1}/3)")

                time.sleep(1)

            

            print("\n[Step 2] 로봇 귀환 중 위치 전송")

            data = {"device_id": "farmily", "x": 5.0, "y": 5.0, "theta": 0.0, "map_id": "ssafy_room_A", "timestamp": time.time()}

            msg = builder_jetson.create_telemetry(data)

            send_mqtt(TOPIC_JETSON, msg, "Robot: Moving (5, 5)")

            time.sleep(1)

            

            print("\n[Step 3] 로봇 Station 도착")

            data = {"device_id": "farmily", "x": 0.1, "y": 0.1, "theta": 0.0, "map_id": "ssafy_room_A", "timestamp": time.time()}

            msg = builder_jetson.create_telemetry(data)

            send_mqtt(TOPIC_JETSON, msg, "Robot: Arrived (0.1, 0.1)")

            

            print("\n✅ 시나리오 완료!")



        elif choice == 'b':

            break

        else:

            print("⚠️ 잘못된 선택")

# --- 메인 루프 ---
try:
    time.sleep(1) # 연결 대기
    while True:
        print("\n" + "="*30)
        print("   🎛️  Farmily Master Controller")
        print("="*30)
        print("1. 🔥 이벤트 발생 (물, 터치)")
        print("2. 💡 장치 제어 (조명)")
        print("3. 🧪 시나리오 시뮬레이션")
        print("q. ❌ 종료")
        print("-" * 30)
        
        main_choice = input("메뉴 선택 > ").strip().lower()

        if main_choice == '1':
            menu_events()
        elif main_choice == '2':
            menu_controls()
        elif main_choice == '3':
            menu_scenarios()
        elif main_choice == 'q':
            print("\n👋 종료합니다.")
            break
        else:
            print("⚠️ 잘못된 입력입니다.")

except KeyboardInterrupt:
    print("\n\n👋 종료합니다.")
finally:
    client.loop_stop()
    client.disconnect()