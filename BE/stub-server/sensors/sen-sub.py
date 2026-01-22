import paho.mqtt.client as mqtt
import time

# --- 설정 정보 ---
BROKER_HOST = "i14d101.p.ssafy.io"
BROKER_PORT = 443
TOPIC = "#"
TRANSPORT = "websockets"  # 웹소켓 방식 설정
PATH = "/mqtt"            # Nginx에서 설정한 경로

# 연결 성공 시 호출되는 콜백
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"✅ 연결 성공! (코드: {rc})")
        client.subscribe(TOPIC)
        print(f"📡 '{TOPIC}' 토픽 구독 중...")
    else:
        print(f"❌ 연결 실패 (코드: {rc})")

# 메시지를 받았을 때 호출되는 콜백
def on_message(client, userdata, msg):
    print(f"📩 메시지 수신 받았습니다!: {msg.topic} -> {msg.payload.decode()}")

# 1. 클라이언트 인스턴스 생성 (Websockets 설정 필수)
client = mqtt.Client(transport=TRANSPORT)

# 2. SSL/TLS 설정 (443 포트 접속을 위해 필수)
# 기본적으로 시스템의 CA 인증서를 사용하도록 설정
client.tls_set()

# 3. 웹소켓 경로 설정
client.ws_set_options(path=PATH)

# 4. 콜백 함수 등록
client.on_connect = on_connect
client.on_message = on_message

# 5. 브로커 연결
print(f"🔗 {BROKER_HOST}에 연결 시도 중...")
client.connect(BROKER_HOST, BROKER_PORT, 60)

# 6. 메시지 수신
print("📡 메시지 수신 대기 중... (종료하려면 Ctrl+C)")
try:
    client.loop_forever()
except KeyboardInterrupt:
    print("\n👋 프로그램을 종료합니다.")
    client.disconnect()