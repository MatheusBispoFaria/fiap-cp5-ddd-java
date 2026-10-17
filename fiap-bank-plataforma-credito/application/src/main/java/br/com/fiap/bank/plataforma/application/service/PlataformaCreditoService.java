package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.application.dto.*;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.model.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.application.exception.SolicitacaoNaoEncontradaException;

import org.springframework.stereotype.Service;
import br.com.fiap.bank.plataforma.infrastructure.client.NotificacaoClient;
import br.com.fiap.bank.plataforma.infrastructure.client.BureauCreditoClient;
import br.com.fiap.bank.plataforma.infrastructure.client.ScrClient;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlataformaCreditoService {

    private final SolicitacaoCreditoRepository repository;
    private final ScrClient scrService;
    private final br.com.fiap.bank.plataforma.infrastructure.client.CrmClient crmClient;
    private final BureauCreditoClient bureauService;
    private final NotificacaoClient notificacaoService;
    private final CoreBancarioClientService coreBancarioClientService;

    public PlataformaCreditoService(
            SolicitacaoCreditoRepository repository,
            ScrClient scrService,
            br.com.fiap.bank.plataforma.infrastructure.client.CrmClient crmClient,
            BureauCreditoClient bureauService,
            NotificacaoClient notificacaoService,
            CoreBancarioClientService coreBancarioClientService) {

        this.repository = repository;
        this.scrService = scrService;
        this.crmClient = crmClient;
        this.bureauService = bureauService;
        this.notificacaoService = notificacaoService;
        this.coreBancarioClientService = coreBancarioClientService;
    }

    @Transactional
    public SolicitacaoCreditoResponseDTO solicitarCredito(
            SolicitarCreditoRequest request) {

        if (request.quantidadeParcelas() == null
                || request.quantidadeParcelas() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de parcelas deve ser maior que zero.");
        }

        String cpfLimpo = request.cpfCliente().replaceAll("\\D", "");

        // 1. Consulta SCR
        boolean temRestricaoScr =
                scrService.consultarRestricoesScr(cpfLimpo);

        // 2. Consulta Bureau de Credito
        int scoreBureau =
                bureauService.consultarScoreBureau(cpfLimpo);

        // 3. Consulta Core Bancario
        coreBancarioClientService.consultarContaCliente(cpfLimpo);

        var decisao = br.com.fiap.bank.plataforma.domain.model.MotorCredito.analisar(
                temRestricaoScr, scoreBureau, request.valorSolicitado(), request.quantidadeParcelas(), request.rendaMensal());
        StatusSolicitacao status = decisao.status();
        String parecer = decisao.parecer() + " CRM: " + crmClient.consultarHistorico(cpfLimpo) + ".";

        LocalDateTime agora = LocalDateTime.now();

        SolicitacaoCredito solicitacao = new SolicitacaoCredito(
                null,
                cpfLimpo,
                request.nomeCliente(),
                request.tipoCredito(),
                request.valorSolicitado(),
                request.quantidadeParcelas(),
                request.rendaMensal(),
                scoreBureau,
                status,
                parecer,
                temRestricaoScr,
                scoreBureau,
                agora,
                agora
        );

        SolicitacaoCredito salva = repository.salvar(solicitacao);

        // 5. Notificacao externa
        notificacaoService.enviarNotificacao(
                cpfLimpo,
                "Sua solicitacao de credito #" + salva.getId()
                        + " esta no status: " + status
        );

        return SolicitacaoCreditoResponseDTO.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoCreditoResponseDTO> consultarStatusPorCpf(
            ConsultarStatusRequest request) {

        String cpfLimpo = request.cpfCliente().replaceAll("\\D", "");

        return repository.buscarPorCpfCliente(cpfLimpo)
                .stream()
                .map(SolicitacaoCreditoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolicitacaoCreditoResponseDTO buscarPorId(
            BuscarSolicitacaoRequest request) {

        SolicitacaoCredito solicitacao =
                repository.buscarPorId(request.solicitacaoId())
                        .orElseThrow(() -> new SolicitacaoNaoEncontradaException(
                                request.solicitacaoId()
                        ));

        return SolicitacaoCreditoResponseDTO.fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoCreditoResponseDTO> listarTodasSolicitacoes() {

        return repository.listarTodas()
                .stream()
                .map(SolicitacaoCreditoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public SolicitacaoCreditoResponseDTO aprovarSolicitacao(
            AprovarSolicitacaoRequest request) {

        SolicitacaoCredito solicitacao =
                repository.buscarPorId(request.solicitacaoId())
                        .orElseThrow(() -> new SolicitacaoNaoEncontradaException(request.solicitacaoId()));

        if (solicitacao.getStatus() != StatusSolicitacao.EM_ANALISE) {
            throw new IllegalArgumentException("Esta solicitacao ja foi finalizada.");
        }

        solicitacao.setStatus(StatusSolicitacao.APROVADO);

        solicitacao.setParecerOperador(
                "Aprovado pela Mesa de Operacoes: "
                        + (request.parecerOperador() != null
                        ? request.parecerOperador()
                        : "OK")
        );

        solicitacao.setDataAtualizacao(LocalDateTime.now());

        SolicitacaoCredito salva = repository.salvar(solicitacao);

        notificacaoService.enviarNotificacao(
                salva.getCpfCliente(),
                "PARABENS! Sua solicitacao de credito #"
                        + salva.getId()
                        + " foi APROVADA pela Mesa de Operacoes."
        );

        return SolicitacaoCreditoResponseDTO.fromEntity(salva);
    }

    @Transactional
    public SolicitacaoCreditoResponseDTO reprovarSolicitacao(
            ReprovarSolicitacaoRequest request) {

        SolicitacaoCredito solicitacao =
                repository.buscarPorId(request.solicitacaoId())
                        .orElseThrow(() -> new SolicitacaoNaoEncontradaException(request.solicitacaoId()));

        if (solicitacao.getStatus() != StatusSolicitacao.EM_ANALISE) {
            throw new IllegalArgumentException("Esta solicitacao ja foi finalizada.");
        }

        solicitacao.setStatus(StatusSolicitacao.REPROVADO);

        solicitacao.setParecerOperador(
                "Reprovado pela Mesa de Operacoes. Motivo: "
                        + (request.motivoReprovacao() != null
                        ? request.motivoReprovacao()
                        : "Inconformidade nos criterios de risco")
        );

        solicitacao.setDataAtualizacao(LocalDateTime.now());

        SolicitacaoCredito salva = repository.salvar(solicitacao);

        notificacaoService.enviarNotificacao(
                salva.getCpfCliente(),
                "Sua solicitacao de credito #"
                        + salva.getId()
                        + " foi REPROVADA pela Mesa de Operacoes."
        );

        return SolicitacaoCreditoResponseDTO.fromEntity(salva);
    }
}
