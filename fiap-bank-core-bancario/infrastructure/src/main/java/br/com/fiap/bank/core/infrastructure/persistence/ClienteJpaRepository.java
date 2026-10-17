package br.com.fiap.bank.core.infrastructure.persistence;

import br.com.fiap.bank.core.infrastructure.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteJpaRepository
        extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
