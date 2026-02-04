# 🤖 Jetson Orin Nano 기반 자율주행 로봇 구현 가이드 (Ackermann)

본 문서는 전륜 서보 조향 및 후륜 DC 모터 구동 방식을 채택하고, YDLidar X4 Pro를 사용하는 로봇의 ROS 2 Humble 기반 자율주행 구현 단계를 설명합니다.

---

## 📋 1. 시스템 환경 (System Stack)
* **Main Board:** NVIDIA Jetson Orin Nano
* **OS:** Ubuntu 22.04 (JetPack 5.x/6.x)
* **Middleware:** ROS 2 Humble
* **Sensors:** YDLidar X4 Pro (추가 IMU/Encoder 없음)
* **Actuators:** PCA9685를 이용한 서보 조향 및 DC 모터 구동

---

## 🛠️ 2. 필수 사전 준비 (Pre-requisites)
자율주행 스택을 올리기 전에 아래 데이터들이 정상적으로 발행되어야 합니다.

1.  **Lidar Driver:** `ydlidar_ros2_driver` 실행 후 `/scan` 토픽 확인.
2.  **Base Controller:** `geometry_msgs/Twist` 메시지를 구독하여 PCA9685를 제어하는 노드 구동.
3.  **Static TF:** 로봇의 중심(`base_link`)에서 라이다 위치(`laser_frame`)까지의 물리적 좌표 정의.
    ```bash
    # 예시: base_link에서 앞(x)으로 10cm, 위(z)로 15cm 지점에 라이다가 있는 경우
    ros2 run tf2_ros static_transform_publisher 0.1 0 0.15 0 0 0 base_link laser_frame
    ```

---

## 🗺️ 3. 자율주행 구현 핵심 4단계

### 1단계: Lidar 기반 오도메트리 (rf2o_laser_odometry)
엔코더가 없기 때문에 Lidar의 스캔 매칭을 통해 로봇의 움직임을 추정합니다.
* **역할:** `/scan` 데이터를 분석하여 `/odom` 토픽과 `odom -> base_link` 변환(TF)을 생성.
* **특징:** 휠 엔코더 없이도 지도 위에서 로봇의 위치 변화를 알 수 있게 해주는 필수 단계입니다.

### 2단계: SLAM을 이용한 지도 작성 (slam_toolbox)
* **패키지:** `slam_toolbox` (Asynchronous 모드 권장)
* **방법:** 1. `slam_toolbox` 노드 실행.
    2. 조이스틱이나 키보드로 로봇을 주행하며 실시간으로 지도 생성.
    3. 완료 후 `Nav2`에서 사용할 수 있도록 지도 저장 (`.yaml`, `.pgm`).

### 3단계: 위치 추정 (AMCL)
* **역할:** Map Server로 불러온 지도 위에서 Lidar 데이터를 대조해 로봇의 현재 좌표를 결정.
* **설정:** 엔코더가 없으므로 `alpha` 파라미터(노이즈 값)를 조절하여 Lidar 매칭 비중을 높여야 합니다.

### 4단계: Nav2 스택 설정 (Navigation2)
Ackermann 조향 구조에 맞게 `nav2_params.yaml`을 수정해야 합니다.
* **Controller Server:** `Regulated Pure Pursuit` 컨트롤러 추천 (조향 각도 제한이 있는 로봇에 최적화).
* **Planner Server:** `Smac Planner` (Ackermann 모델 지원).
* **Kinematics:** * `min_turning_radius`: 로봇이 회전할 수 있는 최소 반경.
    * `wheelbase`: 전륜과 후륜 사이의 거리.

---

## ⚠️ 구현 시 주의사항
* **Lidar 매칭 유실:** 급격한 회전 시 Lidar 데이터가 겹치지 않아 위치를 잃어버릴 수 있습니다. **최대 회전 속도**를 낮게 설정하세요.
* **연산 부하:** Jetson Orin Nano의 성능을 고려하여 `Nav2`의 `controller_frequency`를 5~10Hz 정도로 설정하는 것이 안정적입니다.
* **조향 오차:** 서보 모터의 0도(직진) 위치를 물리적으로 정확히 맞추어야 위치 추정 오차가 줄어듭니다.

---

## 5. PCA9685 제어 노드

- PCA9685를 통해 **전륜 서보(조향)**와 **후륜 DC 모터(주행)**를 제어하는 ROS 2 Humble 파이썬 노드 예시 코드입니다.
- 이 코드는 geometry_msgs/msg/Twist 메시지를 구독하여, 선속도($x$)는 DC 모터 속도로, 각속도($z$)는 서보 모터의 조향 각도로 변환합니다.

---

### 🛠️ 필수 라이브러리 설치
Jetson Orin Nano에서 PCA9685를 제어하기 위해 아다프루트(Adafruit) 라이브러리가 필요합니다.

```bash
sudo pip3 install adafruit-circuitpython-pca9685 adafruit-circuitpython-servokit
```

### PCA9685 제어 ROS 2 노드 (`ackermann_controller.py`)
이 코드는 사용자의 하드웨어 연결 상태에 맞춰 `STEERING_CHANNEL`과 `MOTOR_CHANNEL`을 수정하여 사용하세요.

```python
import rclpy
from rclpy.node import Node
from geometry_msgs.msg import Twist
from adafruit_servokit import ServoKit
import board
import busio

class AckermannController(Node):
    def __init__(self):
        super().__init__('ackermann_controller')
        
        # 1. PCA9685 및 ServoKit 초기화 (주소 0x40 기본값)
        i2c = busio.I2C(board.SCL, board.SDA)
        self.kit = ServoKit(channels=16, i2c=i2c)
        
        # 2. 파라미터 설정 (로봇에 맞춰 수정)
        self.STEERING_CHANNEL = 0   # 서보 모터 연결 채널
        self.MOTOR_CHANNEL = 1      # DC 모터 드라이버(ESC 등) 연결 채널
        
        self.STEER_CENTER = 90      # 정면 각도 (도)
        self.STEER_RANGE = 45       # 좌우 최대 회전 범위 (도)
        
        # 3. 구독자 설정
        self.subscription = self.create_subscription(
            Twist,
            '/cmd_vel',
            self.listener_callback,
            10)
        
        self.get_logger().info('Ackermann Controller Node has been started')

    def listener_callback(self, msg):
        # 선속도 (Linear X) -> DC 모터 제어
        # 0.0 ~ 1.0 사이의 값을 서보 모터의 0~180도 등으로 매핑하거나 ESC 제어
        throttle = msg.linear.x
        
        # 각속도 (Angular Z) -> 서보 조향 제어
        # Twist의 angular.z는 라디안 단위이므로, 이를 조향 각도로 변환
        steering_angle = self.STEER_CENTER + (msg.angular.z * self.STEER_RANGE)
        
        # 각도 제한 (범위를 벗어나지 않게 처리)
        steering_angle = max(min(steering_angle, self.STEER_CENTER + self.STEER_RANGE), 
                             self.STEER_CENTER - self.STEER_RANGE)

        # 실제 하드웨어 명령 하달
        self.control_robot(throttle, steering_angle)

    def control_robot(self, throttle, steer):
        # 조향 서보 제어
        self.kit.servo[self.STEERING_CHANNEL].angle = steer
        
        # 후륜 구동 제어 (Continuous Servo 모드 혹은 PWM 직접 제어)
        # 사용하시는 모터 드라이버 방식에 따라 아래 로직은 수정이 필요할 수 있습니다.
        # 예: 서보 형태의 ESC 사용 시
        # self.kit.continuous_servo[self.MOTOR_CHANNEL].throttle = throttle
        
        self.get_logger().info(f'Steer: {steer:.2f}, Throttle: {throttle:.2f}')

def main(args=None):
    rclpy.init(args=args)
    node = AckermannController()
    try:
        rclpy.spin(node)
    except KeyboardInterrupt:
        pass
    finally:
        # 종료 시 정지 명령
        node.control_robot(0.0, 90)
        node.destroy_node()
        rclpy.shutdown()

if __name__ == '__main__':
    main()
```

---

### 💡 코드 구현 시 핵심 포인트
1. 매핑(Mapping) 최적화:

- 사용하시는 DC 모터 드라이버가 PWM 방식인지 연속 회전 서보(Continuous Servo) 방식인지에 따라 control_robot 내부 함수를 수정해야 합니다.

- 어제 말씀하신 내용 중 "무거워서 회전 각도가 크지 않다"고 하셨는데, 코드에서 STEER_RANGE 값을 조절하며 서보가 기계적 한계까지 움직이도록 보정해 보세요.

2. 부드러운 움직임:

- `A`를 눌렀을 때 좌측으로 직진하는 등의 복합 동작은 `Nav2`가 생성하는 `cmd_vel` 토픽 내에 `linear.x`와 `angular.z` 값이 동시에 포함되어 전달되므로 자연스럽게 해결됩니다.

3. 안전 장치:

- `try-finally` 구문을 통해 노드가 강제 종료될 때 반드시 모터를 정지(`throttle = 0`) 시키도록 설계했습니다.

이 노드를 실행한 상태에서 별도의 터미널을 열어 `ros2 run teleop_twist_keyboard teleop_twist_keyboard`를 실행하면 키보드로 로봇을 직접 움직여보며 조향 각도를 미세 조정할 수 있습니다.

---

## 6. rf2o 오도메트리 설정 파일

rf2o_laser_odometry는 바퀴 엔코더가 없는 로봇에게 가상의 이동 정보(Odometry)를 제공하는 아주 중요한 역할을 합니다. 이 패키지는 Lidar 스캔 데이터를 비교하여 로봇이 얼마나 움직였는지 계산합니다.

---

1. rf2o 패키지 설치

- 먼저 Jetson에서 해당 패키지를 설치해야 합니다. (빌드가 필요한 경우 소스 설치를 해야 할 수도 있으나, 보통 데비안 패키지로 제공됩니다.)

```bash
sudo apt install ros-humble-rf2o-laser-odometry
```

2. rf2o 실행을 위한 Launch 파일 (`rf2o_odom.launch.py`)

- 이 파일은 라이다 데이터를 받아 오도메트리를 계산하고, `odom -> base_link` 좌표 변환(TF)을 발행합니다.

```python
import os
from launch import LaunchDescription
from launch_ros.actions import Node

def generate_launch_description():
    return LaunchDescription([
        Node(
            package='rf2o_laser_odometry',
            executable='rf2o_laser_odometry_node',
            name='rf2o_laser_odometry',
            output='screen',
            parameters=[{
                'laser_scan_topic': '/scan',        # Lidar 토픽 이름
                'odom_topic': '/odom',              # 생성할 오도메트리 토픽 이름
                'publish_tf': True,                 # odom -> base_link TF 브로드캐스트 여부
                'base_frame_id': 'base_link',       # 로봇의 중심 프레임
                'odom_frame_id': 'odom',            # 오도메트리 프레임
                'init_pose_from_topic': '',         # 초기 위치 설정 (비워둠)
                'freq': 10.0,                       # 오도메트리 발행 주기 (Hz)
                'sensor_timeout': 0.5               # 센서 타임아웃
            }],
        ),
    ])
```

---

3. 매우 중요한 사전 작업: Static TF 설정
- `rf2o`가 정상적으로 동작하려면 `base_link`와 `laser_frame` 사이의 물리적 위치 관계가 미리 정의되어 있어야 합니다. (이게 없으면 노드가 실행되지 않거나 오류가 발생합니다.)
- 별도의 로봇 모델(URDF)이 없다면, Launch 파일에 아래 노드를 추가하거나 터미널에서 실행하세요.
  - 예: 라이다가 로봇 중심에서 앞(x)으로 10cm, 위(z)로 15cm 위치에 있는 경우

```python
# Launch 파일에 추가할 경우
Node(
    package='tf2_ros',
    executable='static_transform_publisher',
    arguments=['0.1', '0', '0.15', '0', '0', '0', 'base_link', 'ydlidar_frame'] 
    # ydlidar_frame은 실제 scan 토픽의 frame_id와 일치해야 합니다.
)
```

---

4. Nav2를 위한 최종 점검 (중요!)

- `rf2o` 설정이 끝나면 다음 사항을 확인해야 자율주행이 가능합니다.

  1. Frame ID 확인:
  - `ydlidar_ros2_driver`에서 나오는 `/scan` 토픽의 `frame_id`가 무엇인지 확인하세요 (보통 `laser_frame` 또는 `ydlidar_frame`). `rf2o` 설정과 Static TF 설정의 이름이 모두 일치해야 합니다.

  2. 동작확인:
  - 로봇을 실행하고 rviz2를 켭니다.
  - Fixed Frame을 `odom`으로 설정합니다.
  - 로봇을 손으로 천천히 밀었을 때, `rviz2` 상의 Lidar 점들이 가만히 있고 로봇 모델(또는 좌표축)이 움직인다면 성공입니다.
  3. Ackermann 제약:
  - `rf2o`는 평면 이동을 계산하므로 조향 방식에 상관없이 동작합니다. 하지만 나중에 `Nav2`를 돌릴 때는 앞서 드린 가이드처럼 **Ackermann 전용 플래너**를 반드시 써야 합니다.

---

### 💡 정리된 최종 패키지 구조 제안
프로젝트 폴더(예: `my_robot_nav`) 안에 다음과 같이 파일을 배치하는 것을 권장합니다.

- `src/my_robot_nav/`

  - `launch/`

    - `bringup.launch.py` (Lidar + PCA9685 노드 + Static TF 실행)

    - `rf2o_odom.launch.py` (오도메트리 실행)

  - `my_robot_nav/`

    - `ackermann_controller.py` (PCA9685 제어 코드)

___

7. Nav2 상세 파라미터 설정

### 📄 Nav2 상세 파라미터 (`nav2_params.yaml`)

```YAML
amcl:
  ros__parameters:
    use_sim_time: false
    # 엔코더가 없으므로 정지 상태에서의 노이즈(alpha) 값을 적절히 조절
    alpha1: 0.2
    alpha2: 0.2
    alpha3: 0.2
    alpha4: 0.2
    base_frame_id: "base_link"
    odom_frame_id: "odom"
    scan_topic: "scan"

bt_navigator:
  ros__parameters:
    use_sim_time: false
    global_frame: map
    robot_base_frame: base_link
    odom_frame: odom

controller_server:
  ros__parameters:
    use_sim_time: false
    controller_frequency: 10.0  # Jetson 부담을 줄이기 위해 10Hz 권장
    min_x_velocity_threshold: 0.001
    publish_lower_priority_bt: true
    # Ackermann 로봇을 위한 컨트롤러 설정
    progress_checker_plugin: "progress_checker"
    goal_checker_plugins: ["general_goal_checker"]
    controller_plugins: ["FollowPath"]

    progress_checker:
      plugin: "nav2_controller::SimpleProgressChecker"
      required_movement_radius: 0.5
      movement_time_allowance: 10.0

    general_goal_checker:
      stateful: True
      plugin: "nav2_controller::SimpleGoalChecker"
      xy_goal_tolerance: 0.25
      yaw_goal_tolerance: 0.25

    FollowPath:
      plugin: "nav2_regulated_pure_pursuit_controller::RegulatedPurePursuitController"
      desired_linear_vel: 0.3      # 최대 속도 (안전을 위해 낮게 시작)
      lookahead_dist: 0.6          # 앞을 내다보는 거리
      min_lookahead_dist: 0.3
      max_lookahead_dist: 0.9
      lookahead_time: 1.5
      rotate_to_heading_angular_vel: 1.0
      use_velocity_scaled_lookahead_dist: false
      min_approach_linear_velocity: 0.05
      approach_velocity_scaling_dist: 0.6
      use_collision_detection: true
      max_allowed_time_to_collision_up_to_carrot: 1.0
      use_regulated_linear_velocity_scaling: true
      use_cost_constrained_velocity_scaling: true
      regulated_linear_scaling_min_radius: 0.9  # 로봇의 최소 회전 반경에 맞춰 조절
      regulated_linear_scaling_min_speed: 0.1

planner_server:
  ros__parameters:
    expected_planner_frequency: 1.0
    use_sim_time: false
    planner_plugins: ["GridBased"]
    GridBased:
      plugin: "nav2_navfn_planner/NavfnPlanner"  # 전역 경로는 일반 플래너 사용

global_costmap:
  global_costmap:
    ros__parameters:
      update_frequency: 1.0
      publish_frequency: 1.0
      global_frame: map
      robot_base_frame: base_link
      use_sim_time: false
      robot_radius: 0.25  # 로봇의 크기에 맞게 수정 (중심에서 바깥까지 거리)
      plugins: ["static_layer", "obstacle_layer", "inflation_layer"]
      inflation_layer:
        plugin: "nav2_costmap_2d::InflationLayer"
        inflation_radius: 0.55
        cost_scaling_factor: 3.0

local_costmap:
  local_costmap:
    ros__parameters:
      update_frequency: 5.0
      publish_frequency: 2.0
      global_frame: odom
      robot_base_frame: base_link
      use_sim_time: false
      rolling_window: true
      width: 3
      height: 3
      resolution: 0.05
      robot_radius: 0.25  # 로봇의 크기에 맞게 수정
      plugins: ["obstacle_layer", "inflation_layer"]
```

---

### 🛠️ 반드시 수정해야 할 파라미터 설명
1. `robot_radius`:

로봇을 위에서 보았을 때 중심축에서 가장 먼 곳까지의 거리(m)입니다. 이 값만큼 벽에서 떨어져서 주행합니다.

2. `desired_linear_vel`:

현재 엔코더가 없으므로 `0.2`~`0.3` 정도의 느린 속도로 시작하는 것을 추천합니다. 너무 빠르면 Lidar 기반 오도메트리가 위치를 놓칠 수 있습니다.

3. `regulated_linear_scaling_min_radius`:

로봇이 최대한 꺾었을 때 그리는 원의 반지름입니다. 어제 "무거워서 회전 각도가 크지 않다"고 하셨으므로, 이 값을 실제 측정치보다 조금 더 크게 잡아야 Nav2가 무리하게 좁은 길로 경로를 생성하지 않습니다.

---

# 🚀 자율주행 실행 순서

아래 순서대로 실행하세요.

1. Bringup: Lidar 구동 + PCA9685 제어 노드 + Static TF 실행

2. Odometry: `rf2o_laser_odometry` 실행

3. Map Server: 미리 만든 지도를 로드

4. Localization: `amcl` 실행 (RViz2에서 `2D Pose Estimate`로 위치 잡아주기)

5. Navigation: 위 파라미터를 사용하여 Nav2 실행

이제 RViz2에서 `Nav2 Goal`을 찍으면 로봇이 조향을 꺾어가며 목적지로 이동할 것입니다.