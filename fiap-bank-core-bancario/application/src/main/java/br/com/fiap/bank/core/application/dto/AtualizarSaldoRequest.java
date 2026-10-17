package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AtualizarSaldoRequest(

        @NotBlank(message = "A operacao e obrigatoria")
        @Pattern(
                regexp = "(?i)^(DEPOSITO|SAQUE)$",
                message = "A operacao deve ser DEPOSITO ou SAQUE"
        )
        String operacao,

        @NotNull(message = "O valor e obrigatorio")
        @DecimalMin(
                value = "0.01",
                message = "O valor da movimentacao deve ser maior que zero"
        )
        BigDecimal valor

) {}