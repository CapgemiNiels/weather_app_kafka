package nh.weather_app_kafka.config;

import nh.weather_app_kafka.model.CurrentWeather;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
        KafkaTemplate<String, CurrentWeather> kafkaTemplate = kafkaConfig.kafkaTemplate();
        DefaultKafkaProducerFactory<String, CurrentWeather> defaultKafkaProducerFactory =
                (DefaultKafkaProducerFactory<String, CurrentWeather>) producerFactory;

        assertEquals("weather-test-topic", topic.name());
        assertEquals(3, topic.numPartitions());
        assertEquals((short) 1, topic.replicationFactor());
        assertEquals("2000000", topic.configs().get("max.message.bytes"));
        assertEquals("127.0.0.1:9092",
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("all",
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.ACKS_CONFIG));
        assertEquals(true,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        assertEquals(Integer.MAX_VALUE,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.RETRIES_CONFIG));
        assertEquals(5,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION));
        assertEquals(10,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.LINGER_MS_CONFIG));
        assertEquals(32_768,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.BATCH_SIZE_CONFIG));
        assertEquals("snappy",
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.COMPRESSION_TYPE_CONFIG));
        assertEquals(15_000,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG));
        assertEquals(60_000,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG));
        assertEquals("weather-app-producer-dev",
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.CLIENT_ID_CONFIG));
        assertEquals(StringSerializer.class,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JacksonJsonSerializer.class,
                defaultKafkaProducerFactory.getConfigurationProperties().get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertNotNull(kafkaTemplate);
        assertNotNull(kafkaTemplate.getProducerFactory());
    }
}

