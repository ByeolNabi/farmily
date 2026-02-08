import time

import rclpy
from rclpy.node import Node
from geometry_msgs.msg import Twist

import board
import busio
from adafruit_pca9685 import PCA9685
from adafruit_servokit import ServoKit


def clamp(x, lo, hi):
    return max(lo, min(hi, x))


class PWMThrottleHat:
    """
    channel=0 기준:
      +3, +4 : 방향 제어
      +5     : PWM (speed)
    """
    def __init__(self, pca: PCA9685, channel: int, freq_hz: int = 60):
        self.pca = pca
        self.channel = channel
        self.pca.frequency = freq_hz

    def set_throttle(self, throttle: float):
        throttle = clamp(throttle, -1.0, 1.0)
        pulse = int(0xFFFF * abs(throttle))

        if throttle < 0.0:
            self.pca.channels[self.channel + 5].duty_cycle = pulse
            self.pca.channels[self.channel + 4].duty_cycle = 0
            self.pca.channels[self.channel + 3].duty_cycle = 0xFFFF
        elif throttle > 0.0:
            self.pca.channels[self.channel + 5].duty_cycle = pulse
            self.pca.channels[self.channel + 4].duty_cycle = 0xFFFF
            self.pca.channels[self.channel + 3].duty_cycle = 0
        else:
            self.pca.channels[self.channel + 5].duty_cycle = 0
            self.pca.channels[self.channel + 4].duty_cycle = 0
            self.pca.channels[self.channel + 3].duty_cycle = 0


class CmdVelToPCA9685(Node):
    def __init__(self):
        super().__init__('cmd_vel_to_pca9685')

        # ---- Parameters ----
        self.declare_parameter('cmd_vel_topic', '/cmd_vel')
        self.declare_parameter('i2c_address', 0x60)
        self.declare_parameter('pca_freq_hz', 60)

        self.declare_parameter('motor_channel', 0)
        self.declare_parameter('servo_channel', 0)

        # Nav2와 맞추기 쉬운 저속 기본값(launch에서 조절)
        self.declare_parameter('max_linear_x', 0.12)    # m/s
        self.declare_parameter('max_throttle', 0.35)    # [-1..1]에서 제한

        self.declare_parameter('steer_center_deg', 100.0)
        self.declare_parameter('steer_max_offset_deg', 25.0)
        self.declare_parameter('max_angular_z', 0.5)    # rad/s

        self.declare_parameter('cmd_timeout_sec', 0.5)

        # ---- Load params ----
        self.cmd_vel_topic = self.get_parameter('cmd_vel_topic').value
        addr = int(self.get_parameter('i2c_address').value)
        pca_freq = int(self.get_parameter('pca_freq_hz').value)

        self.motor_channel = int(self.get_parameter('motor_channel').value)
        self.servo_channel = int(self.get_parameter('servo_channel').value)

        self.max_linear_x = float(self.get_parameter('max_linear_x').value)
        self.max_throttle = float(self.get_parameter('max_throttle').value)

        self.steer_center = float(self.get_parameter('steer_center_deg').value)
        self.steer_offset = float(self.get_parameter('steer_max_offset_deg').value)
        self.max_angular_z = float(self.get_parameter('max_angular_z').value)

        self.cmd_timeout = float(self.get_parameter('cmd_timeout_sec').value)

        # ---- Hardware init ----
        try:
            i2c = busio.I2C(board.SCL, board.SDA)
            self.pca = PCA9685(i2c, address=addr)
            self.pca.frequency = pca_freq

            self.motor = PWMThrottleHat(self.pca, channel=self.motor_channel, freq_hz=pca_freq)

            self.kit = ServoKit(channels=16, i2c=i2c, address=addr)
            self.kit.servo[self.servo_channel].angle = clamp(self.steer_center, 0.0, 180.0)
        except Exception as e:
            self.get_logger().error(f"I2C/PCA9685 init failed: {e}")
            raise

        # ---- ROS wiring ----
        self.last_cmd_time = time.time()
        self.sub = self.create_subscription(Twist, self.cmd_vel_topic, self.on_cmd_vel, 10)
        self.timer = self.create_timer(0.1, self.watchdog)

        self.get_logger().info(
            f"Listening {self.cmd_vel_topic} | i2c addr=0x{addr:X} | motor_ch={self.motor_channel}, servo_ch={self.servo_channel}"
        )

    def on_cmd_vel(self, msg: Twist):
        self.last_cmd_time = time.time()

        # linear.x [m/s] -> throttle [-1..1]
        throttle_cmd = 0.0 if self.max_linear_x <= 1e-6 else (msg.linear.x / self.max_linear_x)
        throttle_cmd = clamp(throttle_cmd, -1.0, 1.0)
        throttle_cmd *= self.max_throttle
        throttle_cmd = clamp(throttle_cmd, -self.max_throttle, self.max_throttle)

        # angular.z [rad/s] -> steering angle [deg]
        steer_norm = 0.0 if self.max_angular_z <= 1e-6 else (msg.angular.z / self.max_angular_z)
        steer_norm = clamp(steer_norm, -1.0, 1.0)
        angle = clamp(self.steer_center + steer_norm * self.steer_offset, 0.0, 180.0)

        self.motor.set_throttle(throttle_cmd)
        self.kit.servo[self.servo_channel].angle = angle

    def watchdog(self):
        if (time.time() - self.last_cmd_time) > self.cmd_timeout:
            self.motor.set_throttle(0.0)
            self.kit.servo[self.servo_channel].angle = clamp(self.steer_center, 0.0, 180.0)

    def destroy_node(self):
        try:
            self.motor.set_throttle(0.0)
            self.kit.servo[self.servo_channel].angle = clamp(self.steer_center, 0.0, 180.0)
            self.pca.deinit()
        except Exception:
            pass
        super().destroy_node()


def main():
    rclpy.init()
    node = CmdVelToPCA9685()
    try:
        rclpy.spin(node)
    finally:
        node.destroy_node()
        rclpy.shutdown()


if __name__ == '__main__':
    main()
