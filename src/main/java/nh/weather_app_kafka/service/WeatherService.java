package nh.weather_app_kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {

    @Value("${kafka.topic.name}")
    private String topicName;

    @Value("${weather.api.url}")
    private String apiUrl;

    Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, CurrentWeather> kafkaTemplate;

    @Autowired
    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper, KafkaTemplate<String, CurrentWeather> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void fetchAndDeserializeWeatherData() {
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);
            if (weatherResponse != null && weatherResponse.getCurrent_weather() != null) {
                CurrentWeather currentWeather = weatherResponse.getCurrent_weather();
                String key = "weather-data";
                logger.info("Sending message with key: {} and value: {} to topic: {}", key, currentWeather, topicName);
                kafkaTemplate.send(topicName, key, currentWeather);
            } else {
                logger.error("WeatherResponse or CurrentWeather is null");
            }
        } catch (Exception e) {
            logger.error("Error fetching and sending weather data", e);
        }
    }
}