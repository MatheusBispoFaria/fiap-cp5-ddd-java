package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.model.Conta;
import br.com.fiap.bank.core.domain.model.TipoConta;

import java.math.BigDecimal;

public record ContaResponseDTO(
        Long id,
        String numeroConta,
        String agencia,
        TipoConta tipoConta,
        BigDecimal saldo,
        Long clienteId,
        boolean ativa
) {

    public static ContaResponseDTO fromDomain(Conta conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getTipoConta(),
                conta.getSaldo(),
                conta.getClienteId(),
                conta.isAtiva()
        );
    }
}
