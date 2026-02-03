# [ROS 2] YDLidar X4 Pro로 SLAM Mapping 구현하기 (엔코더 없이 맵핑)

**날짜:** 2026-01-27  
**카테고리:** ROS 2 / SLAM / Embedded  
**환경:** Jetson Orin Nano, Ubuntu 22.04 (Jammy), ROS 2 Humble  
**하드웨어:** YDLidar X4 Pro (Serial)

---

## 1. 개요 (Overview)

오늘은 Jetson Orin Nano와 YDLidar X4 Pro를 연동하여 **SLAM(Simultaneous Localization and Mapping)**을 구현했다.
로봇 베이스(Chassis)에 바퀴 엔코더(Encoder)가 없는 상황이라 위치 추정(Odometry)에 어려움이 있었지만, **`rf2o_laser_odometry`** 패키지를 사용하여 라이다 데이터만으로 오도메트리를 생성하고 **SLAM Toolbox**를 통해 지도를 그리는 데 성공했다.

---

## 2. 주요 개념 정리 (Concept)

### 2.1 오도메트리 (Odometry)의 중요성

SLAM이 지도를 그리려면 두 가지 정보가 필요하다.

1. **LaserScan:** 벽이 어디에 있는가? (Lidar)
2. **Odometry:** 내가(로봇이) 어디로 움직였는가? (Encoder/IMU)

보통은 바퀴 회전수(Encoder)로 이동 거리를 계산하지만, 현재 하드웨어에는 엔코더가 없다.
엔코더 없이 `Static TF`로 좌표를 고정하면, 로봇이 움직여도 컴퓨터는 "제자리에 있다"고 판단하여 **맵이 업데이트되지 않는 문제**가 발생한다.

### 2.2 해결책: rf2o_laser_odometry

- **개념:** 라이다 스캔 데이터의 변화(매칭)를 계산하여 로봇의 이동량(Odometry)을 추측하는 패키지.
- **장점:** 엔코더가 없어도 SLAM 가능.
- **단점:** 빠른 회전이나 민무늬 벽에서는 위치를 놓칠 수 있음.

### 2.3 로봇청소기의 원리 (Sensor Fusion)

정밀한 지도를 만들기 위해서는 **[바퀴 엔코더 + IMU(관성센서) + 라이다]** 3가지 센서를 융합(Sensor Fusion, EKF)해야 한다는 것을 배웠다.

---

## 3. 구현 과정 (Implementation)

### Step 1: rf2o_laser_odometry 설치

라이다 기반 오도메트리 패키지를 소스 빌드로 설치했다.

```bash
cd ~/ros2_ws/src
git clone [https://github.com/MAPIRlab/rf2o_laser_odometry.git](https://github.com/MAPIRlab/rf2o_laser_odometry.git)
cd ~/ros2_ws
colcon build --packages-select rf2o_laser_odometry
source install/setup.bash
```

### Step 2: 파라미터 튜닝 (my_slam_params.yaml)

엔코더가 없는 환경에서 노이즈를 줄이기 위해 slam_toolbox 파라미터를 수정했다.

- minimum_travel_distance: 0.15 (확실히 움직였을 때만 맵 갱신)

- map_resolution: 0.05

- loop_match_minimum_response_coarse: 0.40 (위치 보정 강화)

### Step 3: 통합 런치 파일 작성 (sllidar_slam.launch.py)

터미널을 4개씩 띄우는 번거로움을 없애기 위해, Python Launch 파일 하나로 모든 노드를 실행하도록 구성했다.

- 구성 노드: YDLidar Driver + Static TF (Robot->Laser) + RF2O (Odom 생성) + SLAM Toolbox + RViz2

```Python
# Launch 파일 핵심 부분 (rf2o 설정)
Node(
    package='rf2o_laser_odometry',
    executable='rf2o_laser_odometry_node',
    name='rf2o_laser_odometry',
    output='screen',
    parameters=[{
        'laser_scan_topic': '/scan',
        'odom_topic': '/odom',
        'publish_tf': True,
        'base_frame_id': 'base_link',
        'odom_frame_id': 'odom',
        'init_pose_from_topic': '',
        'freq': 20.0  # X4 Pro의 반응성을 위해 주파수 상향
    }],
)
```

### Step 4: 맵 저장 (Map Saving)

SLAM으로 그려진 지도를 추후 Nav2(자율주행)에서 사용하기 위해 파일로 저장했다.

1. 맵 세이버 패키지 설치

```Bash
sudo apt install ros-humble-nav2-map-server
```

2. 저장 명령어 실행 SLAM이 실행 중인 상태에서 새 터미널을 열고, maps 디렉토리를 생성하여 저장했다.

```Bash
mkdir maps
cd maps
# 사용법: ros2 run nav2_map_server map_saver_cli -f [파일명]
ros2 run nav2_map_server map_saver_cli -f my_room_map
```

3. 결과 파일

- my_room_map.pgm: 지도의 실제 이미지 파일 (Occupancy Grid).

- my_room_map.yaml: 지도의 해상도, 원점 좌표 등을 담은 메타데이터 파일.

## 4. 트러블슈팅 (Troubleshooting)

Issue 1: 맵이 처음에만 뜨고 업데이트가 안 됨

- 원인: static_transform_publisher로 odom -> base_link를 연결했더니, 로봇 좌표가 (0,0,0)으로 고정됨. SLAM 노드는 로봇이 움직이지 않았다고 판단하여 맵을 갱신하지 않음.

- 해결: Static TF를 제거하고 rf2o_laser_odometry를 도입하여 실제 움직임을 odom 좌표계에 반영함.

Issue 2: "package 'rf2o_laser_odometry' not found" 에러

- 원인: 패키지 빌드(colcon build) 후 환경 변수 갱신(source)을 하지 않고 런치 파일을 실행함.

- 해결: source install/setup.bash 명령어로 워크스페이스 오버레이 적용.

Issue 3: Python Launch 파일 실행 시 무반응

- 원인: generate_launch_description 함수만 정의하고, 실제 실행 진입점(if **name** == '**main**': ...)을 작성하지 않음. ros2 launch 명령어가 아닌 python3 명령어로 실행해서 발생.

- 해결: LaunchService를 호출하는 메인 실행 블록 추가.

Issue 4: 맵이 겹치고 지저분하게 그려짐 (Ghosting)

- 원인: 엔코더 없이 라이다로만 위치를 추정하다 보니, 빠른 회전 시 위치를 놓침.

- 해결:
  1. 주행 습관 교정 (천천히 회전, 주기적 정지).

  2. SLAM 파라미터 튜닝 (노이즈 필터링 강화).

  3. rf2o 주파수(freq)를 20.0Hz로 상향.

## 5. 결과 및 회고 (Retrospective)

결과

- RViz2 상에서 Fixed Frame: map 설정 후, 로봇 이동에 따라 실시간으로 확장되는 지도를 확인했다.

- 엔코더 없이도 제한적인 상황에서 SLAM이 가능하다는 것을 검증했다.

앞으로의 과제

- IMU 센서 추가: 회전 시 위치를 놓치는 문제를 해결하기 위해 MPU6050 등의 IMU 센서를 부착하여 rf2o 또는 robot_localization에 융합할 예정.

- Nav2 연동: 만들어진 맵을 저장(map_saver)하고, 자율 주행(Navigation2) 스택을 올려 목적지 이동 구현하기.
