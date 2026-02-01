import paho.mqtt.client as mqtt
import time
import sys
from message_builder import MessageBuilder

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
TOPIC = "farmily/devices/device_1/event"
TRANSPORT = "websockets"
PATH = "/mqtt"

# 연결 성공 시 호출되는 콜백
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"\n✅ MQTT 브로커 연결 성공! ({BROKER_HOST}:{BROKER_PORT})")
    else:
        print(f"\n❌ 연결 실패 (코드: {rc})")

# 1. 클라이언트 인스턴스 생성
client = mqtt.Client(transport=TRANSPORT)

# 2. SSL/TLS 설정
client.tls_set()

# 3. 웹소켓 경로 설정
client.ws_set_options(path=PATH)

# 4. 콜백 함수 등록
client.on_connect = on_connect

# 5. 브로커 연결
print(f"🔗 {BROKER_HOST}에 연결 시도 중...")
try:
    client.connect(BROKER_HOST, BROKER_PORT, 60)
except Exception as e:
    print(f"❌ 연결 오류 발생: {e}")
    sys.exit(1)

# 6. 네트워크 루프 시작 (백그라운드)
client.loop_start()

# 메시지 빌더 인스턴스 생성
# device_id는 기기 식별자입니다.
builder = MessageBuilder(device_id="raspi_sensors")

def print_menu():
    print("\n" + "="*30)
    print("   🌱 Farmily Event Trigger   ")
    print("="*30)
    print("1. 💧 물 감지 (WATER)")
    print("2. 👋 쓰다듬기 감지 (TOUCH)")
    print("q. ❌ 종료")
    print("-" * 30)

try:
    # 연결 대기
    time.sleep(1)
    
    while True:
        print_menu()
        choice = input("선택하세요 > ").strip().lower()

        if choice == '1':
            event_type = "WATER"
            print(f"\n💧 'WATER' 이벤트 생성 중...")
        elif choice == '2':
            event_type = "TOUCH"
            print(f"\n👋 'TOUCH' 이벤트 생성 중...")
        elif choice == 'q':
            print("\n👋 프로그램을 종료합니다.")
            break
        else:
            print("\n⚠️ 잘못된 입력입니다.")
            continue

        # 메시지 생성 및 전송
        message = builder.create_event(event_type)
        info = client.publish(TOPIC, message)
        
        # 전송 확인
        info.wait_for_publish()
        if info.rc == mqtt.MQTT_ERR_SUCCESS:
            print(f"📤 전송 완료: {TOPIC}")
            print(f"📄 내용: {message}")
        else:
            print(f"❌ 전송 실패 (코드: {info.rc})")

except KeyboardInterrupt:
    print("\n\n👋 프로그램을 종료합니다.")
finally:
    client.loop_stop()
    client.disconnect()
