from sensor_base import SensorSimulator

if __name__ == "__main__":
    # 온도 센서: 초기값 24도, -10~40도 범위
    sensor = SensorSimulator("TempSensor", "home/livingroom/temperature", 24.0, -10.0, 40.0)
    sensor.start()
