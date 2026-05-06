package nh.weather_app_kafka.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {
    private String time;
    private int interval;
    private double temperature;
    private double windSpeed;
    private int windDirection;
    private int isDay;
    private int weatherCode;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @JsonIgnore
    public double getWindSpeed() {
        return windSpeed;
    }

    @JsonIgnore
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    @JsonIgnore
    public int getWindDirection() {
        return windDirection;
    }

    @JsonIgnore
    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @JsonIgnore
    public int getIsDay() {
        return isDay;
    }

    @JsonIgnore
    public void setIsDay(int isDay) {
        this.isDay = isDay;
    }

    @JsonIgnore
    public int getWeatherCode() {
        return weatherCode;
    }

    @JsonIgnore
    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    @JsonProperty("windspeed")
    public double getWindspeed() {
        return getWindSpeed();
    }

    @JsonProperty("windspeed")
    public void setWindspeed(double windspeed) {
        setWindSpeed(windspeed);
    }

    @JsonProperty("winddirection")
    public int getWinddirection() {
        return getWindDirection();
    }

    @JsonProperty("winddirection")
    public void setWinddirection(int winddirection) {
        setWindDirection(winddirection);
    }

    @JsonProperty("is_day")
    public int getIs_day() {
        return getIsDay();
    }

    @JsonProperty("is_day")
    public void setIs_day(int is_day) {
        setIsDay(is_day);
    }

    @JsonProperty("weathercode")
    public int getWeathercode() {
        return getWeatherCode();
    }

    @JsonProperty("weathercode")
    public void setWeathercode(int weathercode) {
        setWeatherCode(weathercode);
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "time='" + time + '\'' +
                ", interval=" + interval +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", isDay=" + isDay +
                ", weatherCode=" + weatherCode +
                '}';
    }
}
