import json
import time
import threading
import paho.mqtt.client as mqtt

from config import MQTT_BROKER, MQTT_PORT, MQTT_TOPIC_COMMAND, DEVICE_ID, MQTT_WS_PATH, MQTT_USE_WSS
from light import light_on, light_off

def handle_light_control(params):
	state = params.get("state")
	duration = int(params.get("duration_sec", 0))

	if state != "ON":
		light_off()
		return
		

	light_on(params.get("brightness", 100))
	time.sleep(duration)
	light_off()

def on_connect(client, userdata, flags, rc):
	print("[MQTT] connected rc=", rc)
	client.subscribe(MQTT_TOPIC_COMMAND)
	print("[MQTT] subscribed:", MQTT_TOPIC_COMMAND)

def on_disconnect(client, userdata, rc):
	print("[MQTT] disconnected rc=", rc)


def on_message(client, userdata, msg):
	try:
		data = json.loads(msg.payload.decode("utf-8"))
		header = data.get("header", {})
		payload = data.get("payload", {})

		if header.get("device_id") != DEVICE_ID:
			print("[SKIP] not for this device")
			return

		if payload.get("cmd") == "CONTROL_LIGHT":
			params = payload.get("params", {})
			handle_light_control(params)

	except Exception as e:
		print("[ERROR] on_message:", e)

def build_client():
	if MQTT_USE_WSS:
		client = mqtt.Client(transport="websockets")
		client.ws_set_options(path=MQTT_WS_PATH)
		client.tls_set()
	else:
		client = mqtt.Client()

	client.on_connect = on_connect
	client.on_disconnect = on_disconnect
	client.on_message = on_message

	client.reconnect_delay_set(min_delay=1, max_delay=30)
	return client


def main():
	client = build_client()

	while True:
		try:
			print("[MQTT] connecting to", MQTT_BROKER, MQTT_PORT, "wss" if MQTT_USE_WSS else "tcp")
			client.connect(MQTT_BROKER, MQTT_PORT, 60)
			client.loop_forever()
		except Exception as e:
			print("[MQTT] connect/loop error:", e)
			time.sleep(3)

if __name__ == "__main__":
	main()
