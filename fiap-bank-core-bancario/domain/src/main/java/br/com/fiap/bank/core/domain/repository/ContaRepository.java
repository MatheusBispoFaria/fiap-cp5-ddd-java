package br.com.fiap.bank.core.domain.repository;

import br.com.fiap.bank.core.domain.model.Conta;

import java.util.List;
import java.util.Optional;

public interface ContaRepository {

    Conta salvar(Conta conta);

    Optional<Conta> buscarPorNumeroConta(String numeroConta);

    List<Conta> listarTodas();

    List<Conta> buscarPorClienteId(Long clienteId);

    boolean existePorNumeroConta(String numeroConta);
}
