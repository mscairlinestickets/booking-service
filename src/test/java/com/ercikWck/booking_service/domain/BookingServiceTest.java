package com.ercikWck.booking_service.ticket.domain;


import com.ercikWck.booking_service.domain.Booking;
import com.ercikWck.booking_service.domain.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {


    @Test
    void shouldApprovedOrder() {
        //arrange
        String flightNumber = "TK1933";
        int quantity = 3;
        Booking booking = new Booking();
        BookingService bookingService = new BookingService();
        //act
        bookingService.buildApprovedOrder(flightNumber, quantity);

        //arrange

    }


}
