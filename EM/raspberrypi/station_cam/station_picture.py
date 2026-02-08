import json
import os
import subprocess
from datetime import datetime
import requests
from message_builder import MessageBuilder


# =====================
# option value
# =====================
DEVICE_ID = "rsapi_station"

# server upload endpoint
# ex: http://i14d101.p.ssafy.io:8080/farmily/raspi/cam
HTTP_URL = "http://i14d101.p.ssafy.io:8081/api/v1/timelapse"

# save picture
SAVE_DIR = "/home/ssafy/station_cam/shots"
os.makedirs(SAVE_DIR, exist_ok=True)

# fswebcam
VIDEO_DEV = "/dev/video0"

# fswebcam
RESOLUTION = "800x600"


def capture_photo() -> str:
    ts = datetime.now().strftime("%Y%m%d_%H%M%S")
    filepath = os.path.join(SAVE_DIR, f"plant_{ts}.jpg")

    cmd = [
        "fswebcam",
        "-d", VIDEO_DEV,
        "-r", RESOLUTION,
        "--no-banner",
        "--jpeg", "95",
        filepath
    ]

    result = subprocess.run(cmd, capture_output=True, text=True)

    if result.returncode != 0 or not os.path.exists(filepath):
        raise RuntimeError(
            "fswebcam fail\n"
            f"stdout: {result.stdout}\n"
            f"stderr: {result.stderr}"
        )

    return filepath


def post_http(filepath: str):
    builder = MessageBuilder(device_id=DEVICE_ID)  
    plant_id = 1

    payload = {
	"plant_id": plant_id,
        "image": {
            "filename": os.path.basename(filepath),
            "captured_at": datetime.now().isoformat(),
        }
    }

    message_json = builder.create_telemetry(payload)

    # recommend: multipart/form-data
    # - metadata: JSON
    with open(filepath, "rb") as f:
        files = {
            "image": (os.path.basename(filepath), f, "image/jpeg")
        }
        data = {
	    "plant_id": str(plant_id),
            "metadata": message_json
        }

        resp = requests.post(HTTP_URL, data=data, files=files, timeout=30)

    print("HTTP status:", resp.status_code)
    if resp.status_code >= 400:
        print("response:", resp.text)
        resp.raise_for_status()


def main():
    filepath = capture_photo()
    post_http(filepath)


if __name__ == "__main__":
    main()
