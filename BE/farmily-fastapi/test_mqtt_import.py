# Test script to verify MQTT module imports
import sys
sys.path.insert(0, ".")

try:
    from app.mqtt.schemas import MQTTMessage, SensorPayload, MQTTHeader
    print("✓ schemas.py imported successfully")
    
    from app.mqtt.config import Topics, PLANT_THRESHOLDS, check_sensor_status
    print("✓ config.py imported successfully")
    
    from app.mqtt.client import MQTTClient, mqtt_client
    print("✓ client.py imported successfully")
    
    from app.mqtt.handlers.sensor_handler import handle_sensor_data, register_sensor_handlers
    print("✓ sensor_handler.py imported successfully")
    
    from app.mqtt.publishers.command_publisher import publish_weather_update, publish_condition_update
    print("✓ command_publisher.py imported successfully")
    
    print("\n=== All MQTT module imports successful! ===")
    
except Exception as e:
    print(f"✗ Import error: {e}")
    import traceback
    traceback.print_exc()
    sys.exit(1)
