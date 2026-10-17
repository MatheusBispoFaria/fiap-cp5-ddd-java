package br.com.fiap.bank.plataforma.infrastructure.adapter;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.infrastructure.entity.SolicitacaoCreditoEntity;
import br.com.fiap.bank.plataforma.infrastructure.persistence.SolicitacaoCreditoJpaRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class SolicitacaoCreditoRepositoryAdapter
        implements SolicitacaoCreditoRepository {

    private final SolicitacaoCreditoJpaRepository jpaRepository;

    public SolicitacaoCreditoRepositoryAdapter(
            SolicitacaoCreditoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public SolicitacaoCredito salvar(SolicitacaoCredito solicitacao) {

        SolicitacaoCreditoEntity entity = toEntity(solicitacao);

        SolicitacaoCreditoEntity salva = jpaRepository.save(entity);

        return toDomain(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SolicitacaoCredito> buscarPorId(Long id) {

        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitacaoCredito> listarTodas() {

        return jpaRepository.findAllByOrderByDataSolicitacaoDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitacaoCredito> buscarPorCpfCliente(String cpfCliente) {

        return jpaRepository
                .findByCpfClienteOrderByDataSolicitacaoDesc(cpfCliente)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private SolicitacaoCreditoEntity toEntity(
            SolicitacaoCredito solicitacao) {

        SolicitacaoCreditoEntity entity =
                new SolicitacaoCreditoEntity();

        entity.setId(solicitacao.getId());
        entity.setCpfCliente(solicitacao.getCpfCliente());
        entity.setNomeCliente(solicitacao.getNomeCliente());
        entity.setTipoCredito(solicitacao.getTipoCredito());
        entity.setValorSolicitado(solicitacao.getValorSolicitado());
        entity.setQuantidadeParcelas(solicitacao.getQuantidadeParcelas());
        entity.setRendaMensal(solicitacao.getRendaMensal());
        entity.setScoreCredito(solicitacao.getScoreCredito());
        entity.setStatus(solicitacao.getStatus());
        entity.setParecerOperador(solicitacao.getParecerOperador());
        entity.setScrRestricoes(solicitacao.getScrRestricoes());
        entity.setScoreBureau(solicitacao.getScoreBureau());
        entity.setDataSolicitacao(solicitacao.getDataSolicitacao());
        entity.setDataAtualizacao(solicitacao.getDataAtualizacao());

        return entity;
    }

    private SolicitacaoCredito toDomain(
            SolicitacaoCreditoEntity entity) {

        return new SolicitacaoCredito(
                entity.getId(),
                entity.getCpfCliente(),
                entity.getNomeCliente(),
                entity.getTipoCredito(),
                entity.getValorSolicitado(),
                entity.getQuantidadeParcelas(),
                entity.getRendaMensal(),
                entity.getScoreCredito(),
                entity.getStatus(),
                entity.getParecerOperador(),
                entity.getScrRestricoes(),
                entity.getScoreBureau(),
                entity.getDataSolicitacao(),
                entity.getDataAtualizacao()
        );
    }
}
