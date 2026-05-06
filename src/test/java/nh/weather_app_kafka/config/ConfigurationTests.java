package nh.weather_app_kafka.config;

import nh.weather_app_kafka.model.CurrentWeather;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigurationTests {

    @Test
    void appConfigExposesRequiredInfrastructureBeans() {
        AppConfig appConfig = new AppConfig();

        assertNotNull(appConfig.restTemplate());
        assertNotNull(appConfig.objectMapper());
    }

    @Test
    void kafkaConfigWeatherInputTopicUsesConfiguredTopicName() {
        KafkaConfig kafkaConfig = new KafkaConfig();
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "127.0.0.1:9092");
        ReflectionTestUtils.setField(kafkaConfig, "topicName", "weather-test-topic");

        NewTopic topic = kafkaConfig.weatherInputTopic();
        ProducerFactory<String, CurrentWeather> producerFactory = kafkaConfig.producerFactory();
        DefaultKafkaProducerFactory<String, CurrentWeather> defaultKafkaProducerFactory =
                (DefaultKafkaProducerFactory<String, CurrentWeather>) producerFactory;

        assertEquals("weather-test-topic", topic.name());
        assertEquals(3, topic.numPartitions());
        assertEquals((short) 1, topic.replicationFactor());
        assertEquals("2000000", topic.configs().get("max.message.bytes"));
        assertEquals("127.0.0.1:9092",
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }
}

