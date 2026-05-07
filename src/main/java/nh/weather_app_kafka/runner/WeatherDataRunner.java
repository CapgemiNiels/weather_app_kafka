package nh.weather_app_kafka.runner;

import nh.weather_app_kafka.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class WeatherDataRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherDataRunner.class);

    private final WeatherService weatherService;
    private final boolean runOnStartup;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public WeatherDataRunner(WeatherService weatherService, @Value("${weather.fetch.run-on-startup:true}") boolean runOnStartup) {
        this.weatherService = weatherService;
        this.runOnStartup = runOnStartup;
    }

    @Override
    public void run(String... args) {
        if (runOnStartup) {
            LOGGER.info("Running initial weather fetch on startup");
            fetchWeatherData();
        } else {
            LOGGER.info("Startup weather fetch disabled");
        }
    }

    @Scheduled(cron = "${weather.fetch.cron:0 */15 * * * *}")
    public void fetchWeatherData() {
        if (!running.compareAndSet(false, true)) {
            LOGGER.warn("Previous weather fetch still running; skipping this schedule tick");
            return;
        }

        try {
            LOGGER.info("Executing scheduled weather fetch");
            weatherService.fetchAndDeserializeWeatherData();
        } finally {
            running.set(false);
        }
    }
}