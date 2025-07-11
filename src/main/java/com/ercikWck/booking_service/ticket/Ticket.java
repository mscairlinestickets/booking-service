package com.ercikWck.booking_service.ticket;

import com.ercikWck.booking_service.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Ticket(

        String flightNumber,

        String origin,

        String destination,

        LocalDateTime departureDateTime,

        BigDecimal price,

        String airlineName,

        BookingStatus status,

        String icaoCode,

        String aircraftModel
        //marvel
        //ellis
        //yaml
) {

}
