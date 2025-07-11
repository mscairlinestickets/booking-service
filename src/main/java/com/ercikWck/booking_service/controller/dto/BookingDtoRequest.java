package com.ercikWck.booking_service.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record BookingDtoRequest(

        @Schema(description = "Código do voo", example = "FL1234", required = true)
        @NotBlank(message = "O código do voo é obrigatório")
        String flightNumber,

        @Schema(description = "Quantidade de passagens solicitadas", example = "2", minimum = "1", required = true)
        @NotNull(message = "A quantidade de passagens é obrigatória")
        @Min(value = 1, message = "A quantidade mínima é 1")
        Integer quantity

) {}
