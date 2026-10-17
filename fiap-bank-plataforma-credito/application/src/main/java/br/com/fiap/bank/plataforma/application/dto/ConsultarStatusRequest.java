package br.com.fiap.bank.plataforma.application.dto;

public record ConsultarStatusRequest(
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Pattern(regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$")
    String cpfCliente
) {}
