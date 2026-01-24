# YDLIDAR SDK & ROS2 Driver 설치 및 트러블슈팅 정리

> **목표**
> YDLIDAR SDK를 빌드하고, ROS2 환경에서 YDLIDAR ROS2 드라이버를 실행하기까지의 전체 과정을 정리한다.
> (환경: Ubuntu / Jetson, ROS2 사용)

---

## 0. 진행 상황

### 26.01.20 - YDLidar SDK 와 Ros2 드라이버 설치 중 Ros2 환경 구성이 안되어 여러가지 에러 발생

---

## 1. 전체 구조 이해

### 빌드 도구 관계

```
CMakeLists.txt  → 패키지(프로젝트) 단위 빌드 설정
cmake / make    → 실제 컴파일
colcon          → ROS2 워크스페이스 전체 빌드 도구
```

* **CMakeLists.txt**: Makefile을 생성하는 설계도
* **cmake**: Makefile 생성
* **make**: 실제 컴파일
* **colcon**: ROS2 전용 상위 빌드 도구 (여러 패키지 관리)

---

## 2. YDLIDAR SDK 설치

### 2.1 SDK 다운로드

```bash
git clone https://github.com/YDLIDAR/YDLidar-SDK.git
cd YDLidar-SDK
```

### 2.2 SDK 빌드

```bash
mkdir build
cd build
cmake ..
make -j4
sudo make install
```

기본 설치 위치:

* 헤더: `/usr/local/include/CYdLidar.h`
* 라이브러리: `/usr/local/lib/libydlidar_sdk.so`

---

## 3. SDK 테스트 (tri_test)

### 3.1 실행

```bash
cd build
./tri_test
```

### 3.2 발생한 에러 로그

```text
Error, cannot retrieve Lidar health code -1
Fail to get baseplate device information!
Failed to start scan mode -1
```

### 3.3 에러 원인 분석

| 항목       | 상태 |
| -------- | -- |
| SDK 초기화  | 정상 |
| USB 연결   | 정상 |
| 포트 열림    | 정상 |
| Baudrate | 정상 |
| LiDAR 응답 | 실패 |

**결론**: 빌드 문제 ❌ / 통신·환경 문제 ⭕

---

## 4. tri_test 에러 주요 원인

### 4.1 시리얼 포트 권한

```bash
ls -l /dev/ttyUSB0
sudo usermod -aG dialout $USER
# 로그아웃 후 재로그인
```

### 4.2 포트 확인

```bash
ls /dev/ttyUSB*
dmesg | grep tty
```

### 4.3 Baudrate 확인 (모델별)

| 모델          | Baudrate |
| ----------- | -------- |
| X2          | 115200   |
| X4 / X4-Pro | 128000   |
| G2          | 230400   |
| G4          | 512000   |

```bash
./tri_test --port /dev/ttyUSB0 --baudrate 128000
```

### 4.4 LiDAR 모델 지정 (중요)

```bash
./tri_test --help
./tri_test --port /dev/ttyUSB0 --baudrate 128000 --type x4
```

### 4.5 전원 문제 (Jetson에서 매우 흔함)

* 모터 안 돌면 전원 부족
* USB 허브(외부 전원) 사용 권장

---

## 5. ROS2 환경 준비

### 5.1 ROS2 설치 확인

```bash
ls /opt/ros
```

예:

* `/opt/ros/humble`

### 5.2 ROS2 환경 source

```bash
source /opt/ros/humble/setup.bash
```

(매번 귀찮으면 `.bashrc`에 추가)

---

## 6. colcon 설치 문제 해결

### 6.1 문제 상황

```text
colcon: command not found
E: Unable to locate package python3-colcon-common-extensions
```

### 6.2 universe 저장소 활성화

```bash
sudo add-apt-repository universe
sudo apt update
```

### 6.3 APT 설치 (가능한 경우)

```bash
sudo apt install python3-colcon-common-extensions
```

### 6.4 pip 설치 (Jetson 최종 해결책)

```bash
sudo apt install -y python3-pip
pip3 install -U colcon-common-extensions
```

PATH 추가:

```bash
echo 'export PATH=$HOME/.local/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

확인:

```bash
colcon --version
```

---

## 7. ROS2 워크스페이스 구성

```text
ros2_ws/
├── src/
│   └── ydlidar_ros2_driver/
├── build/
├── install/
└── log/
```

```bash
mkdir -p ~/ros2_ws/src
cd ~/ros2_ws/src
```

---

## 8. YDLIDAR ROS2 Driver 빌드

```bash
cd ~/ros2_ws
colcon build --symlink-install
```

빌드 후 반드시:

```bash
source install/setup.bash
```

---

## 9. 핵심 개념 정리

### CMake vs colcon

| 항목             | 역할          |
| -------------- | ----------- |
| CMakeLists.txt | 패키지 빌드 규칙   |
| cmake          | Makefile 생성 |
| make           | 컴파일         |
| colcon         | ROS2 전체 빌드  |

### --symlink-install 의미

* install 폴더에 복사 ❌
* install 폴더에 심볼릭 링크 ⭕
* 개발 중 수정 즉시 반영

---

## 10. 오늘 배운 핵심 교훈

1. **SDK 빌드는 성공했는데 실행 안 되면 → 하드웨어/환경 문제**
2. health code -1 = LiDAR가 응답 안 함
3. Jetson에서는 **전원 + baudrate + 모델 설정**이 핵심
4. colcon 에러의 90%는 저장소 또는 PATH 문제
5. ROS2 개발은 `colcon build --symlink-install`이 기본

---

## 11. 다음 단계

* YDLIDAR ROS2 노드 실행 (`ros2 run / ros2 launch`)
* `/scan` 토픽 확인
* RViz2 시각화
* 실제 자율주행/센서 융합 연동

---

> **주제:** YDLIDAR SDK & ROS2 Driver 설치/디버깅
