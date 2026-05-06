package nh.weather_app_kafka.runner;

import nh.weather_app_kafka.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WeatherDataRunnerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherDataRunner weatherDataRunner;

    @Test
    void runInvokesWeatherFetchOnce() throws Exception {
        weatherDataRunner.run();

        verify(weatherService, times(1)).fetchAndDeserializeWeatherData();
    }

    @Test
    void fetchWeatherDataInvokesWeatherFetchOnce() {
        weatherDataRunner.fetchWeatherData();

        verify(weatherService, times(1)).fetchAndDeserializeWeatherData();
    }
}