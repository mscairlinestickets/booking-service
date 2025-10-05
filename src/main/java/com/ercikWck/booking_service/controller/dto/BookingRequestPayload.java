package com.ercikWck.booking_service.controller.dto;

import com.ercikWck.booking_service.domain.PaymentDtoTransaction;
import com.ercikWck.booking_service.domain.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookingRequestPayload(


        @Schema(description = "Número do voo", example = "FL1234", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O número do voo é obrigatório")
        String flightNumber,

        @Schema(description = "Quantidade de passagens solicitadas", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        Integer quantity,

        @Schema(description = "Tipo de pagamento",example = "boleto, pix, credito", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Insira o tipo de pagamento.")
        PaymentType paymentType,
        PaymentDtoTransaction paymentTransaction

) {
}
