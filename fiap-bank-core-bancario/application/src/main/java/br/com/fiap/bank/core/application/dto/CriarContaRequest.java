package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.model.TipoConta;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CriarContaRequest(

        @NotBlank(message = "O CPF do cliente e obrigatorio")
        String cpfCliente,

        @Pattern(
                regexp = "\\d{4}",
                message = "A agencia deve possuir exatamente 4 digitos"
        )
        String agencia,

        @NotNull(message = "O tipo de conta e obrigatorio")
        TipoConta tipoConta,

        @NotNull(message = "O saldo inicial e obrigatorio")
        @DecimalMin(
                value = "0.00",
                message = "O saldo inicial nao pode ser negativo"
        )
        BigDecimal saldoInicial

) {}