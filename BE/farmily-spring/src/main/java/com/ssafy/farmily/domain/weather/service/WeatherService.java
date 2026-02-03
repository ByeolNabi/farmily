package com.ssafy.farmily.domain.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.farmily.domain.weather.dto.WeatherResponse;
import com.ssafy.farmily.global.mqtt.MqttGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final MqttGateway mqttGateway;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${farmily.weather.url}")
    private String weatherApiUrl;

    @Value("${farmily.weather.api-key}")
    private String apiKey;

    /**
     * 날씨 조회 및 MQTT 전송
     */
    /**
     * 날씨 조회 및 MQTT 전송
     * @param plantId 식물 ID (null이면 공통 토픽 robot/weather, 값이 있으면 robot/weather/{plantId})
     */
    public WeatherResponse getCurrentWeather(Double lat, Double lon, Long plantId) {
        if (lat == null || lon == null) {
            // 기본값 (삼성전자 구미 2사업장)
            lat = 36.1082;  
            lon = 128.4140; 
        }

        // 1. OpenWeatherMap API 호출
        String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric") // 섭씨 온도
                .toUriString();

        WeatherResponse weatherResponse = restTemplate.getForObject(url, WeatherResponse.class);
        
        if (weatherResponse != null) {
            // 2. MQTT 메시지 발행
            publishWeatherToMqtt(weatherResponse, plantId);
        }

        return weatherResponse;
    }

    private void publishWeatherToMqtt(WeatherResponse weather, Long plantId) {
        try {
            // MQTT Payload 구성
            // ex: {"header":{...}, "payload":{"cmd":"UPDATE_WEATHER", "params":{"weather":"SUNNY"}}}
            Map<String, Object> payloadMap = new HashMap<>();
            Map<String, Object> params = new HashMap<>();
            
            // OpenWeatherMap의 main 날씨 (Clear, Clouds, Rain, Snow 등)
            String mainWeather = weather.getWeather().isEmpty() ? "Clear" : weather.getWeather().get(0).getMain();
            params.put("weather", mainWeather.toUpperCase()); // 대문자로 변환 (SUNNY, RAINY 등 매핑 필요 시 로직 추가)
            if (plantId != null) {
                params.put("plantId", plantId);
            }

            Map<String, Object> innerPayload = new HashMap<>();
            innerPayload.put("cmd", "UPDATE_WEATHER");
            innerPayload.put("params", params);

            payloadMap.put("header", Map.of("timestamp", System.currentTimeMillis()));
            payloadMap.put("payload", innerPayload);

            String jsonMessage = objectMapper.writeValueAsString(payloadMap);
            
            // MQTT 전송
            // plantId가 있으면 "robot/weather/{plantId}", 없으면 "robot/weather"
            String topic = (plantId != null) ? "robot/weather/" + plantId : "robot/weather";
            
            mqttGateway.sendToMqtt(jsonMessage, topic);
            log.info("Sent Weather MQTT to {}: {}", topic, jsonMessage);

        } catch (Exception e) {
            log.error("Failed to publish MQTT message", e);
        }
    }
}
