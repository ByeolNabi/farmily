from setuptools import setup
import os
from glob import glob

package_name = 'my_robot_nav'

setup(
    name=package_name,
    version='0.0.0',
    packages=[package_name],
    data_files=[
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
        # Launch 파일 설치 경로 지정 (필수!)
        (os.path.join('share', package_name, 'launch'), glob('launch/*.launch.py')),
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='d101',
    maintainer_email='your_email@gmail.com',
    description='Ackermann Robot Navigation Package',
    license='TODO: License declaration',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
            'ackermann_controller = my_robot_nav.ackermann_controller:main'
        ],
    },
)
