import json
import uuid
from datetime import datetime, timezone

class MessageBuilder:
    def __init__(self, device_id: str):
        self.device_id = device_id

    def create_telemetry(self, payload: dict) -> str:
        msg = {
            "header": {
                "msg_id": str(uuid.uuid4()),
                "type": "telemetry",
                "device_id": self.device_id,
                "timestamp": datetime.now(timezone.utc).astimezone().isoformat()
            },
            "payload": payload
        }
        return json.dumps(msg, ensure_ascii=False)
