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

        @Schema(description = "Número do voo", example = "FL1234", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O número do voo é obrigatório")
        String flightNumber,

        @Schema(description = "Código do aeroporto de origem", example = "GRU", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A origem é obrigatória")
        String origin,

        @Schema(description = "Código do aeroporto de destino", example = "JFK", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O destino é obrigatório")
        String destination,

        @Schema(description = "Data e hora de partida", example = "2025-12-20T15:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A data de partida é obrigatória")
        @Future(message = "A data de partida deve ser no futuro")
        LocalDateTime departureDateTime,

        @Schema(description = "Preço por passagem", example = "1299.90", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        BigDecimal price,

        @Schema(description = "Quantidade de passagens solicitadas", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        Integer quantity,

        @Schema(description = "Nome da companhia aérea", example = "Latam Airlines", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O nome da companhia aérea é obrigatório")
        String airlineName,


        @Schema(description = "Status da reserva", example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O status da reserva é obrigatório")
        BookingStatus status,

        @Schema(description = "Tipo do pagamento", example = "CREDITO", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Insira o tipo de pagamento.")

        PaymentType paymentType,

        @Schema(description = "Código ICAO da aeronave", example = "LAT", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O código ICAO é obrigatório")
        String icaoCode,

        @Schema(description = "Modelo da aeronave", example = "Boeing 787", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O modelo da aeronave é obrigatório")
        String aircraftModel,

        @Schema(description = "Data de criação da reserva", example = "2025-12-01T15:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        @CreatedDate
        Instant createdDate,

        @Schema(description = "Última modificação da reserva", example = "2025-12-01T16:45:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        @LastModifiedDate
        Instant lastModifiedDate,

        @Schema(description = "Versão do registro (controle de concorrência)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        @Version
        int version

) {

    public static Booking createBooking(Booking booking,  int quantity, PaymentType paymentType, BookingStatus status) {
        return new Booking(null, booking.flightNumber(), booking.origin(), booking.destination(), booking.departureDateTime(), getMultiply(booking, quantity), quantity, booking.airlineName(),
                status, paymentType, booking.icaoCode(), booking.aircraftModel(), null, null, 0);
    }

    private static BigDecimal getMultiply(Booking booking, int quantity) {
        return booking.price().multiply(BigDecimal.valueOf(quantity));
    }

    public static Booking createRejectedBooking(String flightNumber, PaymentType paymentType, int quantity) {
        return new Booking(null, flightNumber, null, null, null, null, quantity, null,
                BookingStatus.REJECTED, paymentType, null, null, null, null, 0);
    }


}
