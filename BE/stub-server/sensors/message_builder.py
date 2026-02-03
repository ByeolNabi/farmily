import json
import uuid
import time
from datetime import datetime

class MessageBuilder:
    def __init__(self, device_id):
        self.device_id = device_id

    def _create_header(self, msg_type):
        """공통 헤더 생성 함수"""
        return {
            "msg_id": str(uuid.uuid4()),
            "type": msg_type,
            "device_id": self.device_id,
            "timestamp": datetime.now().isoformat()
        }

    def create_telemetry(self, sensor_data):
        """센서 데이터 메시지 생성"""
        return json.dumps({
            "header": self._create_header("telemetry"),
            "payload": sensor_data
        }, ensure_ascii=False)

    def create_command(self, action, target, value=None):
        """명령 메시지 생성 (앱 개발시 참고)"""
        payload = {"action": action, "target": target}
        if value is not None:
            payload["value"] = value
            
        return json.dumps({
            "header": self._create_header("command"),
            "payload": payload
        }, ensure_ascii=False)

    def create_event(self, event_type):
        """이벤트 메시지 생성 (WATER, TOUCH 등)"""
        return json.dumps({
            "header": self._create_header("event"),
            "payload": {
                "event": event_type
            }
        }, ensure_ascii=False)

    def create_control_light(self, state, duration_sec=None):
        """조명 제어 명령 생성"""
        params = {"state": state}
        if duration_sec is not None:
            params["duration_sec"] = duration_sec
            
        return json.dumps({
            "header": self._create_header("command"),
            "payload": {
                "cmd": "CONTROL_LIGHT",
                "params": params
            }
        }, ensure_ascii=False)

# --- 사용 예시 (Raspi - SmartFarm) ---
farm_builder = MessageBuilder(device_id="Raspi_SmartFarm_01")

# 1. 센서 값 읽기 (예시)
current_sensors = {
    "temperature": 25.3,
    "humidity": 55,
    "soil": 80
}

# 2. JSON 생성
mqtt_msg = farm_builder.create_telemetry(current_sensors)

# 3. 출력 확인
print(f"전송할 메시지:\n{mqtt_msg}")

# 이후 client.publish("farm/telemetry", mqtt_msg) 로 전송