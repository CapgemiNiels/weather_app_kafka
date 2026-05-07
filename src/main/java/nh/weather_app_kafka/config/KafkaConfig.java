package nh.weather_app_kafka.config;

import nh.weather_app_kafka.model.CurrentWeather;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.name}")
    private String topicName;

    @Bean
    public ProducerFactory<String, CurrentWeather> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serialization
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        // Local/dev reliability defaults
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // Local/dev latency + batching balance
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32_768); // 32 KB
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        // Timeouts to avoid hanging too long in dev
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60_000);

        // Helpful for debugging in local logs/metrics
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, "weather-app-producer-dev");

        // Optional Spring JSON behavior (enable only if you want no type headers):
        // configProps.put("spring.json.add.type.headers", false);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, CurrentWeather> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic weatherInputTopic() {

        Map<String, String> config = new HashMap<>();
        config.put("max.message.bytes", "2000000");

        return new NewTopic(topicName, 3, (short) 1)
                .configs(config);
    }
}
