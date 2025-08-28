package com.ercikWck.booking_service;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Map;

@Testcontainers
public class WireMockContainerConfig {

    @Container
    static WireMockContainer wiremockContainer = new WireMockContainer("wiremock/wiremock:3.13.1")
            .withCopyToContainer(MountableFile.forClasspathResource("wiremock"), "/home/wiremock");


    public static Map<String, String> getProperties() {
        return Map.of("ticket.catalog-flight-uri", getWireMockUrl());

    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        WireMock.configureFor(wiremockContainer.getHost(), wiremockContainer.getPort());

        getProperties().entrySet().forEach(kv -> {
            registry.add(kv.getKey(), kv::getValue);
        });
    }

    private static String getWireMockUrl() {
        return "http://" + wiremockContainer.getHost() + ":" + wiremockContainer.getFirstMappedPort();
    }


}
