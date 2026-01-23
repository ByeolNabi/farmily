# MQTT Protocol Test Guide

이 프로젝트는 MQTT 프로토콜을 사용하여 센서(Publisher)와 클라이언트(Subscriber: Android, Unity), 그리고 FastAPI 서버 간의 데이터 송수신을 테스트하기 위한 Mock 환경입니다.

## 1. 사전 준비 (Prerequisites)

*   **Python 3.10+**
*   **Mosquitto MQTT Broker** (로컬 설치 필요)
    *   Linux: `sudo apt install mosquitto`
    *   Mac: `brew install mosquitto`
    *   Windows: [Download Installer](https://mosquitto.org/download/)

## 2. 환경 설정

필요한 파이썬 라이브러리를 설치합니다.

```bash
pip install -r requirements.txt
```

## 3. MQTT Broker 실행

테스트를 위해 익명 접속을 허용하는 설정으로 브로커를 실행합니다.

**방법 A: 간단 실행 (설정 파일 없이)**
보안 설정 없이 로컬에서만 테스트할 경우 (Mosquitto 버전에 따라 외부 접속이 제한될 수 있음)
```bash
mosquitto
```

**방법 B: 설정 파일 사용 (권장)**
`mosquitto/config/mosquitto.conf` 파일이 생성되어 있다면 아래 명령어로 실행합니다. (외부 접속 및 익명 접속 허용됨)
```bash
mosquitto -c mosquitto/config/mosquitto.conf
```
*참고: `-d` 옵션을 추가하면 백그라운드에서 실행됩니다.*

## 4. 테스트 실행 (터미널 분할 권장)

각 구성 요소를 별도의 터미널에서 실행하여 로그를 실시간으로 확인하는 것이 좋습니다.

### Step 1: Subscribers (데이터 받는 쪽) 실행

먼저 데이터를 기다리는 클라이언트들을 실행합니다.

**Android Mock (거실의 모든 센서 데이터 수신)**
```bash
python3 clients/mock_android.py
```

**Unity Mock (조도 데이터만 수신)**
```bash
python3 clients/mock_unity.py
```

**FastAPI Server (백엔드)**
서버도 MQTT에 연결되어 센서 데이터를 로깅하고, 클라이언트에 명령을 보낼 수 있습니다.
```bash
uvicorn backend.main:app --reload --port 8000
```

### Step 2: Publishers (데이터 보내는 쪽) 실행

센서 시뮬레이터를 실행하여 가짜 데이터를 1초마다 전송합니다.

**습도 센서**
```bash
python3 sensors/humidity_sensor.py
```

**조도 센서**
```bash
python3 sensors/lux_sensor.py
```

**온도 센서**
```bash
python3 sensors/temp_sensor.py
```

## 5. 검증 방법

1.  **데이터 수신 확인**:
    *   `mock_android.py` 터미널에 습도, 조도, 온도 데이터가 모두 출력되는지 확인합니다.
    *   `mock_unity.py` 터미널에 조도(Lux) 데이터만 출력되는지 확인합니다.
    *   FastAPI 터미널 로그에 센서 데이터가 수신되는지 확인합니다.

2.  **FastAPI -> Client 명령 전송 (제어 테스트)**:
    *   FastAPI가 클라이언트(Android/Unity)에게 명령을 내리는 시나리오입니다.
    *   새로운 터미널을 열고 아래 명령어를 입력합니다:
    ```bash
    curl -X POST "http://localhost:8000/api/command/TURN_ON_LIGHT"
    ```
    *   **결과**: `mock_android.py`와 `mock_unity.py` (혹은 구독 중인 모든 클라이언트) 터미널에 `Server Command: TURN_ON_LIGHT` 메시지가 수신되는지 확인합니다.

## 6. 종료 방법

각 터미널에서 `Ctrl + C`를 눌러 프로세스를 종료합니다.
백그라운드로 실행된 Mosquitto는 `pkill mosquitto` 또는 `killall mosquitto`로 종료할 수 있습니다.
