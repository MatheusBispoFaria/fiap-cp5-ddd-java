package br.com.fiap.bank.core.presentation.controller;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponseDTO;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.application.service.ContaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // Listar contas
    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> listar() {

        return ResponseEntity.ok(contaService.listarTodas());
    }

    // Buscar conta pelo numero
    @GetMapping("/{numeroConta}")
    public ResponseEntity<ContaResponseDTO> buscarPorNumero(
            @PathVariable String numeroConta) {

        return ResponseEntity.ok(
                contaService.buscarPorNumeroConta(numeroConta)
        );
    }

    // Buscar contas pelo CPF do cliente
    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<ContaResponseDTO>> buscarPorCliente(
            @PathVariable String cpf) {

        return ResponseEntity.ok(
                contaService.buscarPorCpfCliente(cpf)
        );
    }

    // Abrir nova conta
    @PostMapping
    public ResponseEntity<ContaResponseDTO> criar(
            @Valid @RequestBody CriarContaRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contaService.criar(request));
    }

    // Deposito ou saque
    @PutMapping("/{numeroConta}/saldo")
    public ResponseEntity<ContaResponseDTO> atualizarSaldo(
            @PathVariable String numeroConta,
            @Valid @RequestBody AtualizarSaldoRequest request) {

        return ResponseEntity.ok(
                contaService.atualizarSaldo(numeroConta, request)
        );
    }

    // Inativar conta
    @DeleteMapping("/{numeroConta}")
    public ResponseEntity<Void> inativar(
            @PathVariable String numeroConta) {

        contaService.inativar(numeroConta);

        return ResponseEntity.noContent().build();
    }
}
