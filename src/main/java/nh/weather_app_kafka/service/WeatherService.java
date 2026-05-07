package nh.weather_app_kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);
    private static final String WEATHER_DATA_KEY = "weather-data";

    private final String topicName;
    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, CurrentWeather> kafkaTemplate;

    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper, KafkaTemplate<String, CurrentWeather> kafkaTemplate, @org.springframework.beans.factory.annotation.Value("${kafka.topic.name}") String topicName, @org.springframework.beans.factory.annotation.Value("${weather.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
        this.apiUrl = apiUrl;
    }

    public void fetchAndDeserializeWeatherData() {
        LOGGER.info("Starting weather fetch/parse/publish pipeline");
        String response = fetchWeatherData();
        if (response == null || response.isBlank()) {
            LOGGER.warn("Weather API response is null/blank, skipping publish");
            return;
        }

        CurrentWeather currentWeather = deserializeWeatherData(response);
        if (currentWeather == null) {
            LOGGER.warn("Parsed CurrentWeather is null, skipping publish");
            return;
        }

        pushWeatherDataToKafka(currentWeather);
    }

    String fetchWeatherData() {
        try {
            LOGGER.info("Fetching weather data from API: {}", apiUrl);
            String response = restTemplate.getForObject(apiUrl, String.class);
            LOGGER.debug("Fetched weather payload ({} chars)", response == null ? 0 : response.length());
            return response;
        } catch (RestClientException e) {
            LOGGER.error("Error fetching weather data from API: {}", apiUrl, e);
            return null;
        }
    }

    CurrentWeather deserializeWeatherData(String response) {
        try {
            WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);
            CurrentWeather currentWeather = weatherResponse.getCurrentWeather();

            if (currentWeather == null) {
                LOGGER.warn("WeatherResponse.currentWeather is null");
            } else {
                LOGGER.debug("Successfully deserialized CurrentWeather");
            }

            return currentWeather;
        } catch (JsonProcessingException e) {
            LOGGER.error("Error deserializing weather payload", e);
            return null;
        }
    }

    void pushWeatherDataToKafka(CurrentWeather currentWeather) {
        String messageKey = WEATHER_DATA_KEY + "-" + String.valueOf(Instant.now().toEpochMilli());
        LOGGER.info("Sending weather message to topic '{}' with key '{}'", topicName, messageKey);

        kafkaTemplate.send(topicName, messageKey, currentWeather).whenComplete((result, ex) -> {
            if (ex != null) {
                LOGGER.error("Failed to send weather message to topic '{}'", topicName, ex);
                return;
            }

            if (result == null || result.getRecordMetadata() == null) {
                LOGGER.info("Weather message sent successfully (metadata unavailable)");
                return;
            }

            LOGGER.info("Weather message sent successfully: topic={}, partition={}, offset={}", result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        });
    }
}