from launch import LaunchDescription
from launch_ros.actions import Node
from launch.actions import DeclareLaunchArgument
from launch.substitutions import LaunchConfiguration

def generate_launch_description():
    # base_link -> laser (라이다 장착 위치/각도는 실제 치수로 나중에 튜닝)
    # x y z roll pitch yaw
    x = LaunchConfiguration('x')
    y = LaunchConfiguration('y')
    z = LaunchConfiguration('z')
    roll = LaunchConfiguration('roll')
    pitch = LaunchConfiguration('pitch')
    yaw = LaunchConfiguration('yaw')

    parent = LaunchConfiguration('parent')
    child = LaunchConfiguration('child')

    return LaunchDescription([
        DeclareLaunchArgument('parent', default_value='base_link'),
        DeclareLaunchArgument('child', default_value='laser_frame'),
        DeclareLaunchArgument('x', default_value='0.10'),
        DeclareLaunchArgument('y', default_value='0.0'),
        DeclareLaunchArgument('z', default_value='0.15'),
        DeclareLaunchArgument('roll', default_value='0.0'),
        DeclareLaunchArgument('pitch', default_value='0.0'),
        DeclareLaunchArgument('yaw', default_value='0.0'),

        Node(
            package='tf2_ros',
            executable='static_transform_publisher',
            name='static_tf_base_to_laser',
            arguments=[x, y, z, roll, pitch, yaw, parent, child],
            output='screen'
        )
    ])
