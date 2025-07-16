package com.ercikWck.booking_service.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record CardDtoTransaction(

        @Schema(description = "ID do pagamento", example = "98765", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Id
        Long paymentId,

        @Schema(description = "Nome do titular do cartão", example = "João da Silva", required = true)
        @NotBlank(message = "Insira o nome do responsavel pelo cartão.")
        String cardholderName,

        @Schema(description = "Valor total da compra", example = "2599.90", required = true)
        @NotNull(message = "Insira o valor total do produto.")
        @Positive(message = "O preço deve ser um número positovo.")
        BigDecimal amount,

        @Schema(description = "Tipo de cartão (credito ou debito)", example = "credito", required = true)
        @NotBlank(message = "Insira o tipo do cartão credito ou debito")
        String type,

        @Schema(description = "Número do cartão", example = "1234567890123456", required = true)
        @NotBlank(message = "Insira o numero do cartão")
        @Pattern(regexp = "^([0-9]{16})$", message = "O número do cartão está invalido.")
        String cardNumber,

        @Schema(description = "Data de expiração (MMYYYY)", example = "122025", required = true)
        @NotBlank(message = "Insira a data de expiração")
        @Pattern(regexp = "^([0-9]{6})$", message = "A data de expiração deve conter só 6 numeros.")
        String expiryDate,

        @Schema(description = "Código de segurança (CVV)", example = "123", required = true)
        @NotBlank(message = "Insira o código de segurançã do cartão")
        @Pattern(regexp = "^([0-9]{3})$", message = "O número do cartão está invalido.")
        String cvv
) {

}
