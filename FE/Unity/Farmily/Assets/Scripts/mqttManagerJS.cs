using UnityEngine;
using System.Runtime.InteropServices;
using TMPro;
using System;

// Version 3
public class mqttManagerJS : MonoBehaviour
{
    public string brokerAdress = "wss://i14d101.p.ssafy.io:443/mqtt";
    // public string topicSub = "farmily/raspi/sensor/all";
    private readonly string[] topics = {
        "farmily/raspi/sensor/all",
        "farmily/devices/device_1/weather",
    };
    
    public static event Action<string> WeatherChanged;

    public TextMeshProUGUI temperatureText;
    public TextMeshProUGUI humidityText;
    public TextMeshProUGUI illuminanceText;
    public TextMeshProUGUI soilMoistureText;

    // Import the external JavaScript functions
    [DllImport("__Internal")] private static extern void mqttConnect(string broker, string topic);

    private void Start()
    {
        var topicSubCsv = string.Join(",", topics);
        mqttConnect(brokerAdress, topicSubCsv); // this calls the Javascript method
    }

    public void GetData(string message) // this is called from Javascript using the SendMessage method
    {
        // Debug.Log("Received string from JavaScript: " + message);
        var data = JsonUtility.FromJson<MqttEnvelope>(message);
        if (data == null || data.payload == null)
        {
            Debug.LogWarning("MQTT JSON parse failed or payload missing.");
            return;
        }

        // weather 메시지 처리
        if(data.header.type == "weather" && data.payload.@params != null){
            Debug.Log("Weather message: " + data.payload.@params.weather);
            WeatherChanged?.Invoke(data.payload.@params.weather);
            return;
        }

        // sensor 메시지 처리 - 온도, 습도, 조도, 토양 습도
        if(data.header.type == "telemetry"){
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

            return;
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
        public string cmd;
        public WeatherParams @params;

        public float temperature;
        public float humidity;
        public float illuminance;
        public float soil_moisture;
    }

    [System.Serializable]
    private class WeatherParams
    {
        public string weather;
        public int plantId;
    }
}
