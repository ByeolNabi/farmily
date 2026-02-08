import json
import ssl
import paho.mqtt.client as mqtt

from led_control import light_on_for, light_off, light_on

BROKER_HOST = "YOUR_BROKER"  
BROKER_PORT = 8883
CLIENT_ID = "raspi_station_led"

CMD_TOPIC = "farmily/raspi_station/command"


def on_connect(client, userdata, flags, rc):
	print("[MQTT] connected", rc)
	client.subscribe(CMD_TOPIC)

def on_message(client, userdata, msg):
	try:
		data = json.loads(msg.payload.decode())
	except Exception as e:
		print("[MQTT] JSON error", e)
		return


	payload = data.get("payload", {})
	cmd = payload.get("cmd")
	params = payload.get("params", {})

	if cmd != "CONTROL_LIGHT":
		return

	state = str(params.get("state", "")).upper()
	duration = params.get("duration_sec")

	if state == "ON":
		if duration is None:
			light_on()
		else:
			light_on_for(duration)

	elif state == "OFF":
		light_off()

def main():
	client = mqtt.Client(client_id=CLIENT_ID)
	client.tls_set(cert_reqs=ssl.CERT_REQUIRED)

	client.on_connect = on_connect
	client.on_message = on_message

	client.connect(BROKER_HOST, BROKER_PORT, 60)
	client.loop_forever()

if __name__ == "__main__":
	main()





