package nh.weather_app_kafka.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherServiceAvroContractTest {

    @Test
    void pushMethodMustAcceptGeneratedAvroRecordType() throws Exception {
        Class<?> serviceClass = WeatherService.class;
        Class<?> avroClass = Class.forName("nh.weather_app_kafka.avro.CurrentWeatherAvro");

        Method pushMethod = serviceClass.getDeclaredMethod("pushWeatherDataToKafka", avroClass);

        assertNotNull(pushMethod,
                "WeatherService must publish generated Avro record instead of JSON model");
    }

    @Test
    void weatherDataKeyPrefixRemainsStable() throws Exception {
        Field keyField = WeatherService.class.getDeclaredField("WEATHER_DATA_KEY");
        keyField.setAccessible(true);

        Object fieldValue = keyField.get(null);

        assertEquals("weather-data", fieldValue,
                "Existing key prefix must remain unchanged for downstream key compatibility");
    }

    @Test
    void serviceMustDependOnDedicatedAvroMapper() {
        boolean mapperFieldExists = Arrays.stream(WeatherService.class.getDeclaredFields())
                .map(Field::getType)
                .map(Class::getName)
                .anyMatch("nh.weather_app_kafka.mapper.WeatherAvroMapper"::equals);

        assertTrue(mapperFieldExists,
                "WeatherService should use a dedicated WeatherAvroMapper to isolate mapping concerns");
    }
}

