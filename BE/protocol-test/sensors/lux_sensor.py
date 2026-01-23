from sensor_base import SensorSimulator

if __name__ == "__main__":
    # 조도 센서: 초기값 300 lux, 0~1000 lux 범위
    sensor = SensorSimulator("LuxSensor", "home/livingroom/lux", 300.0, 0.0, 1000.0)
    sensor.start()
