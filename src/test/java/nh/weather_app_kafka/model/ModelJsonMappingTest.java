package nh.weather_app_kafka.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelJsonMappingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void currentWeatherJsonMappingPreservesWireCompatibilityForSnakeCaseFields() throws Exception {
        String json = """
                {
                  "time": "2026-05-06T10:30",
                  "interval": 900,
                  "temperature": 10.5,
                  "windspeed": 13.0,
                  "winddirection": 12,
                  "is_day": 1,
                  "weathercode": 3
                }
                """;

        CurrentWeather currentWeather = objectMapper.readValue(json, CurrentWeather.class);
        JsonNode serialized = objectMapper.readTree(objectMapper.writeValueAsString(currentWeather));

        assertNotNull(currentWeather);
        assertEquals("2026-05-06T10:30", serialized.get("time").asText());
        assertEquals(900, serialized.get("interval").asInt());
        assertEquals(10.5, serialized.get("temperature").asDouble());
        assertEquals(13.0, serialized.get("windspeed").asDouble());
        assertEquals(12, serialized.get("winddirection").asInt());
        assertEquals(1, serialized.get("is_day").asInt());
        assertEquals(3, serialized.get("weathercode").asInt());
    }

    @Test
    void weatherResponseDeserializesCurrentWeatherFromCurrentWeatherJsonProperty() throws Exception {
        String json = """
                {
                  "current_weather": {
                    "time": "2026-05-06T10:30",
                    "temperature": 10.5
                  }
                }
                """;

        WeatherResponse weatherResponse = objectMapper.readValue(json, WeatherResponse.class);

        assertNotNull(weatherResponse.getCurrentWeather());
        assertEquals("2026-05-06T10:30", weatherResponse.getCurrentWeather().getTime());
        assertEquals(10.5, weatherResponse.getCurrentWeather().getTemperature());
    }

    @Test
    void currentWeatherToStringContainsKeyFieldsForDebugging() {
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTime("2026-05-06T10:30");
        currentWeather.setTemperature(10.5);

        String value = currentWeather.toString();

        assertNotNull(value);
        assertTrue(value.contains("time='2026-05-06T10:30'"));
        assertTrue(value.contains("temperature=10.5"));
    }
}

