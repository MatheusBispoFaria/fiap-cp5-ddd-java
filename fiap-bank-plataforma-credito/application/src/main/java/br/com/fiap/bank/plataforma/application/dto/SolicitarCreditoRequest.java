package br.com.fiap.bank.plataforma.application.dto;

import java.math.BigDecimal;

public record SolicitarCreditoRequest(
    String cpfCliente,
    String nomeCliente,
    String tipoCredito,
    BigDecimal valorSolicitado,
    Integer quantidadeParcelas,
    BigDecimal rendaMensal
) {}
