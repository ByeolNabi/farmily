from adafruit_motor import motor
from adafruit_pca9685 import PCA9685
from adafruit_servokit import ServoKit
import board
import busio
import time
from pynput import keyboard

# --- 하드웨어 설정 ---
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

i2c = busio.I2C(board.SCL, board.SDA)
pca = PCA9685(i2c)
pca.frequency = 60
motor_hat = PWMThrottleHat(pca, channel=0)

kit = ServoKit(channels=16, i2c=i2c, address=0x60)
pan = 90
kit.servo[0].angle = pan

# --- [수정됨] 키 상태 관리 클래스 ---
class KeyState:
    def __init__(self):
        self.pressed = set()         # 현재 물리적으로 눌려있는 키
        self.release_times = {}      # 키가 떨어진 시간 기록
        self.DEBOUNCE_DELAY = 0.1   # 0.15초 동안은 떨어진 걸 무시 (연속 입력 보정)

    def press(self, key):
        self.pressed.add(key)
        # 키가 다시 눌렸으니 릴리즈(뗀) 기록은 삭제합니다.
        if key in self.release_times:
            del self.release_times[key]

    def release(self, key):
        # 1. 물리적 상태: 즉시 제거합니다. (이전 코드의 버그 수정 부분)
        if key in self.pressed:
            self.pressed.remove(key)
        # 2. 시간 기록: 언제 뗐는지 기록해둡니다.
        self.release_times[key] = time.time()

    def is_active(self, key):
        # 1. 물리적으로 눌려있으면 당연히 True
        if key in self.pressed:
            return True
        
        # 2. 물리적으로는 뗐지만, 뗀 지 0.15초가 안 지났으면 
        #    "아직 눌려있다"고 거짓말을 합니다. (CPU 렉으로 인한 끊김 방지)
        if key in self.release_times:
            if time.time() - self.release_times[key] < self.DEBOUNCE_DELAY:
                return True
        
        return False

key_state = KeyState()

# --- 콜백 함수 ---
def on_press(key):
    try:
        if hasattr(key, 'char'):
            key_state.press(key.char)
    except AttributeError:
        pass

def on_release(key):
    try:
        if hasattr(key, 'char'):
            key_state.release(key.char)
        if key == keyboard.Key.esc:
            return False
    except KeyError:
        pass

# --- 리스너 시작 ---
listener = keyboard.Listener(on_press=on_press, on_release=on_release)
listener.start()

print("Motor Running... (Bug Fixed)")

try:
    while listener.is_alive():
        
        # --- 주행 제어 ---
        # W와 S가 동시에 눌린 것으로 인식될 때는 멈추도록 안전장치 추가
        forward = key_state.is_active('w')
        backward = key_state.is_active('s')

        if forward and not backward:
            motor_hat.set_throttle(1.0)
        elif backward and not forward:
            motor_hat.set_throttle(-1.0)
        else:
            # 둘 다 안 눌렸거나, 둘 다 눌렸으면 정지
            motor_hat.set_throttle(0)

        # --- 서보 제어 ---
        # 서보 각도가 튀지 않게 부드럽게 조절
        left = key_state.is_active('a')
        right = key_state.is_active('d')

        if left:
            pan -= 3  # 각도 변화량 (너무 빠르면 줄이세요)
            if pan < 30: pan = 30
            kit.servo[0].angle = pan
        elif right:
            pan += 3
            if pan > 150: pan = 150
            kit.servo[0].angle = pan
            
        time.sleep(0.05) # 20Hz 주기로 명령 전송

except KeyboardInterrupt:
    pass
finally:
    motor_hat.set_throttle(0)
    kit.servo[0].angle = 90
    pca.deinit()
    print("\nProgram stopped.")