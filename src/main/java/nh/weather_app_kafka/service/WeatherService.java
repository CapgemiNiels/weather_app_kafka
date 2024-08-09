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
        logger.info("Starting fetchAndDeserializeWeatherData");
        String response = fetchWeatherData();
        logger.info("Fetched response: {}", response);
        if (response != null) {
            CurrentWeather currentWeather = deserializeWeatherData(response);
            if (currentWeather != null) {
                pushWeatherDataToKafka(currentWeather);
            } else {
                logger.error("Deserialized CurrentWeather is null");
            }
        } else {
            logger.error("Fetched response is null");
        }
    }

    private String fetchWeatherData() {
        try {
            logger.info("Fetching weather data from API: {}", apiUrl);
            return restTemplate.getForObject(apiUrl, String.class);
        } catch (Exception e) {
            logger.error("Error fetching weather data", e);
            return null;
        }
    }

    private CurrentWeather deserializeWeatherData(String response) {
        try {
            logger.info("Deserializing weather data: {}", response);
            WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);
            return weatherResponse.getCurrentWeather();
        } catch (Exception e) {
            logger.error("Error deserializing weather data", e);
            return null;
        }
    }

    private void pushWeatherDataToKafka(CurrentWeather currentWeather) {
        try {
            String key = "weather-data";
            logger.info("Sending message with key: {} and value: {} to topic: {}", key, currentWeather, topicName);
            kafkaTemplate.send(topicName, key, currentWeather);
        } catch (Exception e) {
            logger.error("Error pushing weather data to Kafka", e);
        }
    }
}