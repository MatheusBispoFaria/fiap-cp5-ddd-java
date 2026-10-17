package br.com.fiap.bank.plataforma.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record SolicitarCreditoRequest(

        @NotBlank(message = "O CPF e obrigatorio")
        @Pattern(
                regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$",
                message = "O CPF deve possuir 11 digitos"
        )
        String cpfCliente,

        @NotBlank(message = "O nome do cliente e obrigatorio")
        String nomeCliente,

        @NotBlank(message = "O tipo de credito e obrigatorio")
        String tipoCredito,

        @NotNull(message = "O valor solicitado e obrigatorio")
        @DecimalMin(
                value = "0.01",
                message = "O valor solicitado deve ser positivo"
        )
        BigDecimal valorSolicitado,

        @NotNull(message = "A quantidade de parcelas e obrigatoria")
        @Min(
                value = 1,
                message = "A quantidade de parcelas deve ser maior que zero"
        )
        Integer quantidadeParcelas,

        @NotNull(message = "A renda mensal e obrigatoria")
        @DecimalMin(
                value = "0.01",
                message = "A renda mensal deve ser positiva"
        )
        BigDecimal rendaMensal

) {}
