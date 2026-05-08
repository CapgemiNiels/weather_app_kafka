package nh.weather_app_kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KafkaAvroConfigurationContractTest {

    @Test
    void producerFactoryUsesKafkaAvroSerializerAndSchemaRegistrySettings() {
        KafkaConfig kafkaConfig = new KafkaConfig();
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "127.0.0.1:9092");
        ReflectionTestUtils.setField(kafkaConfig, "topicName", "weather-test-topic");

        assertTrue(hasField(KafkaConfig.class, "schemaRegistryUrl"),
                "KafkaConfig must declare a schemaRegistryUrl property sourced from configuration");
        if (hasField(KafkaConfig.class, "schemaRegistryUrl")) {
            ReflectionTestUtils.setField(kafkaConfig, "schemaRegistryUrl", "http://127.0.0.1:8081");
        }

        ProducerFactory<String, ?> producerFactory = kafkaConfig.producerFactory();
        DefaultKafkaProducerFactory<String, ?> defaultFactory = (DefaultKafkaProducerFactory<String, ?>) producerFactory;
        Map<String, Object> config = defaultFactory.getConfigurationProperties();

        Object serializer = config.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG);
        String serializerClassName = serializer instanceof Class<?> clazz ? clazz.getName() : String.valueOf(serializer);

        assertEquals("io.confluent.kafka.serializers.KafkaAvroSerializer", serializerClassName,
                "Producer value serializer must switch from JSON to KafkaAvroSerializer");
        assertEquals("http://127.0.0.1:8081", config.get("schema.registry.url"),
                "Producer config must include schema registry URL");
        assertEquals("io.confluent.kafka.serializers.subject.TopicNameStrategy", config.get("value.subject.name.strategy"),
                "Producer config must enforce TopicNameStrategy for value subjects");
    }

    private boolean hasField(Class<?> type, String fieldName) {
        return Arrays.stream(type.getDeclaredFields())
                .map(Field::getName)
                .anyMatch(fieldName::equals);
    }
}

