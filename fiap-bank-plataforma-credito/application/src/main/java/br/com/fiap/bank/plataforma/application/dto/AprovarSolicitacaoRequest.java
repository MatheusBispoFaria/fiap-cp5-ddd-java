package br.com.fiap.bank.plataforma.application.dto;

public record AprovarSolicitacaoRequest(
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Positive
    Long solicitacaoId,
    String parecerOperador
) {}
