package nh.weather_app_kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);
    private static final String WEATHER_DATA_KEY = "weather-data";

    @Value("${kafka.topic.name}")
    private String topicName;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, CurrentWeather> kafkaTemplate;

    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper, KafkaTemplate<String, CurrentWeather> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void fetchAndDeserializeWeatherData() {
        LOGGER.info("Starting fetchAndDeserializeWeatherData");
        String response = fetchWeatherData();
        LOGGER.info("Fetched response: {}", response);
        if (response != null) {
            CurrentWeather currentWeather = deserializeWeatherData(response);
            if (currentWeather != null) {
                pushWeatherDataToKafka(currentWeather);
            } else {
                LOGGER.error("Deserialized CurrentWeather is null");
            }
        } else {
            LOGGER.error("Fetched response is null");
        }
    }

    String fetchWeatherData() {
        try {
            LOGGER.info("Fetching weather data from API: {}", apiUrl);
            return restTemplate.getForObject(apiUrl, String.class);
        } catch (Exception e) {
            LOGGER.error("Error fetching weather data", e);
            return null;
        }
    }

    CurrentWeather deserializeWeatherData(String response) {
        try {
            LOGGER.info("Deserializing weather data: {}", response);
            WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);
            return weatherResponse.getCurrentWeather();
        } catch (Exception e) {
            LOGGER.error("Error deserializing weather data", e);
            return null;
        }
    }

    void pushWeatherDataToKafka(CurrentWeather currentWeather) {
        try {
            LOGGER.info("Sending message with key: {} and value: {} to topic: {}", WEATHER_DATA_KEY, currentWeather, topicName);
            kafkaTemplate.send(topicName, WEATHER_DATA_KEY, currentWeather);
        } catch (Exception e) {
            LOGGER.error("Error pushing weather data to Kafka", e);
        }
    }
}