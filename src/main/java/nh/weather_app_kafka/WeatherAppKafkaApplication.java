package nh.weather_app_kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherAppKafkaApplication {

   public static void main(String[] args) {
      SpringApplication.run(WeatherAppKafkaApplication.class, args);
   }

}
