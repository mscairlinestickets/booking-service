package com.ercikWck.booking_service;

import org.springframework.boot.SpringApplication;

public class TestBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(BookingServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
