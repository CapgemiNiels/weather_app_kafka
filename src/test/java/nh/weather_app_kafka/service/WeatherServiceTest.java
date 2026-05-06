package nh.weather_app_kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    private static final String API_URL = "https://example.test/weather";
    private static final String TOPIC_NAME = "weather-test-topic";
    private static final String RESPONSE = "{\"current_weather\": {\"temperature\": 20.0}}";

    @Mock
    private KafkaTemplate<String, CurrentWeather> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiUrl", API_URL);
        ReflectionTestUtils.setField(weatherService, "topicName", TOPIC_NAME);
    }

    @Test
    void fetchAndDeserializeWeatherDataPublishesCurrentWeatherWhenApiAndDeserializationSucceed() throws Exception {
        WeatherResponse weatherResponse = new WeatherResponse();
        CurrentWeather currentWeather = buildCurrentWeather();
        weatherResponse.setCurrentWeather(currentWeather);

        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(RESPONSE);
        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenReturn(weatherResponse);

        weatherService.fetchAndDeserializeWeatherData();

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CurrentWeather> valueCaptor = ArgumentCaptor.forClass(CurrentWeather.class);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC_NAME), keyCaptor.capture(), valueCaptor.capture());
        assertEquals("weather-data", keyCaptor.getValue());
        assertEquals(currentWeather, valueCaptor.getValue());
    }

    @Test
    void fetchAndDeserializeWeatherDataDoesNotPublishWhenApiReturnsNull() {
        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(null);

        weatherService.fetchAndDeserializeWeatherData();

        verify(kafkaTemplate, never()).send(eq(TOPIC_NAME), eq("weather-data"), org.mockito.ArgumentMatchers.any(CurrentWeather.class));
    }

    @Test
    void fetchAndDeserializeWeatherDataDoesNotPublishWhenDeserializationReturnsNull() throws Exception {
        WeatherResponse weatherResponse = new WeatherResponse();

        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(RESPONSE);
        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenReturn(weatherResponse);

        weatherService.fetchAndDeserializeWeatherData();

        verify(kafkaTemplate, never()).send(eq(TOPIC_NAME), eq("weather-data"), org.mockito.ArgumentMatchers.any(CurrentWeather.class));
    }

    @Test
    void fetchWeatherDataReturnsResponseBodyWhenHttpCallSucceeds() {
        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(RESPONSE);

        String actualResponse = weatherService.fetchWeatherData();

        assertEquals(RESPONSE, actualResponse);
        verify(restTemplate, times(1)).getForObject(API_URL, String.class);
    }

    @Test
    void fetchWeatherDataReturnsNullWhenHttpCallThrows() {
        when(restTemplate.getForObject(API_URL, String.class)).thenThrow(new RuntimeException("API unavailable"));

        String actualResponse = weatherService.fetchWeatherData();

        assertNull(actualResponse);
    }

    @Test
    void deserializeWeatherDataReturnsCurrentWeatherWhenJsonIsValid() throws Exception {
        WeatherResponse weatherResponse = new WeatherResponse();
        CurrentWeather currentWeather = buildCurrentWeather();
        weatherResponse.setCurrentWeather(currentWeather);

        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenReturn(weatherResponse);

        CurrentWeather actualCurrentWeather = weatherService.deserializeWeatherData(RESPONSE);

        assertNotNull(actualCurrentWeather);
        assertEquals(currentWeather.getTemperature(), actualCurrentWeather.getTemperature());
        verify(objectMapper, times(1)).readValue(RESPONSE, WeatherResponse.class);
    }

    @Test
    void deserializeWeatherDataReturnsNullWhenJsonIsInvalid() throws Exception {
        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenThrow(new RuntimeException("Invalid JSON"));

        CurrentWeather actualCurrentWeather = weatherService.deserializeWeatherData(RESPONSE);

        assertNull(actualCurrentWeather);
    }

    @Test
    void pushWeatherDataToKafkaSendsExpectedKeyAndPayload() {
        CurrentWeather currentWeather = buildCurrentWeather();

        weatherService.pushWeatherDataToKafka(currentWeather);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CurrentWeather> valueCaptor = ArgumentCaptor.forClass(CurrentWeather.class);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC_NAME), keyCaptor.capture(), valueCaptor.capture());
        assertEquals("weather-data", keyCaptor.getValue());
        assertEquals(currentWeather, valueCaptor.getValue());
    }

    @Test
    void pushWeatherDataToKafkaSwallowsPublishFailureWithoutThrowing() {
        CurrentWeather currentWeather = buildCurrentWeather();
        doThrow(new RuntimeException("Kafka unavailable"))
                .when(kafkaTemplate)
                .send(TOPIC_NAME, "weather-data", currentWeather);

        assertDoesNotThrow(() -> weatherService.pushWeatherDataToKafka(currentWeather));
    }

    private CurrentWeather buildCurrentWeather() {
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTime("2026-05-06T10:30");
        currentWeather.setInterval(900);
        currentWeather.setTemperature(20.0);
        currentWeather.setWindspeed(10.0);
        currentWeather.setWinddirection(180);
        currentWeather.setIs_day(1);
        currentWeather.setWeathercode(3);
        return currentWeather;
    }
}