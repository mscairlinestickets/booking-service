package com.ercikWck.booking_service.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record CardDtoTransaction(

        Long bookId,

        @Size(min = 3, max = 170, message = "O nome deve conter entre {min} e {max} caracteres.")
        String name,

        @CPF
        @NotBlank(message = "Insira o cpfNumber")
        String cpfNumber,

        @Schema(description = "Nome do titular do cartão", example = "João da Silva", required = true)
        @NotBlank(message = "Insira o nome do responsavel pelo cartão.")
        String cardholderName,


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
