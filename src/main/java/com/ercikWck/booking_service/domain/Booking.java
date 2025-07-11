package com.ercikWck.booking_service.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Table(name = "booking_orders")
public record Booking(

        @Id
        Long bookingId,

        @Schema(description = "Número do voo", example = "FL1234", required = true)
        @NotBlank(message = "O número do voo é obrigatório")
        String flightNumber,

        @Schema(description = "Código do aeroporto de origem", example = "GRU", required = true)
        @NotBlank(message = "A origem é obrigatória")
        String origin,

        @Schema(description = "Código do aeroporto de destino", example = "JFK", required = true)
        @NotBlank(message = "O destino é obrigatório")
        String destination,

        @Schema(description = "Data e hora de partida", example = "2025-12-20T15:30:00", required = true)
        @NotNull(message = "A data de partida é obrigatória")
        @Future(message = "A data de partida deve ser no futuro")
        LocalDateTime departureDateTime,

        @Schema(description = "Preço por passagem", example = "1299.90", required = true)
        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        BigDecimal price,

        @Schema(description = "Quantidade de passagens solicitadas", example = "2", minimum = "1", required = true)
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        Integer quantity,

        @Schema(description = "Nome da companhia aérea", example = "Latam Airlines", required = true)
        @NotBlank(message = "O nome da companhia aérea é obrigatório")
        String airlineName,

        @Schema(description = "Status da reserva", example = "PENDING", required = true)
        @NotNull(message = "O status da reserva é obrigatório")
        BookingStatus status,

        @Schema(description = "Código ICAO da aeronave", example = "LAT", required = true)
        @NotBlank(message = "O código ICAO é obrigatório")
        String icaoCode,

        @Schema(description = "Modelo da aeronave", example = "Boeing 787", required = true)
        @NotBlank(message = "O modelo da aeronave é obrigatório")
        String aircraftModel,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @Version
        int version

) {

    public static Booking createBooking(Booking booking, int quantity, BookingStatus status) {
        return new Booking(null, booking.flightNumber(), booking.origin(), booking.destination(), booking.departureDateTime(), booking.price(), quantity, booking.airlineName(),
                status, booking.icaoCode(), booking.aircraftModel(), null, null, 0);
    }

    public static Booking createRejectedBooking(String flightNumber, int quantity) {
            return new Booking(null, flightNumber, null, null,null,null, quantity, null,
                    BookingStatus.REJECTED, null,null, null, null, 0);
    }

}
