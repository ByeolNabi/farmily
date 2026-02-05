using UnityEngine;

public class WeatherBackgroundApplier : MonoBehaviour
{
    [SerializeField] private SpriteRenderer backgroundRenderer;
    [SerializeField] private Sprite sunnySprite;
    [SerializeField] private Sprite cloudySprite;
    [SerializeField] private Sprite rainySprite;
    [SerializeField] private Sprite snowySprite;

    private void Awake()
    {
        if (backgroundRenderer == null)
        {
            backgroundRenderer = GetComponent<SpriteRenderer>();
        }
    }

    private void OnEnable()
    {
        mqttManagerJS.WeatherChanged += ApplyWeather;
    }

    private void OnDisable()
    {
        mqttManagerJS.WeatherChanged -= ApplyWeather;
    }

    private void ApplyWeather(string weather)
    {
        Debug.Log("ApplyWeather: " + weather);
        if (backgroundRenderer == null)
        {
            return;
        }

        switch (weather)
        {
            case "SUNNY":
                backgroundRenderer.sprite = sunnySprite;
                break;
            case "CLOUDY":
            case "CLOUDS":
                backgroundRenderer.sprite = cloudySprite;
                break;
            case "RAINY":
                backgroundRenderer.sprite = rainySprite;
                break;
            case "SNOWY":
                backgroundRenderer.sprite = snowySprite;
                break;
            default:
                Debug.LogWarning("Unknown weather: " + weather);
                break;
        }
    }
}
