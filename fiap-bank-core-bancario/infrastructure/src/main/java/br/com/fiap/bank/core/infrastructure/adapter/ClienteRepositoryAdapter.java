package br.com.fiap.bank.core.infrastructure.adapter;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.infrastructure.entity.ClienteEntity;
import br.com.fiap.bank.core.infrastructure.persistence.ClienteJpaRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Cliente salvar(Cliente cliente) {
        ClienteEntity entity = toEntity(cliente);
        ClienteEntity salva = jpaRepository.save(entity);

        return toDomain(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return jpaRepository.findByCpf(cpf)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    @Transactional
    public void excluirPorId(Long id) {
        jpaRepository.deleteById(id);
    }

    private ClienteEntity toEntity(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity();

        entity.setId(cliente.getId());
        entity.setCpf(cliente.getCpf());
        entity.setNome(cliente.getNome());
        entity.setEmail(cliente.getEmail());
        entity.setDataNascimento(cliente.getDataNascimento());
        entity.setRendaMensal(cliente.getRendaMensal());

        return entity;
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(
                entity.getId(),
                entity.getCpf(),
                entity.getNome(),
                entity.getEmail(),
                entity.getDataNascimento(),
                entity.getRendaMensal()
        );
    }
}
