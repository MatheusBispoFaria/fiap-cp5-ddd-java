package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.model.Cliente;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClienteResponseDTO(
        Long id,
        String cpf,
        String nome,
        String email,
        LocalDate dataNascimento,
        BigDecimal rendaMensal
) {

    public static ClienteResponseDTO fromDomain(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getDataNascimento(),
                cliente.getRendaMensal()
        );
    }
}
