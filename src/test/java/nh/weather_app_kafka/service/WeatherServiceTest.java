package nh.weather_app_kafka.service;

import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @Mock
    private KafkaTemplate<String, CurrentWeather> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${kafka.topic.name}")
    private String topicName;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchAndDeserializeWeatherData() throws Exception {
        // Arrange
        String response = "{\"current_weather\": {\"temperature\": 20.0}}";
        WeatherResponse weatherResponse = new WeatherResponse();
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemperature(20.0);
        weatherResponse.setCurrentWeather(currentWeather);

        when(restTemplate.getForObject(apiUrl, String.class)).thenReturn(response);
        when(objectMapper.readValue(response, WeatherResponse.class)).thenReturn(weatherResponse);

        // Act
        weatherService.fetchAndDeserializeWeatherData();

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CurrentWeather> valueCaptor = ArgumentCaptor.forClass(CurrentWeather.class);
        verify(kafkaTemplate, times(1)).send(eq(topicName), keyCaptor.capture(), valueCaptor.capture());

        assertNotNull(keyCaptor.getValue());
        assertEquals(currentWeather, valueCaptor.getValue());
    }

    @Test
    public void testFetchWeatherData() {
        // Arrange
        String expectedResponse = "{\"current_weather\": {\"temperature\": 20.0}}";
        when(restTemplate.getForObject(apiUrl, String.class)).thenReturn(expectedResponse);

        // Act
        String actualResponse = weatherService.fetchWeatherData();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).getForObject(apiUrl, String.class);
    }

    @Test
    void testDeserializeWeatherData() throws Exception {
        // Arrange
        String response = "{\"current_weather\": {\"temperature\": 20.0}}";
        WeatherResponse weatherResponse = new WeatherResponse();
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemperature(20.0);
        weatherResponse.setCurrentWeather(currentWeather);

        when(objectMapper.readValue(response, WeatherResponse.class)).thenReturn(weatherResponse);

        // Act
        CurrentWeather actualCurrentWeather = weatherService.deserializeWeatherData(response);

        // Assert
        assertNotNull(actualCurrentWeather);
        assertEquals(20.0, actualCurrentWeather.getTemperature());
        verify(objectMapper, times(1)).readValue(response, WeatherResponse.class);
    }

    @Test
    void testPushWeatherDataToKafka() {
        // Arrange
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemperature(20.0);
        currentWeather.setTime("2023-10-01T10:00:00");
        currentWeather.setWindspeed(10.0);
        currentWeather.setWinddirection(180);

        // Act
        weatherService.pushWeatherDataToKafka(currentWeather);

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CurrentWeather> valueCaptor = ArgumentCaptor.forClass(CurrentWeather.class);
        verify(kafkaTemplate, times(1)).send(eq(topicName), keyCaptor.capture(), valueCaptor.capture());

        assertEquals("weather-data", keyCaptor.getValue());
        assertEquals(currentWeather, valueCaptor.getValue());
    }
}