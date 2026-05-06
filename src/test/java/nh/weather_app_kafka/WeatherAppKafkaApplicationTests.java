package nh.weather_app_kafka;

import nh.weather_app_kafka.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.kafka.bootstrap-servers=127.0.0.1:19092",
        "kafka.topic.name=weather-test-topic",
        "weather.api.url=https://example.test/weather"
})
class WeatherAppKafkaApplicationTests {

   @Autowired
   private ApplicationContext applicationContext;

   @Autowired
   private Environment environment;

   @MockitoBean
   private WeatherService weatherService;

   @Test
   void applicationContextLoadsWithoutTriggeringExternalCalls() {
      assertNotNull(applicationContext);
      verifyNoInteractions(weatherService);
   }

   @Test
   void applicationContextBindsConfigurationPropertiesCorrectly() {
      assertEquals("127.0.0.1:19092", environment.getProperty("spring.kafka.bootstrap-servers"));
      assertEquals("weather-test-topic", environment.getProperty("kafka.topic.name"));
      assertEquals("https://example.test/weather", environment.getProperty("weather.api.url"));
   }

}
