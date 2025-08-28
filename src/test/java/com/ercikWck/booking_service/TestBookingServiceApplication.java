package com.ercikWck.booking_service;

import org.springframework.boot.SpringApplication;

import static com.ercikWck.booking_service.WireMockContainerConfig.getProperties;
import static com.ercikWck.booking_service.WireMockContainerConfig.wiremockContainer;

public class TestBookingServiceApplication {

    public static void main(String[] args) {
        wiremockContainer.start();
        getProperties().forEach(System::setProperty);
        SpringApplication.from(BookingServiceApplication::main)
                .with(TestContainersPostgresConfiguration.class)
                .with(WireMockContainerConfig.class)
                .run(args);
    }

}
