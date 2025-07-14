package com.ercikWck.booking_service.controller;

import com.ercikWck.booking_service.controller.dto.BookingRequestPayload;
import com.ercikWck.booking_service.domain.Booking;
import com.ercikWck.booking_service.domain.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public Flux<Booking> findAll() {
        return null;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Booking>> findById(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Booking> createBooking(@RequestBody @Valid BookingRequestPayload request) {
        return bookingService.submitOrder(request.flightNumber(), request.quantity(), request.card());
    }

}

