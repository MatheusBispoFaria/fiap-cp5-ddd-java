package br.com.fiap.bank.core.infrastructure.persistence;

import br.com.fiap.bank.core.infrastructure.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContaJpaRepository
        extends JpaRepository<ContaEntity, Long> {

    Optional<ContaEntity> findByNumeroConta(String numeroConta);

    List<ContaEntity> findByCliente_Id(Long clienteId);

    boolean existsByNumeroConta(String numeroConta);
}
