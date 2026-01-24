# YDLidar X4 Pro Setup Log (Jetson Orin Nano / ROS 2 Humble)

## 1. 개요 (Overview)

- **작성일:** 2026-01-21
- **환경:** NVIDIA Jetson Orin Nano (Ubuntu 22.04 LTS)
- **ROS 버전:** ROS 2 Humble Hawksbill
- **센서:** YDLidar X4 Pro
- **목표:** YDLidar SDK 및 ROS 2 드라이버 설치, Python 바인딩 에러 해결 및 구동 테스트

---

## 2. 사전 준비 (Prerequisites)

YDLidar SDK 빌드 및 Python 바인딩을 위한 필수 패키지 설치.

```bash
sudo apt update
sudo apt install swig python3-pip git cmake
```

## 3. YDLidar SDK 설치 (YDLidar-SDK)

SDK는 C++ 라이브러리 빌드 후, 반드시 Python 바인딩까지 설치해야 함.

### 3.1 소스 다운로드 및 C++ 빌드

```Bash

# 소스 코드 다운로드
cd ~/jun_ros2_ws/src
git clone [https://github.com/YDLIDAR/YDLidar-SDK.git](https://github.com/YDLIDAR/YDLidar-SDK.git)

# 빌드 디렉토리 생성 및 컴파일
cd YDLidar-SDK
mkdir build
cd build
cmake ..
make
sudo make install
```

### 3.2 Python 바인딩 설치 (중요)

Python 스크립트에서 라이다를 제어하기 위해 필수적인 단계.

```Bash

cd ~/jun_ros2_ws/src/YDLidar-SDK/python
pip install .
```

## 4. 트러블슈팅 (Troubleshooting)

🔴 Issue: ModuleNotFoundError 발생
SDK의 예제 파일(tof_test.py) 실행 시 ydlidar 모듈을 찾지 못하는 에러 발생.

에러 로그 (Error Log):

```Plaintext

d101@d101-desktop:~/jun_ros2_ws/src/YDLidar-SDK/python/examples$ python tof_test.py
Traceback (most recent call last):
  File ".../tof_test.py", line 2, in <module>
    import ydlidar
ModuleNotFoundError: No module named 'ydlidar'
```

#### 원인 (Cause):

- C++ 라이브러리(make install)만 진행하고, Python 바인딩(pip install .) 과정을 누락함.

- 시스템의 Python 환경에 ydlidar 패키지가 등록되지 않아 import 실패.

#### 해결 (Solution):

- ~/jun_ros2_ws/src/YDLidar-SDK/python 디렉토리로 이동하여 pip install . 명령어를 실행하여 해결 완료.

## 5. ROS 2 드라이버 설치 (ydlidar_ros2_driver)

### 5.1 워크스페이스 설정 및 빌드

```Bash

# 드라이버 소스 다운로드
cd ~/jun_ros2_ws/src
git clone [https://github.com/YDLIDAR/ydlidar_ros2_driver.git](https://github.com/YDLIDAR/ydlidar_ros2_driver.git)

# 워크스페이스 빌드
cd ~/jun_ros2_ws
colcon build --symlink-install

# 환경 변수 로드
source install/setup.bash
```

### 5.2 USB 포트 권한 부여

라이다 디바이스(/dev/ttyUSB0)에 대한 읽기/쓰기 권한 설정.

```Bash

sudo chmod 777 /dev/ttyUSB0
```

## 6. 구동 테스트 (Testing)

### 6.1 드라이버 실행

ROS 2 런치 파일을 이용하여 드라이버 노드 실행.

```Bash

ros2 launch ydlidar_ros2_driver ydlidar_launch.py
```

### 6.2 데이터 시각화 (Rviz2)

센서 데이터가 정상적으로 들어오는지 시각적으로 확인.

1. 터미널에서 rviz2 실행.

2. Fixed Frame을 laser_frame으로 변경.

3. 좌측 하단 Add 버튼 클릭 -> LaserScan 선택.

4. Topic을 /scan으로 설정.

5. 화면에 붉은색 포인트 클라우드(라이다 스캔 데이터)가 정상 출력되는지 확인.
