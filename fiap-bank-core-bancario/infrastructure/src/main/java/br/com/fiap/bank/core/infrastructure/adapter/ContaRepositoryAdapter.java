package br.com.fiap.bank.core.infrastructure.adapter;

import br.com.fiap.bank.core.domain.model.Conta;
import br.com.fiap.bank.core.domain.repository.ContaRepository;
import br.com.fiap.bank.core.infrastructure.entity.ContaEntity;
import br.com.fiap.bank.core.infrastructure.entity.ClienteEntity;
import br.com.fiap.bank.core.infrastructure.persistence.ContaJpaRepository;
import br.com.fiap.bank.core.infrastructure.persistence.ClienteJpaRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ContaRepositoryAdapter implements ContaRepository {

    private final ContaJpaRepository contaJpaRepository;
    private final ClienteJpaRepository clienteJpaRepository;

    public ContaRepositoryAdapter(
            ContaJpaRepository contaJpaRepository,
            ClienteJpaRepository clienteJpaRepository) {

        this.contaJpaRepository = contaJpaRepository;
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    @Transactional
    public Conta salvar(Conta conta) {

        ContaEntity entity = toEntity(conta);
        ContaEntity salva = contaJpaRepository.save(entity);

        return toDomain(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conta> buscarPorNumeroConta(String numeroConta) {

        return contaJpaRepository.findByNumeroConta(numeroConta)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conta> listarTodas() {

        return contaJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conta> buscarPorClienteId(Long clienteId) {

        return contaJpaRepository.findByCliente_Id(clienteId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNumeroConta(String numeroConta) {

        return contaJpaRepository.existsByNumeroConta(numeroConta);
    }

    private ContaEntity toEntity(Conta conta) {

        ContaEntity entity = new ContaEntity();

        entity.setId(conta.getId());
        entity.setNumeroConta(conta.getNumeroConta());
        entity.setAgencia(conta.getAgencia());
        entity.setTipoConta(conta.getTipoConta());
        entity.setSaldo(conta.getSaldo());
        entity.setAtiva(conta.isAtiva());

        if (conta.getClienteId() == null) {
            throw new IllegalArgumentException(
                    "A conta deve estar vinculada a um cliente.");
        }

        ClienteEntity cliente = clienteJpaRepository
                .findById(conta.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente nao encontrado com ID: "
                                + conta.getClienteId()
                ));

        entity.setCliente(cliente);

        return entity;
    }

    private Conta toDomain(ContaEntity entity) {

        return new Conta(
                entity.getId(),
                entity.getNumeroConta(),
                entity.getAgencia(),
                entity.getTipoConta(),
                entity.getSaldo(),
                entity.getCliente().getId(),
                entity.isAtiva()
        );
    }
}
