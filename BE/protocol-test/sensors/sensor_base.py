import random
import time
import paho.mqtt.client as mqtt

BROKER = "localhost"
PORT = 1883

class SensorSimulator:
    def __init__(self, name, topic, initial_value, min_val, max_val):
        self.name = name
        self.topic = topic
        self.value = initial_value
        self.min_val = min_val
        self.max_val = max_val
        self.client = mqtt.Client()

    def connect(self):
        try:
            self.client.connect(BROKER, PORT, 60)
            print(f"[{self.name}] Connected to Broker")
        except Exception as e:
            print(f"[{self.name}] Connection Failed: {e}")

    def update_value(self):
        # -0.1, 0.0, +0.1 중 랜덤 선택하여 부드러운 변화
        change = random.choice([-0.1, 0.0, 0.1])
        self.value += change
        # 범위 제한 및 소수점 정리
        self.value = max(self.min_val, min(self.max_val, self.value))
        self.value = round(self.value, 1)

    def start(self):
        self.connect()
        while True:
            self.update_value()
            payload = f"{self.value}"
            self.client.publish(self.topic, payload)
            print(f"[{self.name}] Published to {self.topic}: {payload}")
            time.sleep(1)
