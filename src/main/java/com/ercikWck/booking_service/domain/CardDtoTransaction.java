package com.ercikWck.booking_service.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record CardDtoTransaction(

        @Id
        Long paymentId,

        @NotBlank(message = "Insira o nome do responsavel pelo cartão.")
        String cardholderName,

        @NotNull(message = "Insira o valor total do produto.")
        @Positive(message = "O preço deve ser um número positovo.")
        BigDecimal amount,

        @NotBlank(message = "Insira o tipo do cartão credito ou debito")
        String type,

        @NotBlank(message = "Insira o numero do cartão")
        @Pattern(regexp = "^([0-9]{16})$", message = "O número do cartão está invalido.")
        String cardNumber,

        @NotBlank(message = "Insira a data de expiração")
        @Pattern(regexp = "^([0-9]{6})$", message = "A data de expiração deve conter só 6 numeros.")
        String expiryDate,

        @NotBlank(message = "Insira o código de segurançã do cartão")
        @Pattern(regexp = "^([0-9]{3})$", message = "O número do cartão está invalido.")
        String cvv

) {


}