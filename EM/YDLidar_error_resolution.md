# YDLIDAR X4 Pro ROS2 센서 테스트 및 에러 해결 기록

> 작업 날짜: 2026-01-22
> 작업 환경: Ubuntu 22.04 / ROS2 Humble / YDLIDAR X4 Pro
> Workspace: `~/hyup_ros2_ws`

---

## 1. 목적

본 문서는 **YDLIDAR X4 Pro** 센서를 ROS2 환경에서 구동하고 RViz2로 정상적으로 스캔 데이터를 확인하기까지의 **전체 테스트 과정**, **발생한 에러**, 그리고 **해결 과정**을 상세히 기록한 문서이다.

단순 실행 기록이 아니라, **왜 에러가 발생했는지**, **어떤 설정이 잘못되었는지**, **모델별 YAML 파일이 왜 중요한지**에 초점을 두었다.

---

## 2. 하드웨어 및 소프트웨어 환경

### 2.1 하드웨어

- LiDAR: **YDLIDAR X4 Pro**
- 연결 방식: USB (/dev/ttyUSB0)

### 2.2 소프트웨어

- OS: Ubuntu 22.04 (Jammy)
- ROS2: Humble Hawksbill
- 패키지: `ydlidar_ros2_driver`
- SDK Version: 1.2.19
- ROS Driver Version: 1.0.1

---

## 3. 기본 환경 설정

### 3.1 USB 포트 권한 설정

LiDAR는 기본적으로 `/dev/ttyUSB0`로 인식되며, 권한 문제가 발생할 수 있다.

```bash
sudo chmod 666 /dev/ttyUSB0
```

> 참고: 임시 설정이며, 영구 설정은 udev rule을 사용하는 것이 바람직하다.

---

## 4. 초기 실행 및 문제 발생

### 4.1 Launch 실행

```bash
ros2 launch ydlidar_ros2_driver ydlidar_launch.py
```

### 4.2 초기 로그 (문제 발생 시)

```text
Lidar successfully connected [/dev/ttyUSB0:128000]
Error, cannot retrieve Lidar health code -1
Fail to get baseplate device information!
Failed to start scan mode -1
```

### 4.3 증상 정리

- 포트 연결은 성공
- SDK 초기화 성공
- 하지만 **Scan mode 진입 실패**
- RViz2에서 `/scan` 토픽 미출력

---

## 5. 문제 원인 분석

### 5.1 YAML 설정 문제

처음 사용한 YAML 파일은 **X4 Pro 전용 설정이 아니었음**

문제점:

- baudrate 불일치 (230400 → X4 Pro는 128000)
- isSingleChannel 설정 오류
- range_max 과도하게 큼 (64.0)
- inverted / reversion 값 불일치

YDLIDAR는 **모델별로 파라미터가 매우 민감**하여,

> 연결은 되지만 스캔이 시작되지 않는 상황이 자주 발생함

---

## 6. 해결 방법

### 6.1 param 폴더 내 X4 Pro 전용 YAML 사용

패키지 경로:

```bash
ydlidar_ros2_driver/param/
```

이 중 **X4 Pro 모델 전용 YAML 파일**을 launch 파일에서 선택하여 실행

---

## 7. 최종 정상 동작 YAML 파일 (X4 Pro)

아래 YAML 파일 적용 후 **즉시 정상 동작 확인**

```yaml
ydlidar_ros2_driver_node:
  ros__parameters:
    port: /dev/ttyUSB0
    frame_id: laser_frame
    ignore_array: ""
    baudrate: 128000
    lidar_type: 1
    device_type: 0
    sample_rate: 5
    abnormal_check_count: 4
    fixed_resolution: true
    reversion: true
    inverted: true
    auto_reconnect: true
    isSingleChannel: true
    intensity: false
    support_motor_dtr: false
    angle_max: 180.0
    angle_min: -180.0
    range_max: 12.0
    range_min: 0.1
    frequency: 10.0
    invalid_range_is_inf: false
```

---

## 8. 정상 동작 확인

### 8.1 Launch 로그

```text
SDK initializing
SDK has been initialized
Lidar successfully connected [/dev/ttyUSB0:128000]
Lidar init success
```

### 8.2 RViz2 확인

```bash
rviz2
```

- Fixed Frame: `laser_frame`
- Display 추가: `LaserScan`
- Topic: `/scan`

✅ **360도 스캔 데이터 정상 출력 확인**

---

## 9. 정리 및 교훈

### 9.1 핵심 원인

- LiDAR 모델과 **YAML 파라미터 불일치**

### 9.2 중요한 포인트

- YDLIDAR는 **모델별 YAML 필수**
- 연결 성공 ≠ 스캔 성공
- baudrate / isSingleChannel / inverted 값이 매우 중요

### 9.3 다음 단계

- udev rule 작성 (재부팅 시 권한 유지)
- TF 구조 정리 (base_link ↔ laser_frame)
- SLAM / Navigation 스택 연동 테스트

---

## 10. 결론

이번 테스트를 통해 **YDLIDAR X4 Pro는 ROS2 Humble 환경에서 안정적으로 사용 가능**함을 확인했다.

문제의 핵심은 코드가 아니라 **정확한 파라미터 설정**이었으며,
모델 전용 YAML을 사용하는 것이 가장 확실한 해결책이었다.

---
