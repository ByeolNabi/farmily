"""
MQTT configuration for plant monitoring.
Contains topic definitions and plant threshold values.
"""

# ============================================================
# MVP Configuration (User ID 고정)
# ============================================================

MVP_USER_ID = 1  # 고정값, 나중에 수정 가능

# Active Time (08:00 ~ 20:00)
ACTIVE_TIME = {
    "start_hour": 8,
    "end_hour": 20,
}

# 조도 판단 설정
ILLUMINANCE_CONFIG = {
    "avg_window_sec": 1200,       # 20분 평균
    "station_proximity_m": 0.2,   # 도착 판정 거리 (20cm)
}

# 센서 데이터 집계 설정 (수정 가능)
SENSOR_AGGREGATION_CONFIG = {
    "interval_sec": 600,  # 10분 (테스트용, 나중에 3600으로 변경 가능)
    "api_base_url": "https://i14d101.p.ssafy.io",
    "api_path_template": "/api/sensors/{plant_id}",
}


# ============================================================
# MQTT Topics
# ============================================================

class Topics:
    """MQTT topic constants."""
    
    # Telemetry (Subscribe)
    SENSOR_ALL = "farmily/raspi/sensor/all"
    JETSON_POS = "farmily/jetson/lidar/pos"          # 로봇 위치
    
    # Device Events (Subscribe)
    DEVICE_ALL_SUB = "farmily/devices/+/event"
    
    # Commands (Publish)
    DEVICE_COMMAND = "farmily/devices/device_1/command"  # Jetson & Station
    UNITY_COMMAND = "farmily/unity/command"


# ============================================================
# Plant Thresholds
# ============================================================

PLANT_THRESHOLDS = {
    "default": {
        "temperature": {
            "min": 18.0,
            "max": 28.0,
            "optimal": 23.0,
            "unit": "°C"
        },
        "humidity": {
            "min": 40.0,
            "max": 70.0,
            "optimal": 55.0,
            "unit": "%"
        },
        "illuminance": {
            "min": 500.0,
            "max": 2000.0,
            "optimal": 1200.0,
            "unit": "lux"
        },
        "soil_moisture": {
            "min": 30.0,
            "max": 70.0,
            "optimal": 50.0,
            "unit": "%"
        },
    },
    # 추후 식물 종류별 설정 추가 가능
    # "tomato": { ... },
    # "basil": { ... },
}


def get_thresholds(plant_type: str = "default") -> dict:
    """Get threshold values for a specific plant type.
    
    Args:
        plant_type: Plant type identifier (default: "default")
        
    Returns:
        Dictionary containing threshold values
    """
    return PLANT_THRESHOLDS.get(plant_type, PLANT_THRESHOLDS["default"])


def check_sensor_status(sensor_data: dict, plant_type: str = "default") -> dict:
    """Check sensor values against thresholds.
    
    Args:
        sensor_data: Dictionary with temperature, humidity, illuminance, soil_moisture
        plant_type: Plant type for threshold lookup
        
    Returns:
        Dictionary with status for each sensor value
    """
    thresholds = get_thresholds(plant_type)
    status = {}
    
    for key, value in sensor_data.items():
        if key not in thresholds:
            continue
            
        th = thresholds[key]
        if value < th["min"]:
            status[key] = "low"
        elif value > th["max"]:
            status[key] = "high"
        else:
            status[key] = "normal"
    
    return status
