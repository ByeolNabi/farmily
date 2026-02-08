import os
from ament_index_python.packages import get_package_share_directory
from launch import LaunchDescription
from launch.actions import IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch_ros.actions import Node

def generate_launch_description():
    # 1. YDLidar 실행
    lidar_launch = IncludeLaunchDescription(
        PythonLaunchDescriptionSource([
            os.path.join(get_package_share_directory('ydlidar_ros2_driver'), 'launch', 'ydlidar_launch.py')
        ])
    )

    # 2. Static TF (로봇 몸체 <-> 라이다 위치)
    # 로봇 중심에서 라이다가 앞(x)으로 0.1m, 위(z)로 0.15m에 있다고 가정
    static_tf = Node(
        package='tf2_ros',
        executable='static_transform_publisher',
        name='static_tf_pub_laser',
        arguments=['0.1', '0', '0.15', '0', '0', '0', 'base_link', 'laser_frame']
    )

    # 3. 모터 제어 노드
    motor_controller = Node(
        package='my_robot_nav',
        executable='ackermann_controller',
        name='ackermann_controller',
        output='screen'
    )

    # 4. RF2O 오도메트리 (위치 추정)
    rf2o_odom = Node(
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
            'freq': 10.0
        }]
    )

    return LaunchDescription([
        lidar_launch,
        static_tf,
        motor_controller,
        rf2o_odom
    ])
