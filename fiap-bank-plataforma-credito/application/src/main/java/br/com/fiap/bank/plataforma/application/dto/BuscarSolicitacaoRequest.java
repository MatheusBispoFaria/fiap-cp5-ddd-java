package br.com.fiap.bank.plataforma.application.dto;
public record BuscarSolicitacaoRequest(
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Positive
    Long solicitacaoId
) {}
