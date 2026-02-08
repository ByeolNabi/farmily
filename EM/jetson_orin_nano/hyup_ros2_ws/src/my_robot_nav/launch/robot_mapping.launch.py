import os
import sys
from launch import LaunchDescription, LaunchService
from launch.actions import IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch_ros.actions import Node
from ament_index_python.packages import get_package_share_directory

def generate_launch_description():
    # 1. 패키지 경로 및 파라미터 파일 설정
    try:
        lidar_pkg_dir = get_package_share_directory('ydlidar_ros2_driver')
    except Exception as e:
        print("Error: 'ydlidar_ros2_driver' 패키지를 찾을 수 없습니다.")
        sys.exit(1)
    
    slam_params_file = '/home/d101/hyup_ros2_ws/my_slam_params.yaml'

    print(f"Loading Params from: {slam_params_file}") 

    return LaunchDescription([
        # ==========================================
        # 1. 하드웨어 드라이버
        # ==========================================
        
        # [A] YDLidar 드라이버
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
            arguments=['0', '0', '0.1', '0', '0', '0', 'base_link', 'laser_frame'],
            output='screen'
        ),

        # [C] 모터 컨트롤러 (아커만 제어기)
        Node(
            package='my_robot_nav',
            executable='ackermann_controller',
            name='ackermann_controller',
            output='screen'
        ),

        # ==========================================
        # 2. 오도메트리 및 매핑 알고리즘
        # ==========================================

        # [D] 라이다 오도메트리 (RF2O)
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

        # [E] SLAM Toolbox
        Node(
            package='slam_toolbox',
            executable='async_slam_toolbox_node',
            name='slam_toolbox',
            output='screen',
            parameters=[slam_params_file]
        ),
        
        # [F] RViz2
        Node(
            package='rviz2',
            executable='rviz2',
            name='rviz2',
            output='screen'
        )
    ])

if __name__ == '__main__':
    ls = LaunchService()
    ls.include_launch_description(generate_launch_description())
    ls.run()
