package com.ercikWck.booking_service.repository;

import com.ercikWck.booking_service.domain.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BookingRepository extends ReactiveCrudRepository<Booking, Long> {
}
