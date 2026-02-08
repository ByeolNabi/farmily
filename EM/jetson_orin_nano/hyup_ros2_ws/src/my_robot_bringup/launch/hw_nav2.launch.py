import os

from launch import LaunchDescription
from launch.actions import DeclareLaunchArgument, IncludeLaunchDescription
from launch.substitutions import LaunchConfiguration
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch_ros.actions import Node
from ament_index_python.packages import get_package_share_directory


def generate_launch_description():
    # ---- args ----
    use_sim_time = LaunchConfiguration('use_sim_time')
    map_yaml = LaunchConfiguration('map')
    params_file = LaunchConfiguration('params_file')

    laser_frame = LaunchConfiguration('laser_frame')
    laser_x = LaunchConfiguration('laser_x')
    laser_y = LaunchConfiguration('laser_y')
    laser_z = LaunchConfiguration('laser_z')
    laser_roll = LaunchConfiguration('laser_roll')
    laser_pitch = LaunchConfiguration('laser_pitch')
    laser_yaw = LaunchConfiguration('laser_yaw')

    declare_use_sim_time = DeclareLaunchArgument('use_sim_time', default_value='False')
    declare_map = DeclareLaunchArgument(
        'map',
        default_value=os.path.expanduser('~/maps/map.yaml'),
        description='Full path to map.yaml'
    )
    declare_params = DeclareLaunchArgument(
        'params_file',
        default_value=os.path.join(
            get_package_share_directory('my_robot_bringup'),
            'config',
            'nav2_params.yaml'
        ),
        description='Nav2 params'
    )

    # /scan.header.frame_id 와 반드시 동일해야 함
    declare_laser_frame = DeclareLaunchArgument('laser_frame', default_value='laser_frame')
    declare_laser_x = DeclareLaunchArgument('laser_x', default_value='0.10')
    declare_laser_y = DeclareLaunchArgument('laser_y', default_value='0.0')
    declare_laser_z = DeclareLaunchArgument('laser_z', default_value='0.15')
    declare_laser_roll = DeclareLaunchArgument('laser_roll', default_value='0.0')
    declare_laser_pitch = DeclareLaunchArgument('laser_pitch', default_value='0.0')
    declare_laser_yaw = DeclareLaunchArgument('laser_yaw', default_value='0.0')

    # ---- 1) static TF: base_link -> laser_frame ----
    static_tf = Node(
        package='tf2_ros',
        executable='static_transform_publisher',
        name='static_tf_base_to_laser',
        output='screen',
        arguments=[
            laser_x, laser_y, laser_z,
            laser_roll, laser_pitch, laser_yaw,
            'base_link', laser_frame
        ]
    )

    # ---- 2) rf2o: /scan -> /odom + TF(odom->base_link) ----
    # (라이다 드라이버가 반드시 /scan을 발행 중이어야 동작)
    rf2o = Node(
        package='rf2o_laser_odometry',
        executable='rf2o_laser_odometry_node',
        name='rf2o_laser_odometry',
        output='screen',
        parameters=[{'use_sim_time': use_sim_time}],
        remappings=[('scan', '/scan'), ('odom', '/odom')]
    )

    # ---- 3) hardware: /cmd_vel -> PCA9685 ----
    hw = Node(
        package='my_robot_hw',
        executable='cmd_vel_to_pca9685',
        name='cmd_vel_to_pca9685',
        output='screen',
        parameters=[{
            'cmd_vel_topic': '/cmd_vel',
            'max_linear_x': 0.12,
            'max_throttle': 0.35,
            'steer_center_deg': 100.0,
            'steer_max_offset_deg': 25.0,
            'max_angular_z': 0.5,
            'cmd_timeout_sec': 0.5,
        }]
    )

    # ---- 4) Nav2 bringup include (RViz 포함) ----
    nav2_bringup_dir = get_package_share_directory('nav2_bringup')
    nav2 = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(
            os.path.join(nav2_bringup_dir, 'launch', 'navigation_launch.py')
        ),
        launch_arguments={
            'use_sim_time': use_sim_time,
            'map': map_yaml,
            'params_file': params_file,
            'use_rviz': 'True',  # ⭐ RViz 강제 켬
        }.items()
    )

    return LaunchDescription([
        declare_use_sim_time,
        declare_map,
        declare_params,

        declare_laser_frame,
        declare_laser_x, declare_laser_y, declare_laser_z,
        declare_laser_roll, declare_laser_pitch, declare_laser_yaw,

        static_tf,
        rf2o,
        hw,
        nav2,
    ])
