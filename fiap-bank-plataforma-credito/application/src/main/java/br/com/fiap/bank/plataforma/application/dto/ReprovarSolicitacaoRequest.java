package br.com.fiap.bank.plataforma.application.dto;

public record ReprovarSolicitacaoRequest(
    Long solicitacaoId,
    String motivoReprovacao
) {}
