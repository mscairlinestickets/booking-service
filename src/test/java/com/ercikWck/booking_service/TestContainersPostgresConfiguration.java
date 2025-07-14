package com.ercikWck.booking_service;

import com.ercikWck.booking_service.config.ConfigAuditData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Import(ConfigAuditData.class)
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {


	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	}

}
