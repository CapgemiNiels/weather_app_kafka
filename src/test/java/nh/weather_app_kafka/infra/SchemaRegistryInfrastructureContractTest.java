package nh.weather_app_kafka.infra;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaRegistryInfrastructureContractTest {

    @Test
    void dockerComposeMustDefineSchemaRegistryServiceOnPort8081() throws IOException {
        String compose = Files.readString(Path.of("docker-compose.yml"));

        assertTrue(compose.contains("schema-registry:"),
                "docker-compose.yml must include a schema-registry service");
        assertTrue(compose.contains("confluentinc/cp-schema-registry"),
                "Schema Registry service should use the Confluent Schema Registry image");
        assertTrue(compose.contains("\"8081:8081\""),
                "Schema Registry service must expose host port 8081");
    }

    @Test
    void applicationConfigurationMustDefineSchemaRegistryAndSubjectStrategy() throws IOException {
        String appConfig = Files.readString(Path.of("src/main/resources/application.yml"));

        assertTrue(appConfig.contains("schema.registry.url"),
                "application.yml must include schema registry URL setting");
        assertTrue(appConfig.contains("TopicNameStrategy"),
                "application.yml must configure TopicNameStrategy for value subjects");
    }
}

