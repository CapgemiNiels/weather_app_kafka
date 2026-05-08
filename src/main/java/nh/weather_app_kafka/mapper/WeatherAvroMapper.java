package nh.weather_app_kafka.mapper;

import nh.weather_app_kafka.avro.CurrentWeatherAvro;
import nh.weather_app_kafka.model.CurrentWeather;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

@Component
public class WeatherAvroMapper {

    public CurrentWeatherAvro map(CurrentWeather currentWeather) {
        if (currentWeather == null) {
            throw new IllegalArgumentException("currentWeather must not be null");
        }

        return CurrentWeatherAvro.newBuilder()
                .setTime(normalizeToUtc(currentWeather.getTime()))
                .setInterval(currentWeather.getInterval())
                .setTemperature(currentWeather.getTemperature())
                .setWindspeed(currentWeather.getWindspeed())
                .setWinddirection(currentWeather.getWinddirection())
                .setIsDay(currentWeather.getIs_day())
                .setWeathercode(currentWeather.getWeathercode())
                .build();
    }

    String normalizeToUtc(String localTimestamp) {
        if (localTimestamp == null || localTimestamp.isBlank()) {
            throw new IllegalArgumentException("time must not be null or blank");
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(localTimestamp);
            return localDateTime.atOffset(ZoneOffset.UTC).toInstant().toString();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid weather timestamp: " + localTimestamp, e);
        }
    }
}

