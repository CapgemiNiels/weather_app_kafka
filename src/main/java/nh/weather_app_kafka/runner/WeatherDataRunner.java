package nh.weather_app_kafka.runner;

import nh.weather_app_kafka.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherDataRunner implements CommandLineRunner {

   private final WeatherService weatherService;

   @Autowired
   public WeatherDataRunner(WeatherService weatherService) {
      this.weatherService = weatherService;
   }

   @Override
   public void run(String... args) throws Exception {
      fetchWeatherData();
   }
   @Scheduled(cron = "0 */15 * * * *")
   public void fetchWeatherData() {
      weatherService.fetchAndDeserializeWeatherData();
   }
}
