# YDLIDAR X4 Pro & ROS 2 Humble 구축 및 트러블슈팅 완벽 가이드

---

```
작성 일자: 2026-01-22

환경: NVIDIA Jetson Orin Nano (Ubuntu 22.04 LTS)
OS/Framework: ROS 2 Humble Hawksbill
하드웨어: YDLIDAR X4 Pro (USB 연결)
```

## 1. 개요 (Overview)

- 이 문서는 Jetson Orin Nano 환경에서 YDLIDAR X4 Pro를 구동하기 위한 전체 과정을 정리한 문서입니다. SDK 설치부터 ROS 2 드라이버 빌드, 그리고 과정 중 발생했던 주요 에러(Python 바인딩, 파라미터 불일치 등)의 원인과 해결 방법을 상세히 기록했습니다.

## 2. 사전 준비 (Prerequisites)

- SDK와 ROS 2 드라이버 빌드를 위해 필요한 시스템 패키지를 설치합니다.

  ```bash
  sudo apt update
  sudo apt install swig python3-pip git cmake pkg-config
  ```

- ROS 2 빌드 도구인 colcon이 없다면 설치합니다. (Jetson 환경 추천 방식)

  ```bash
  sudo apt install -y python3-pip
  pip3 install -U colcon-common-extensions
  ```

## 3. YDLidar SDK 설치 및 검증

- 하드웨어와 통신하기 위한 C++ 기반 SDK를 먼저 설치합니다.

### 3.1 SDK 다운로드 및 빌드

```bash
# 소스 다운로드 (워크스페이스 src 폴더 등 원하는 곳에서)
git clone [https://github.com/YDLIDAR/YDLidar-SDK.git](https://github.com/YDLIDAR/YDLidar-SDK.git)
cd YDLidar-SDK

# 빌드 디렉토리 생성
mkdir build
cd build

# 컴파일 및 설치
cmake ..
make -j4
sudo make install
```

### 3.2 🚨 트러블슈팅 1: Python 모듈 에러 (ModuleNotFoundError)

- SDK 설치 직후 Python 예제(tof_test.py) 실행 시 모듈을 찾을 수 없다는 에러가 발생했습니다.

  ```bash
  에러 로그:

  Traceback (most recent call last):
    File "tof_test.py", line 2, in <module>
      import ydlidar
  ModuleNotFoundError: No module named 'ydlidar'
  ```

- 원인: make install은 C++ 라이브러리만 설치하며, Python 바인딩은 별도로 설치해야 합니다.

- 해결 방법:

  ```bash
  # SDK 폴더 내의 python 디렉토리로 이동
  cd ~/ros2_ws/src/YDLidar-SDK/python

  # Python 패키지 설치
  pip install .
  ```

### 3.3 🚨 트러블슈팅 2: SDK 자체 테스트 실패 (Health Code -1)

- SDK의 tri_test 도구를 사용하여 하드웨어 연결을 테스트했으나 실패했습니다.

  ```bash
  에러 로그:

  Error, cannot retrieve Lidar health code -1
  Fail to get baseplate device information!
  Failed to start scan mode -1
  ```

- 원인 및 해결:

  ```bash
  권한 문제: USB 포트 권한 부여

  sudo chmod 666 /dev/ttyUSB0


  Baudrate 설정 (핵심): 기본 예제 코드가 다른 모델 기준이었습니다. X4 Pro는 128000을 사용해야 합니다.
  ```

#### 올바른 테스트 명령어

```bash
./tri_test --port /dev/ttyUSB0 --baudrate 128000
```

## 4. ROS 2 드라이버 설치 및 빌드

### 4.1 드라이버 클론 및 빌드

```bash
cd ~/ros2_ws/src
git clone [https://github.com/YDLIDAR/ydlidar_ros2_driver.git](https://github.com/YDLIDAR/ydlidar_ros2_driver.git)

cd ~/ros2_ws
colcon build --symlink-install
```

### 4.2 환경 설정

```bash
source install/setup.bash
```

## 5. ROS 2 실행 및 트러블슈팅 (최종 난관)

- SDK 테스트 성공 후, ROS 2 런치 파일을 실행했으나 다시 에러가 발생했습니다.

### 5.1 🚨 문제 상황: 스캔 모드 진입 실패

```bash
ros2 launch ydlidar_ros2_driver ydlidar_launch.py
```

- 발생한 로그:

  ```bash
  Lidar successfully connected [/dev/ttyUSB0:128000]
  Error, cannot retrieve Lidar health code -1
  Failed to start scan mode -1
  ```

- 증상: "Connected" 메시지는 뜨지만, 실제 스캔 데이터가 들어오지 않고 종료됨.

### 5.2 원인 분석: YAML 파라미터 불일치

- 기본 제공되는 ydlidar_launch.py는 X4 Pro 모델과 맞지 않는 기본 파라미터를 사용하고 있었습니다.

- Baudrate: 230400 (X4 Pro는 128000 필요)

- Channel: isSingleChannel 등의 옵션 설정 오류

### 5.3 ✅ 해결: X4 Pro 전용 파라미터 적용

- ydlidar_ros2_driver/param/ 경로에 있는 모델별 파라미터 중 X4 Pro 전용 설정을 확인하여 적용했습니다.

- 최종 적용된 ydlidar.yaml 내용:

  ```bash
  ydlidar_ros2_driver_node:
    ros__parameters:
      port: /dev/ttyUSB0
      frame_id: laser_frame
      ignore_array: ""
      baudrate: 128000        # X4 Pro 필수값
      lidar_type: 1
      device_type: 0
      sample_rate: 5
      abnormal_check_count: 4
      fixed_resolution: true
      reversion: true         # 중요
      inverted: true          # 중요
      auto_reconnect: true
      isSingleChannel: true   # 중요
      intensity: false
      support_motor_dtr: false
      angle_max: 180.0
      angle_min: -180.0
      range_max: 12.0
      range_min: 0.1
      frequency: 10.0
      invalid_range_is_inf: false
  ```

- 이 설정을 적용하자 로그가 정상(Lidar running correctly!)으로 출력되었습니다.

## 6. 최종 결과: RViz2 시각화

- 드라이버 정상 구동 후 데이터를 시각화하는 과정입니다.

### 6.1 실행 명령어

- 터미널 1 (드라이버 실행):

  ```bash
  ros2 launch ydlidar_ros2_driver ydlidar_launch.py
  ```

- 터미널 2 (RViz2 실행):
  ```bash
  rviz2
  ```

### 6.2 RViz2 설정 순서

1.  Fixed Frame 변경: map → laser_frame

2.  Add Display: 좌측 하단 Add 버튼 클릭 → LaserScan 선택

3.  Topic 설정: LaserScan 메뉴 하위의 Topic을 /scan으로 선택

4.  결과: 화면 중앙을 기준으로 붉은색 포인트 클라우드(360도 스캔 데이터)가 실시간으로 표시됨을 확인.

## 7. 핵심 요약 (Lessons Learned)

- Python 바인딩: SDK 설치 시 make install 외에 pip install . 과정이 필수입니다.

- 모델별 설정: YDLIDAR는 모델(X2, X4, G4 등)마다 Baudrate와 Channel 설정이 다르므로, 반드시 해당 모델의 YAML 파일을 사용해야 합니다.

- 연결 vs 데이터: 포트 연결이 성공했다고 해서 데이터가 들어오는 것은 아닙니다. Scan mode 에러는 대부분 파라미터 설정 문제입니다.
