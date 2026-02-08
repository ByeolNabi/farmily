import rclpy
from rclpy.node import Node
from geometry_msgs.msg import Twist
from adafruit_pca9685 import PCA9685
from adafruit_servokit import ServoKit
import board
import busio
import time

class PWMThrottleHat:
    def __init__(self, pwm, channel):
        self.pwm = pwm
        self.channel = channel
        self.pwm.frequency = 60

    def set_throttle(self, throttle):
        pulse = int(0xFFFF * abs(throttle))
        if throttle < 0:
            self.pwm.channels[self.channel + 5].duty_cycle = pulse
            self.pwm.channels[self.channel + 4].duty_cycle = 0
            self.pwm.channels[self.channel + 3].duty_cycle = 0xFFFF
        elif throttle > 0:
            self.pwm.channels[self.channel + 5].duty_cycle = pulse
            self.pwm.channels[self.channel + 4].duty_cycle = 0xFFFF
            self.pwm.channels[self.channel + 3].duty_cycle = 0
        else:
            self.pwm.channels[self.channel + 5].duty_cycle = 0
            self.pwm.channels[self.channel + 4].duty_cycle = 0
            self.pwm.channels[self.channel + 3].duty_cycle = 0

class AckermannController(Node):
    def __init__(self):
        super().__init__('ackermann_controller')
        
        # --- 하드웨어 초기화 ---
        i2c = busio.I2C(board.SCL, board.SDA)
        self.pca = PCA9685(i2c)
        self.pca.frequency = 60
        self.motor_hat = PWMThrottleHat(self.pca, channel=0)
        self.kit = ServoKit(channels=16, i2c=i2c, address=0x60)
        
        # ==========================================
        # ⚙️ [설정] 물리적 한계값 (사용자 요청 100% 반영)
        # ==========================================
        
        # 1. 서보모터 중앙값 (87도 적용)
        self.SERVO_CENTER = 87.0 
        
        # 2. 바퀴 회전 각도 물리적 한계 (좌우 30도 제한)
        # 87도 기준으로 57도~117도 사이에서만 움직이게 됩니다.
        self.SERVO_LIMIT_RANGE = 30.0
        self.SERVO_MIN = self.SERVO_CENTER - self.SERVO_LIMIT_RANGE 
        self.SERVO_MAX = self.SERVO_CENTER + self.SERVO_LIMIT_RANGE
        
        # 3. [절대 넘을 수 없는 한계치]
        self.MAX_THROTTLE = 1.0   # 속도는 무조건 1.0 이하 (w,x 눌러도 소용없음)
        self.MAX_STEERING = 0.6   # 회전은 무조건 0.6 이하 (e,c 눌러도 소용없음)

        # 상태 변수
        self.current_steer_angle = self.SERVO_CENTER
        self.current_throttle = 0.0
        self.STEER_SENSITIVITY = 5.0
        
        # 스마트 정지 변수
        self.stop_requested = False
        self.last_stop_signal_time = 0.0
        self.STOP_DELAY = 0.5
        
        self.subscription = self.create_subscription(
            Twist, '/cmd_vel', self.listener_callback, 10)
        
        self.timer = self.create_timer(0.1, self.control_loop)
        
        self.kit.servo[0].angle = self.SERVO_CENTER
        self.get_logger().info(f'✅ 설정 완료: 중앙 {self.SERVO_CENTER}도 | 최대속도 {self.MAX_THROTTLE} | 최대회전 {self.MAX_STEERING}')

    def listener_callback(self, msg):
        current_time = time.time()
        
        if msg.linear.x != 0 or msg.angular.z != 0:
            self.stop_requested = False
            
            # [1] 속도 제어: 1.0 넘으면 잘라버림
            input_throttle = msg.linear.x
            
            # 입력값이 제한보다 크면 로그로 알려줌 (확인용)
            if abs(input_throttle) > self.MAX_THROTTLE:
                self.get_logger().warn(f'속도 초과! 입력: {input_throttle} -> 강제조정: {self.MAX_THROTTLE}')
                
            self.current_throttle = max(min(input_throttle, self.MAX_THROTTLE), -self.MAX_THROTTLE)
            
            # [2] 조향 제어: 0.6 넘으면 잘라버림
            input_turn = msg.angular.z
            
            # 입력값이 제한보다 크면 로그로 알려줌
            if abs(input_turn) > self.MAX_STEERING:
                # self.get_logger().warn(f'회전 초과! 입력: {input_turn} -> 강제조정: {self.MAX_STEERING}')
                pass # 회전은 로그가 너무 많이 뜰 수 있어서 생략 (기능은 작동함)

            constrained_turn = max(min(input_turn, self.MAX_STEERING), -self.MAX_STEERING)
            
            if constrained_turn != 0:
                change = constrained_turn * self.STEER_SENSITIVITY
                self.current_steer_angle -= change
                
                # 물리적 각도 제한 (57도 ~ 117도)
                self.current_steer_angle = max(min(self.current_steer_angle, self.SERVO_MAX), self.SERVO_MIN)

        else:
            if not self.stop_requested:
                self.stop_requested = True
                self.last_stop_signal_time = current_time

    def control_loop(self):
        if self.stop_requested:
            if (time.time() - self.last_stop_signal_time) > self.STOP_DELAY:
                self.current_throttle = 0.0
        
        self.motor_hat.set_throttle(self.current_throttle)
        self.kit.servo[0].angle = self.current_steer_angle

    def stop_robot(self):
        self.motor_hat.set_throttle(0)
        self.kit.servo[0].angle = self.SERVO_CENTER

def main(args=None):
    rclpy.init(args=args)
    node = AckermannController()
    try:
        rclpy.spin(node)
    except KeyboardInterrupt:
        pass
    finally:
        node.stop_robot()
        if rclpy.ok():
            node.destroy_node()
            rclpy.shutdown()

if __name__ == '__main__':
    main()