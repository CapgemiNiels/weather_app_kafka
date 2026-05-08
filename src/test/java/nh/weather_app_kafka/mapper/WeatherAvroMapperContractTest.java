package nh.weather_app_kafka.mapper;

import nh.weather_app_kafka.model.CurrentWeather;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherAvroMapperContractTest {

    @Test
    void mapperExistsAndExposesDomainToAvroMapMethod() throws Exception {
        Class<?> mapperClass = Class.forName("nh.weather_app_kafka.mapper.WeatherAvroMapper");
        Class<?> avroClass = Class.forName("nh.weather_app_kafka.avro.CurrentWeatherAvro");

        Method mapMethod = mapperClass.getDeclaredMethod("map", CurrentWeather.class);

        assertEquals(avroClass, mapMethod.getReturnType(),
                "Mapper must convert CurrentWeather to generated CurrentWeatherAvro");
    }

    @Test
    void mapperNormalizesTimeFieldToUtcInstantString() throws Exception {
        Class<?> mapperClass = Class.forName("nh.weather_app_kafka.mapper.WeatherAvroMapper");
        Object mapper = mapperClass.getDeclaredConstructor().newInstance();

        Method normalizeToUtcMethod = mapperClass.getDeclaredMethod("normalizeToUtc", String.class);
        Object normalized = normalizeToUtcMethod.invoke(mapper, "2026-05-07T13:15");

        assertNotNull(normalized);
        assertTrue(normalized instanceof String,
                "normalizeToUtc must return an ISO-8601 UTC string");

        String normalizedTime = (String) normalized;
        assertTrue(normalizedTime.endsWith("Z"),
                "Normalized timestamp must be UTC and end with 'Z'");
        assertNotNull(Instant.parse(normalizedTime),
                "Normalized timestamp must be parseable as an Instant");
    }
}

