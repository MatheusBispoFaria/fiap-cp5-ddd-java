
package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.*;
import br.com.fiap.bank.plataforma.application.service.CoreBancarioClientService;
import br.com.fiap.bank.plataforma.application.service.PlataformaCreditoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credito")
@CrossOrigin(origins = "*")
public class CreditoOperacoesNonRestfulController {

    private final PlataformaCreditoService plataformaService;
    private final CoreBancarioClientService coreBancarioClientService;

    public CreditoOperacoesNonRestfulController(
            PlataformaCreditoService plataformaService,
            CoreBancarioClientService coreBancarioClientService) {

        this.plataformaService = plataformaService;
        this.coreBancarioClientService = coreBancarioClientService;
    }

    // Criar solicitacao de credito
    @PostMapping("/solicitarCredito")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> solicitarCredito(
            @RequestBody SolicitarCreditoRequest request) {

        return ResponseEntity.ok(
                plataformaService.solicitarCredito(request)
        );
    }

    // Consultar status por CPF
    @PostMapping("/consultarStatusCredito")
    public ResponseEntity<List<SolicitacaoCreditoResponseDTO>> consultarStatusCredito(
            @RequestBody ConsultarStatusRequest request) {

        return ResponseEntity.ok(
                plataformaService.consultarStatusPorCpf(request)
        );
    }

    // Buscar solicitacao por ID
    @PostMapping("/buscarSolicitacaoPorId")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> buscarSolicitacaoPorId(
            @RequestBody BuscarSolicitacaoRequest request) {

        return ResponseEntity.ok(
                plataformaService.buscarPorId(request)
        );
    }

    // Listar todas as solicitacoes
    @PostMapping("/listarTodasSolicitacoes")
    public ResponseEntity<List<SolicitacaoCreditoResponseDTO>> listarTodasSolicitacoes() {

        return ResponseEntity.ok(
                plataformaService.listarTodasSolicitacoes()
        );
    }

    // Aprovar solicitacao
    @PostMapping("/aprovarSolicitacao")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> aprovarSolicitacao(
            @RequestBody AprovarSolicitacaoRequest request) {

        return ResponseEntity.ok(
                plataformaService.aprovarSolicitacao(request)
        );
    }

    // Reprovar solicitacao
    @PostMapping("/reprovarSolicitacao")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> reprovarSolicitacao(
            @RequestBody ReprovarSolicitacaoRequest request) {

        return ResponseEntity.ok(
                plataformaService.reprovarSolicitacao(request)
        );
    }

    // Consultar dados do cliente no Core Bancario
    @PostMapping("/consultarClienteCore")
    public ResponseEntity<Map<String, Object>> consultarClienteCore(
            @RequestBody ConsultarStatusRequest request) {

        return ResponseEntity.ok(
                coreBancarioClientService.consultarContaCliente(
                        request.cpfCliente()
                )
        );
    }
}
