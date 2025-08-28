package com.ercikWck.booking_service.controller.dto;

import com.ercikWck.booking_service.domain.CardDtoTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.br.CPF;

public record BookingRequestPayload(


        @Schema(description = "Número do voo", example = "FL1234", required = true)
        @NotBlank(message = "O número do voo é obrigatório")
        String flightNumber,

        @Schema(description = "Quantidade de passagens solicitadas", example = "2", minimum = "1", required = true)
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        Integer quantity,

        CardDtoTransaction card
) {
}
