package com.ercikWck.booking_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainersPostgresConfiguration.class)
@SpringBootTest
class BookingServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
