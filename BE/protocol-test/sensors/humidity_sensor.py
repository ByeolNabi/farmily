from sensor_base import SensorSimulator

if __name__ == "__main__":
    # 습도 센서: 초기값 50%, 0~100% 범위
    sensor = SensorSimulator("HumiditySensor", "home/livingroom/humidity", 50.0, 0.0, 100.0)
    sensor.start()
