package br.com.fiap.bank.plataforma.application.dto;

public record AprovarSolicitacaoRequest(
    Long solicitacaoId,
    String parecerOperador
) {}
