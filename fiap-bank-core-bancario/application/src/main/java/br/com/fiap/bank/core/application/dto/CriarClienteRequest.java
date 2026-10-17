package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CriarClienteRequest(

        @NotBlank(message = "O CPF e obrigatorio")
        @Pattern(
                regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$",
                message = "O CPF deve conter 11 digitos, com ou sem mascara"
        )
        String cpf,

        @NotBlank(message = "O nome e obrigatorio")
        @Size(
                min = 3,
                max = 150,
                message = "O nome deve conter entre 3 e 150 caracteres"
        )
        String nome,

        @NotBlank(message = "O email e obrigatorio")
        @Email(message = "O email informado e invalido")
        String email,

        @NotNull(message = "A data de nascimento e obrigatoria")
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        @NotNull(message = "A renda mensal e obrigatoria")
        @DecimalMin(
                value = "0.00",
                message = "A renda mensal nao pode ser negativa"
        )
        BigDecimal rendaMensal

) {}