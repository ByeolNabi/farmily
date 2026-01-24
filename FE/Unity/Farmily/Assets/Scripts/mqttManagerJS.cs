using UnityEngine;
using System.Runtime.InteropServices;
using TMPro;

// Version 3
public class mqttManagerJS : MonoBehaviour
{
    public string brokerAdress = "wss://i14d101.p.ssafy.io:443/mqtt";
    public string topicSub = "farmily/raspi/sensor/all";
    public TextMeshProUGUI temperatureText;
    public TextMeshProUGUI humidityText;
    public TextMeshProUGUI illuminanceText;
    public TextMeshProUGUI soilMoistureText;

    // Import the external JavaScript functions
    [DllImport("__Internal")] private static extern void mqttConnect(string broker, string topic);

    private void Start()
    {
        mqttConnect(brokerAdress, topicSub); // this calls the Javascript method
    }

    public void GetData(string message) // this is called from Javascript using the SendMessage method
    {
        Debug.Log("Received string from JavaScript: " + message);
        var data = JsonUtility.FromJson<MqttEnvelope>(message);
        if (data == null || data.payload == null)
        {
            Debug.LogWarning("MQTT JSON parse failed or payload missing.");
            return;
        }

        if (temperatureText != null)
        {
            temperatureText.text = data.payload.temperature.ToString("0.0");
        }

        if (humidityText != null)
        {
            humidityText.text = data.payload.humidity.ToString("0.0");
        }

        if (illuminanceText != null)
        {
            illuminanceText.text = data.payload.illuminance.ToString("0.0");
        }

        if (soilMoistureText != null)
        {
            soilMoistureText.text = data.payload.soil_moisture.ToString("0.0");
        }
    }

    [System.Serializable]
    private class MqttEnvelope
    {
        public MqttHeader header;
        public MqttPayload payload;
    }

    [System.Serializable]
    private class MqttHeader
    {
        public string msg_id;
        public string type;
        public string device_id;
        public string timestamp;
    }

    [System.Serializable]
    private class MqttPayload
    {
        public float temperature;
        public float humidity;
        public float illuminance;
        public float soil_moisture;
    }
}
