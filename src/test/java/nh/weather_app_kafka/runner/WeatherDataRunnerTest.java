package nh.weather_app_kafka.runner;

import nh.weather_app_kafka.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WeatherDataRunnerTest {

    @Mock
    private WeatherService weatherService;

    @Test
    void runInvokesWeatherFetchOnceWhenStartupEnabled() throws Exception {
        WeatherDataRunner weatherDataRunner = new WeatherDataRunner(weatherService, true);

        weatherDataRunner.run();

        verify(weatherService, times(1)).fetchAndDeserializeWeatherData();
    }

    @Test
    void runSkipsWeatherFetchWhenStartupDisabled() throws Exception {
        WeatherDataRunner weatherDataRunner = new WeatherDataRunner(weatherService, false);

        weatherDataRunner.run();

        verifyNoInteractions(weatherService);
    }

    @Test
    void fetchWeatherDataInvokesWeatherFetchOnce() {
        WeatherDataRunner weatherDataRunner = new WeatherDataRunner(weatherService, true);

        weatherDataRunner.fetchWeatherData();

        verify(weatherService, times(1)).fetchAndDeserializeWeatherData();
    }

    @Test
    void fetchWeatherDataSkipsWhenPreviousRunIsStillActive() {
        WeatherDataRunner weatherDataRunner = new WeatherDataRunner(weatherService, true);
        AtomicBoolean running = (AtomicBoolean) ReflectionTestUtils.getField(weatherDataRunner, "running");
        assertNotNull(running);
        running.set(true);

        weatherDataRunner.fetchWeatherData();

        verifyNoInteractions(weatherService);
    }

    @Test
    void fetchWeatherDataResetsRunningFlagWhenWeatherServiceThrows() {
        WeatherDataRunner weatherDataRunner = new WeatherDataRunner(weatherService, true);
        doThrow(new RuntimeException("simulated failure")).when(weatherService).fetchAndDeserializeWeatherData();

        assertThrows(RuntimeException.class, weatherDataRunner::fetchWeatherData);

        doNothing().when(weatherService).fetchAndDeserializeWeatherData();
        weatherDataRunner.fetchWeatherData();
        verify(weatherService, times(2)).fetchAndDeserializeWeatherData();
    }
}