# 🚗 Jetson Orin Nano 자율주행 로봇 개발 로그
**Date:** 2026-02-07

**Hardware:** Jetson Orin Nano, YDLidar, Ackermann Steering Chassis (DC Motor + Servo)

**Software:** ROS 2 Humble, Nav2, SLAM Toolbox, RF2O Laser Odometry

---

## 1. SLAM 및 하드웨어 통합 (Mapping)
기존에 따로 놀던 라이다, 오도메트리, 모터 제어를 하나의 런치 파일로 통합하여 지도를 작성함.

### 🛠️ 주요 작업 내용
* **파일 생성:** `robot_mapping.launch.py`
* **노드 구성:**
    1.  `ydlidar_ros2_driver`: 라이다 센서 구동
    2.  `static_transform_publisher`: 로봇 몸체(`base_link`)와 라이다(`laser_frame`) 간의 TF 연결 (0.1m 상단)
    3.  `ackermann_controller`: 모터 및 조향 제어 (cmd_vel 수신)
    4.  `rf2o_laser_odometry`: 휠 엔코더 대신 라이다 스캔 매칭으로 위치 추정 (Odom)
    5.  `slam_toolbox`: 지도 작성 알고리즘

### 💡 핵심 트러블슈팅
* **문제:** `rf2o`가 라이다 데이터를 받지 못해 "Waiting for laser_scan" 상태 지속.
* **원인:** QoS 설정 불일치 (Lidar는 Best Effort, RF2O는 Reliable).
* **해결:** `rf2o` 파라미터에 `reliability: best_effort` 추가 및 `freq: 10.0` 설정.

---

## 2. 지도 저장 (Map Saving)
작성된 지도를 파일로 저장하여 내비게이션에서 사용할 수 있도록 함.

### 💻 명령어
```bash
# map_saver_cli 사용
ros2 run nav2_map_server map_saver_cli -f ~/map/my_home
```

- 결과물: my_home.pgm (이미지), my_home.yaml (메타데이터)

---

## 3. 자율주행 시스템 구축 (Navigation)
저장된 지도를 불러오고, 목적지까지 경로를 생성하여 주행하는 시스템 구축.

### 🛠️ 주요 작업 내용
- 파일 생성: navigation.launch.py

- 설정 파일: my_nav2_params.yaml (Nav2 기본 설정 커스터마이징)

- QoS 문제 해결: nav2_map_server와 amcl이 지도를 읽지 못하는 문제 해결을 위해 Transient Local 설정 확인.

### 💡 핵심 트러블슈팅 (Global Status Error)
- 문제: RViz에서 "Global Status: Error (Frame [map] does not exist)" 발생 및 AMCL 비활성화.

- 원인:

  - use_sim_time: True 설정으로 인해 실제 로봇 시간과 동기화 실패.

  - base_footprint TF 요구 (실제 로봇은 base_link 사용).

  - Lidar QoS 불일치.

- 해결 (my_nav2_params.yaml 수정):

  - use_sim_time: False (전체 변경)

  - robot_base_frame: base_link (전체 변경)

  - amcl에 reliability_policy: best_effort 추가

## 4. 주행 성능 및 하드웨어 튜닝 (Tuning)
로봇이 너무 느리거나 움직이지 않는 하드웨어 특성을 반영하여 파라미터 최적화.

### 🏎️ 속도 및 제어 튜닝
- 문제: 하드웨어 모터 토크 부족으로 저속(0.6 이하)에서 구동 불가, 기본 설정 속도(0.26)가 너무 느림.

- 해결 (FollowPath & velocity_smoother):

  - min_vel_x: 0.7 (최소 구동 가능 속도로 강제)

  - max_vel_x: 1.0 (최대 속도 해제)

  - max_speed_xy: 1.0

### 🎯 도착 판정 튜닝
- 문제: 높은 최소 속도(0.7)로 인해 목표 지점 근처에서 정지하지 못하고 진동(Oscillation) 발생.

- 해결 (general_goal_checker):

  - xy_goal_tolerance: 0.4 (40cm 이내 도착 시 정지 인정)

  - yaw_goal_tolerance: 0.5 (각도 오차 허용 범위 확대)

### 📐 회피 기동 튜닝
- 문제: footprint 좌표 설정 시 오류 발생.

- 해결 (local/global_costmap):

  - 복잡한 footprint 대신 robot_radius: 0.3 (30cm)로 설정하여 안전 마진 확보 및 계산 부하 감소.

---

## 5. 실행 및 운영 가이드

### 🚀 실행 명령어

1. 터미널 환경 설정 (~/.bashrc 등록 완료):

```Bash
source ~/hyup_ros2_ws/install/setup.bash
```

2. 자율주행 시작:

```Bash
ros2 launch my_robot_nav navigation.launch.py
```

### 🎮 RViz 조작 순서
1. 2D Pose Estimate: 지도상에서 로봇의 현재 위치와 방향을 드래그하여 초기화.

2. Nav2 Goal: 가고 싶은 목적지를 클릭하여 자율주행 시작.

---

## 🎉 결론
Jetson Orin Nano와 ROS 2 Humble 환경에서 Lidar 기반의 SLAM과 Navigation 시스템 구축을 완료함. 하드웨어의 물리적 한계(모터 출력)를 소프트웨어 파라미터 튜닝으로 극복하고, 실제 환경에서 주행 가능한 자율주행 로봇을 구현함.