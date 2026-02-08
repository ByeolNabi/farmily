import os
import sys
from launch import LaunchDescription, LaunchService
from launch.actions import IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch_ros.actions import Node
from ament_index_python.packages import get_package_share_directory

def generate_launch_description():
    # ==========================================
    # 1. 경로 및 파일 설정
    # ==========================================
    
    # [중요] 아까 저장한 지도 파일 경로 확인!
    map_file = '/home/d101/map/my_hh.yaml'
    
    # Nav2 기본 파라미터 파일 가져오기
    nav2_bringup_dir = get_package_share_directory('nav2_bringup')
    nav2_launch_dir = os.path.join(nav2_bringup_dir, 'launch')
    nav2_params_file = '/home/d101/hyup_ros2_ws/my_nav2_params.yaml'

    # YDLidar 패키지 경로
    try:
        lidar_pkg_dir = get_package_share_directory('ydlidar_ros2_driver')
    except Exception as e:
        print("Error: 'ydlidar_ros2_driver' 패키지를 찾을 수 없습니다.")
        sys.exit(1)

    return LaunchDescription([
        # ==========================================
        # 2. 하드웨어 및 센서 실행 (SLAM때와 동일)
        # ==========================================
        
        # [A] YDLidar 실행
        IncludeLaunchDescription(
            PythonLaunchDescriptionSource(
                os.path.join(lidar_pkg_dir, 'launch', 'ydlidar_launch.py')
            )
        ),

        # [B] TF 연결 (Robot -> Laser)
        Node(
            package='tf2_ros',
            executable='static_transform_publisher',
            name='base_to_laser',
            arguments=['0', '0', '0.4', '0', '0', '0', 'base_link', 'laser_frame'],
            output='screen'
        ),

        # [C] 모터 제어기 (Ackermann)
        Node(
            package='my_robot_nav',
            executable='ackermann_controller',
            name='ackermann_controller',
            output='screen'
        ),

        # [D] 오도메트리 (RF2O) - 위치 추정 필수!
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
                'freq': 10.0
            }],
        ),

        # ==========================================
        # 3. 내비게이션 (Nav2) 핵심
        # ==========================================
        
        # [E] Nav2 Bringup (지도 로드 + AMCL + 경로계획)
        IncludeLaunchDescription(
            PythonLaunchDescriptionSource(
                os.path.join(nav2_launch_dir, 'bringup_launch.py')
            ),
            launch_arguments={
                'map': map_file,
                'params_file': nav2_params_file,
                'use_sim_time': 'False',
                'autostart': 'True'
            }.items()
        ),

        # [F] RViz2 (Nav2용 설정으로 켜기)
        Node(
            package='rviz2',
            executable='rviz2',
            name='rviz2',
            arguments=['-d', os.path.join(nav2_bringup_dir, 'rviz', 'nav2_default_view.rviz')],
            output='screen'
        )
    ])

if __name__ == '__main__':
    ls = LaunchService()
    ls.include_launch_description(generate_launch_description())
    ls.run()
