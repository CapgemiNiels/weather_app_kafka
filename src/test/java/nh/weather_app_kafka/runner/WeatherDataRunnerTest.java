package nh.weather_app_kafka.runner;

import nh.weather_app_kafka.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WeatherDataRunnerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherDataRunner weatherDataRunner;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRun() throws Exception {
        // Act
        weatherDataRunner.run();

        // Assert
        verify(weatherService, times(1)).fetchAndDeserializeWeatherData();
    }

    @Test
    void testScheduledFetchWeatherData() {
        // Act
        weatherDataRunner.fetchWeatherData();

        // Assert
        verify(weatherService, times(1)).fetchAndDeserializeWeatherData();
    }
}