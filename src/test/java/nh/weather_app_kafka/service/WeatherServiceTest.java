package nh.weather_app_kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nh.weather_app_kafka.avro.CurrentWeatherAvro;
import nh.weather_app_kafka.mapper.WeatherAvroMapper;
import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    private static final String API_URL = "https://example.test/weather";
    private static final String TOPIC_NAME = "weather-test-topic";
    private static final String RESPONSE = "{\"current_weather\": {\"temperature\": 20.0}}";

    @Mock
    private KafkaTemplate<String, CurrentWeatherAvro> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherAvroMapper weatherAvroMapper;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(restTemplate, objectMapper, weatherAvroMapper, kafkaTemplate, TOPIC_NAME, API_URL);
    }

    @Test
    void fetchAndDeserializeWeatherDataPublishesCurrentWeatherWhenApiAndDeserializationSucceed() throws Exception {
        WeatherResponse weatherResponse = new WeatherResponse();
        CurrentWeather currentWeather = buildCurrentWeather();
        CurrentWeatherAvro currentWeatherAvro = buildCurrentWeatherAvro();
        weatherResponse.setCurrentWeather(currentWeather);

        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(RESPONSE);
        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenReturn(weatherResponse);
        when(weatherAvroMapper.map(currentWeather)).thenReturn(currentWeatherAvro);
        when(kafkaTemplate.send(eq(TOPIC_NAME), anyString(), eq(currentWeatherAvro)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        weatherService.fetchAndDeserializeWeatherData();

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CurrentWeatherAvro> valueCaptor = ArgumentCaptor.forClass(CurrentWeatherAvro.class);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC_NAME), keyCaptor.capture(), valueCaptor.capture());
        assertTrue(keyCaptor.getValue().matches("weather-data-\\d{13}"));
        assertEquals(currentWeatherAvro, valueCaptor.getValue());
    }

    @Test
    void fetchAndDeserializeWeatherDataDoesNotPublishWhenApiReturnsNull() {
        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(null);

        weatherService.fetchAndDeserializeWeatherData();

        verify(kafkaTemplate, never()).send(eq(TOPIC_NAME), anyString(), any(CurrentWeatherAvro.class));
    }

    @Test
    void fetchAndDeserializeWeatherDataDoesNotPublishWhenApiReturnsBlank() {
        when(restTemplate.getForObject(API_URL, String.class)).thenReturn("   ");

        weatherService.fetchAndDeserializeWeatherData();

        verify(kafkaTemplate, never()).send(eq(TOPIC_NAME), anyString(), any(CurrentWeatherAvro.class));
    }

    @Test
    void fetchAndDeserializeWeatherDataDoesNotPublishWhenDeserializationReturnsNull() throws Exception {
        WeatherResponse weatherResponse = new WeatherResponse();

        when(restTemplate.getForObject(API_URL, String.class)).thenReturn(RESPONSE);
        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenReturn(weatherResponse);

        weatherService.fetchAndDeserializeWeatherData();

        verify(kafkaTemplate, never()).send(eq(TOPIC_NAME), anyString(), any(CurrentWeatherAvro.class));
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
        when(restTemplate.getForObject(API_URL, String.class)).thenThrow(new RestClientException("API unavailable"));

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
        when(objectMapper.readValue(RESPONSE, WeatherResponse.class)).thenThrow(new JsonProcessingException("Invalid JSON") {
        });

        CurrentWeather actualCurrentWeather = weatherService.deserializeWeatherData(RESPONSE);

        assertNull(actualCurrentWeather);
    }

    @Test
    void pushWeatherDataToKafkaSendsExpectedKeyAndPayload() {
        CurrentWeatherAvro currentWeather = buildCurrentWeatherAvro();
        when(kafkaTemplate.send(eq(TOPIC_NAME), anyString(), eq(currentWeather)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        weatherService.pushWeatherDataToKafka(currentWeather);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CurrentWeatherAvro> valueCaptor = ArgumentCaptor.forClass(CurrentWeatherAvro.class);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC_NAME), keyCaptor.capture(), valueCaptor.capture());
        assertTrue(keyCaptor.getValue().matches("weather-data-\\d{13}"));
        assertEquals(currentWeather, valueCaptor.getValue());
    }

    @Test
    void pushWeatherDataToKafkaFailsFastWhenKafkaSendFails() {
        CurrentWeatherAvro currentWeather = buildCurrentWeatherAvro();
        CompletableFuture<SendResult<String, CurrentWeatherAvro>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka unavailable"));
        when(kafkaTemplate.send(eq(TOPIC_NAME), anyString(), eq(currentWeather))).thenReturn(failedFuture);

        assertThrows(IllegalStateException.class, () -> weatherService.pushWeatherDataToKafka(currentWeather));
    }

    @Test
    void pushWeatherDataToKafkaHandlesCompletedFutureWithNullMetadataWithoutThrowing() {
        CurrentWeatherAvro currentWeather = buildCurrentWeatherAvro();
        when(kafkaTemplate.send(eq(TOPIC_NAME), anyString(), eq(currentWeather)))
                .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> weatherService.pushWeatherDataToKafka(currentWeather));
    }

    @Test
    void pushWeatherDataToKafkaHandlesCompletedFutureWithMetadataWithoutThrowing() {
        CurrentWeatherAvro currentWeather = buildCurrentWeatherAvro();
        RecordMetadata metadata = mock(RecordMetadata.class);
        when(metadata.topic()).thenReturn(TOPIC_NAME);
        when(metadata.partition()).thenReturn(0);
        when(metadata.offset()).thenReturn(42L);

        SendResult<String, CurrentWeatherAvro> sendResult = mock(SendResult.class);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);

        when(kafkaTemplate.send(eq(TOPIC_NAME), anyString(), eq(currentWeather)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

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

    private CurrentWeatherAvro buildCurrentWeatherAvro() {
        return CurrentWeatherAvro.newBuilder()
                .setTime("2026-05-06T10:30:00Z")
                .setInterval(900)
                .setTemperature(20.0)
                .setWindspeed(10.0)
                .setWinddirection(180)
                .setIsDay(1)
                .setWeathercode(3)
                .build();
    }
}