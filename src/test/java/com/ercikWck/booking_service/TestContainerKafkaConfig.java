package com.ercikWck.booking_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Testcontainers
@TestConfiguration
public class TestContainerKafkaConfig {

    @Container
    public static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:3.7.0"))
            .withExposedPorts(9092, 9093)
            .waitingFor(Wait.forListeningPort()
                    .withStartupTimeout(Duration.ofMinutes(1)));

    static {
        kafka.start();
        configureSpringKafkaProperties();
    }

    private static void configureSpringKafkaProperties() {
        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
        System.setProperty("spring.kafka.producer.acks", "all");
        System.setProperty("spring.kafka.producer.retries", "0");
        System.setProperty("spring.kafka.producer.batch-size", "16384");
        System.setProperty("spring.kafka.producer.linger-ms", "1");
    }


}