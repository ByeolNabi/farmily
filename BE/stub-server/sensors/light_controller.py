import paho.mqtt.client as mqtt
import time
import sys
from message_builder import MessageBuilder

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
# 조명 제어 명령을 보낼 토픽
TOPIC = "farmily/devices/device_1/command"
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
# device_id를 "raspi_station"으로 설정하여 헤더에 포함
builder = MessageBuilder(device_id="raspi_station")

def print_menu():
    print("\n" + "="*30)
    print("   💡 Farmily Light Controller")
    print("="*30)
    print("1. 🌕 조명 켜기 (5초)")
    print("2. 🌑 조명 끄기")
    print("3. ⏱️ 사용자 지정 시간 켜기")
    print("q. ❌ 종료")
    print("-" * 30)

try:
    # 연결 대기
    time.sleep(1)
    
    while True:
        print_menu()
        choice = input("선택하세요 > ").strip().lower()
        
        state = None
        duration = None

        if choice == '1':
            state = "ON"
            duration = 5
            print(f"\n🌕 조명을 5초간 켭니다...")
            
        elif choice == '2':
            state = "OFF"
            # 끄는 경우 duration은 보통 0이거나 무시됨
            duration = 0
            print(f"\n🌑 조명을 끕니다...")
            
        elif choice == '3':
            try:
                sec_str = input("지속 시간(초)을 입력하세요: ")
                duration = int(sec_str)
                state = "ON"
                print(f"\n⏱️ 조명을 {duration}초간 켭니다...")
            except ValueError:
                print("⚠️ 올바른 숫자를 입력해주세요.")
                continue

        elif choice == 'q':
            print("\n👋 프로그램을 종료합니다.")
            break
        else:
            print("\n⚠️ 잘못된 입력입니다.")
            continue

        if state:
            # 메시지 생성 및 전송
            message = builder.create_control_light(state, duration_sec=duration)
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
