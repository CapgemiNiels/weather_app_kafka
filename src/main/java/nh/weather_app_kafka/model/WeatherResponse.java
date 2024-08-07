package nh.weather_app_kafka.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
   private CurrentWeather current_weather;

   // Getter and Setter
   public CurrentWeather getCurrent_weather() {
      return current_weather;
   }

   public void setCurrent_weather(CurrentWeather current_weather) {
      this.current_weather = current_weather;
   }
}
