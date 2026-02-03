## 개요

라즈베리파이에서 온습도(DHT), 조도(BH1750), 토양수분(MCP3008 + 토양센서)을 측정하여 MQTT(WebSocket + TLS) 방식으로 서버에 주기적으로 전송하는 센서 수집기

## 구성 센서

- **DHT**
  - 온도 / 습도 측정

- **BH1750**
  - 조도(lux) 측정

- **MCP3008(ADC) + 토양센서**
  - 토양 아날로그 값을 10비트(0~1023)로 측정

## MQTT 전송 정보
- telemetry JSON 형태로 센서 데이터 전송

## 사용 하드웨어

- **Raspberry Pi 5**
- **DHT11 또는 DHT22** (GPIO 입력)
- **BH1750** (I2C)
- **MCP3008** (SPI ADC)
- **토양 수분 센서** (AO 아날로그 출력)

## 통신 방식 요약

- **I2C**
- BH1750 조도 센서 읽기

- **SPI**
- MCP3008을 통해 토양 아날로그 값 읽기

- **GPIO**
- DHT11 / DHT22 온습도 센서

- **MQTT over WebSocket + TLS**
- 센서 데이터 서버 전송

## 파이썬 패키지 및 import 설명

### 표준 라이브러리

- **time**
  - 주기 실행을 위한 `sleep` 사용

- **json**
  - MQTT 메시지 JSON 직렬화

- **uuid**
  - 메시지마다 고유 `msg_id` 생성

- **datetime**
  - ISO 형식 timestamp 기록

### 외부 라이브러리

- **smbus2**
  - I2C 버스 제어 라이브러리
  - BH1750 센서 통신에 사용

- **paho-mqtt**
  - MQTT 클라이언트 라이브러리
  - WebSocket 기반 연결 + TLS 적용

- **board**
  - GPIO 핀을 보드 추상화로 제어하기 위한 모듈

- **adafruit_dht**
  - DHT11 / DHT22 센서 제어
  - 타이밍 이슈로 예외 처리 및 last 값 유지 필요

- **spidev**
  - SPI 디바이스(`/dev/spidevX.Y`) 직접 제어
  - MCP3008 ADC 통신에 사용
  - Pi 5 환경에서 gpiozero 대비 안정성 우수

## 설치 방법

### 1. 가상환경 생성 및 활성화

```bash
python3 -m venv venv
source venv/bin/activate
```

## 패키지 설치

### 파이썬 패키지 설치

```bash
pip install smbus2 paho-mqtt adafruit-circuitpython-dht spidev
pip install Adafruit-Blinka
```
### 필요 시 시스템 패키지 추가
```bash
sudo apt update
sudo apt install -y libgpiod2
```
## 라즈베리파이 초기 설정
### I2C 활성화

```bash
sudo raspi-config
Interface Options → I2C → Enable
```
- 확인 : ls /dev/i2c-*

### SPI 활성화
```bash
sudo raspi-config
Interface Options → SPI → Enable
```
- 확인 : ls /dev/spidev*

### 임시 모듈 로드(spidev 에러 발생 시)
```bash
sudo modprobe spi-bcm2835
sudo modprobe spidev
```

```bash
echo -e "spi-bcm2835\nspidev" | sudo tee /etc/modules-load.d/spi-force.conf
```
- 부팅 시 자동 로드

## 배선 가이드

### BH1750 (I2C)

- **VCC** → 3.3V
- **GND** → GND
- **SDA** → GPIO2 (물리 3)
- **SCL** → GPIO3 (물리 5)

---

### DHT11 / DHT22

- **VCC** → 3.3V
- **GND** → GND
- **DATA** → GPIO17 (물리 11)

---

### MCP3008 (SPI0)

- **VDD(16), VREF(15)** → 3.3V
- **AGND(14), DGND(9)** → GND
- **CLK(13)** → GPIO11 (물리 23)
- **DOUT(12)** → GPIO9 (물리 21)
- **DIN(11)** → GPIO10 (물리 19)
- **CS/SHDN(10)** → GPIO8 (물리 24)

---

### 토양 수분 센서

- **AO** → MCP3008 CH0
- **VCC** → 3.3V
- **GND** → GND

---

## 🧠 코드 동작 설명

### DHT 센서 처리

- DHT 센서는 간헐적으로 읽기 실패가 발생함
- 예외 발생 시 마지막 정상 값(last 값)을 유지하도록 처리

---

### BH1750 조도 측정

- 연속 고해상도 모드 사용
- 측정된 raw 값을 아래 식으로 lux 환산

```text
lux = raw / 1.2
```

### MCP3008 토양 값 읽기

```python
r = spi.xfer2([1, (8 + ch) << 4, 0])
soil_raw = ((r[1] & 3) << 8) | r[2]
```
- SPI 통신을 통해 MCP3008에서 10비트 ADC 값을 읽음
- 반환되는 데이터에서 유효한 10비트만 조합하여 값 생성
- 측정 범위: 0 ~ 1023

---

### 토양값 방향 반전
```python
soil_moisture = 1023 - soil_raw
```
- 토양 수분 센서는 젖을수록 raw 값이 작아지는 특성이 있음
- 데이터 해석을 직관적으로 만들기 위해 값을 반전

---

## MQTT 메시지 형식

### 메시지 구조
```json
{
  "header": {
    "msg_id": "uuid",
    "type": "telemetry",
    "device_id": "raspi_sensors",
    "timestamp": "ISO8601"
  },
  "payload" : {
    "temperature": temp,
    "humidity": hum,
    "illuminance": lux,
    "soil": soil
  },
}
```
### 트러블슈팅
- 토양값이 1023으로 고정됨
- /dev/spidev0.0 존재 여부 확인
- MCP3008 VREF / VDD = 3.3V 연결 확인
- AGND / DGND 모두 GND 연결 확인
- SPI 배선 (MOSI / MISO / CLK / CS) 재확인 
```bash
spidev 패키지 문제 발생 시

sudo modprobe spi-bcm2835
sudo modprobe spidev
```
### 실행 방법
```bash
source venv/bin/activate
python3 sensors_mqtt.py
```

