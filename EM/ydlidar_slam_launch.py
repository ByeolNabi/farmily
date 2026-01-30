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
    
    # 사용자 홈 디렉토리 자동 감지 (d101 등 사용자명이 바뀌어도 작동함)
    user_home = os.path.expanduser('~')
    workspace_dir = os.path.join(user_home, 'hyup_ros2_ws') # 워크스페이스 이름
    
    # SLAM 파라미터 파일 경로
    slam_params_file = os.path.join(workspace_dir, 'my_slam_params.yaml')

    print(f"Loading SLAM params: {slam_params_file}") 

    return LaunchDescription([
        # --- 노드 1: YDLidar 드라이버 ---
        IncludeLaunchDescription(
            PythonLaunchDescriptionSource(
                os.path.join(lidar_pkg_dir, 'launch', 'ydlidar_launch.py')
            )
        ),

        # --- 노드 2: TF 연결 (Robot -> Laser) ---
        # 로봇 몸체에서 라이다까지의 거리
        Node(
            package='tf2_ros',
            executable='static_transform_publisher',
            name='base_to_laser',
            arguments=['0', '0', '0.1', '0', '0', '0', 'base_link', 'laser_frame'],
            output='screen'
        ),

        # --- 노드 3: 라이다 오도메트리 (RF2O) ---
        # 기존의 Static TF(고정)를 삭제하고, 라이다로 움직임을 계산하는 노드를 추가
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
                'freq': 20.0    # -> 변경
            }],
        ),

        # --- 노드 4: SLAM Toolbox ---
        Node(
            package='slam_toolbox',
            executable='async_slam_toolbox_node',
            name='slam_toolbox',
            output='screen',
            parameters=[slam_params_file]
        ),
        
        # --- 노드 5: RViz2 ---
        Node(
            package='rviz2',
            executable='rviz2',
            name='rviz2',
            output='screen'
        )
    ])

# ==========================================
# 메인 실행 코드
# ==========================================
if __name__ == '__main__':
    ls = LaunchService()
    ls.include_launch_description(generate_launch_description())
    ls.run()