package nh.weather_app_kafka.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
   @JsonProperty("current_weather")
   private CurrentWeather currentWeather;

   // Getter and Setter
   public CurrentWeather getCurrentWeather() {
      return currentWeather;
   }

   public void setCurrentWeather(CurrentWeather currentWeather) {
      this.currentWeather = currentWeather;
   }
}
