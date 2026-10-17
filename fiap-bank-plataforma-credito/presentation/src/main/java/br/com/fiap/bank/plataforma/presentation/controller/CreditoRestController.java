
package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.*;
import br.com.fiap.bank.plataforma.application.service.PlataformaCreditoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/creditos")
@CrossOrigin(origins = "*")
public class CreditoRestController {

    private final PlataformaCreditoService service;

    public CreditoRestController(PlataformaCreditoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoCreditoResponseDTO> criar(
            @Valid @RequestBody SolicitarCreditoRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.solicitarCredito(request));
    }

    @GetMapping
    public ResponseEntity<List<SolicitacaoCreditoResponseDTO>> listar() {

        return ResponseEntity.ok(
                service.listarTodasSolicitacoes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(new BuscarSolicitacaoRequest(id))
        );
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<SolicitacaoCreditoResponseDTO>> buscarPorCpf(
            @PathVariable String cpf) {

        return ResponseEntity.ok(
                service.consultarStatusPorCpf(
                        new ConsultarStatusRequest(cpf)
                )
        );
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> aprovar(
            @PathVariable Long id,
            @RequestBody Map<String, String> dados) {

        AprovarSolicitacaoRequest request =
                new AprovarSolicitacaoRequest(
                        id,
                        dados.get("parecerOperador")
                );

        return ResponseEntity.ok(
                service.aprovarSolicitacao(request)
        );
    }

    @PatchMapping("/{id}/reprovar")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> reprovar(
            @PathVariable Long id,
            @RequestBody Map<String, String> dados) {

        ReprovarSolicitacaoRequest request =
                new ReprovarSolicitacaoRequest(
                        id,
                        dados.get("motivoReprovacao")
                );

        return ResponseEntity.ok(
                service.reprovarSolicitacao(request)
        );
    }

}