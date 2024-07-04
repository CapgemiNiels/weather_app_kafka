package nh.weather_app_kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nh.weather_app_kafka.model.CurrentWeather;
import nh.weather_app_kafka.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

   private final RestTemplate restTemplate;
   private final ObjectMapper objectMapper;

   @Autowired
   public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper) {
      this.restTemplate = restTemplate;
      this.objectMapper = objectMapper;
   }

   public void fetchAndDeserializeWeatherData() {
      try {
         String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=51.42&longitude=5.46&current_weather=true";
         String response = restTemplate.getForObject(apiUrl, String.class);
         WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);
         CurrentWeather currentWeather = weatherResponse.getCurrent_weather();
         System.out.println(currentWeather.toString());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}