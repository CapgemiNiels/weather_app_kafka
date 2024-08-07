package nh.weather_app_kafka.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {
   private LocalDateTime timestamp;
   private String time;
   private int interval;
   private double temperature;
   private double windspeed;
   private int winddirection;
   private int is_day;
   private int weathercode;

   // Getters and Setters

   public String getTime() {
      return time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public int getInterval() {
      return interval;
   }

   public void setInterval(int interval) {
      this.interval = interval;
   }

   public double getTemperature() {
      return temperature;
   }

   public void setTemperature(double temperature) {
      this.temperature = temperature;
   }

   public double getWindspeed() {
      return windspeed;
   }

   public void setWindspeed(double windspeed) {
      this.windspeed = windspeed;
   }

   public int getWinddirection() {
      return winddirection;
   }

   public void setWinddirection(int winddirection) {
      this.winddirection = winddirection;
   }

   public int getIs_day() {
      return is_day;
   }

   public void setIs_day(int is_day) {
      this.is_day = is_day;
   }

   public int getWeathercode() {
      return weathercode;
   }

   public void setWeathercode(int weathercode) {
      this.weathercode = weathercode;
   }

   public LocalDateTime getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
   }

   @Override
   public String toString() {
      return "CurrentWeather{" +
            "time='" + time + '\'' +
            ", interval=" + interval +
            ", temperature=" + temperature +
            ", windspeed=" + windspeed +
            ", winddirection=" + winddirection +
            ", is_day=" + is_day +
            ", weathercode=" + weathercode +
            '}';
   }
}
