package nh.weather_app_kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nh.weather_app_kafka.WeatherAppKafkaApplication;
import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = WeatherAppKafkaApplication.class, properties = {
        "spring.task.scheduling.enabled=false",
        "weather.fetch.run-on-startup=false",
        "spring.kafka.bootstrap-servers=127.0.0.1:19092",
        "kafka.topic.name=weather-test-topic",
        "weather.api.url=https://example.test/weather"
})
class WeatherServiceIntegrationTest {

    private static final String API_URL = "https://example.test/weather";
    private static final String TOPIC_NAME = "weather-test-topic";
    private static final String RESPONSE = "{\"current_weather\": {\"temperature\": 20.0}}";

    @Autowired
    private WeatherService weatherService;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, CurrentWeather> kafkaTemplate;

    @BeforeEach
    void resetCollaborators() {
        reset(restTemplate, objectMapper, kafkaTemplate);
    }

    @Test
    void weatherServiceEndToEndWithinSpringContextPublishesUsingInjectedCollaborators() throws Exception {
        WeatherResponse weatherResponse = new WeatherResponse();
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemperature(20.0);
        weatherResponse.setCurrentWeather(currentWeather);

        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(RESPONSE);
        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenReturn(weatherResponse);
        when(kafkaTemplate.send(eq(TOPIC_NAME), anyString(), eq(currentWeather)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        weatherService.fetchAndDeserializeWeatherData();

        verify(kafkaTemplate, times(1)).send(
                eq(TOPIC_NAME),
                argThat(key -> key != null && key.matches("weather-data-\\d{13}")),
                eq(currentWeather)
        );
    }
}


