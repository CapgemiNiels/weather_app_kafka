package nh.weather_app_kafka.runner;

import nh.weather_app_kafka.service.WeatherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherDataRunner implements CommandLineRunner, EnvironmentAware {

   private final WeatherService weatherService;
   private Environment environment;

   public WeatherDataRunner(WeatherService weatherService) {
      this.weatherService = weatherService;
   }

   @Override
   public void run(String... args) {
      if (environment != null && !environment.getProperty("spring.task.scheduling.enabled", Boolean.class, true)) {
         return;
      }
      fetchWeatherData();
   }

   @Scheduled(cron = "0 */15 * * * *")
   public void fetchWeatherData() {
      weatherService.fetchAndDeserializeWeatherData();
   }

   @Override
   public void setEnvironment(Environment environment) {
      this.environment = environment;
   }
}
