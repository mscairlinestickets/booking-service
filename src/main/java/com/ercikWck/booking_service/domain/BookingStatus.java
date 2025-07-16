package com.ercikWck.booking_service.domain;

import io.swagger.v3.oas.annotations.media.Schema;

public enum BookingStatus {

    @Schema(description = "Reserva pendente")
    PENDING,

    @Schema(description = "Reserva confirmada")
    APPROVED,

    @Schema(description = "Reserva rejeitada")
    REJECTED
}
