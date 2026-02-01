"""
MQTT configuration for plant monitoring.
Contains topic definitions and plant threshold values.
"""

# ============================================================
# MQTT Topics
# ============================================================

class Topics:
    """MQTT topic constants."""
    
    # Sensor data from Raspberry Pi
    SENSOR_ALL = "farmily/raspi/sensor/all"
    
    # Commands to Unity display
    UNITY_COMMAND = "farmily/unity/command"
    
    # Events from sensors
    SENSOR_EVENT = "farmily/raspi/event"


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
