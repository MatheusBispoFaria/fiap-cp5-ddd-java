package br.com.fiap.bank.core.presentation.controller;

import br.com.fiap.bank.core.application.dto.ClienteResponseDTO;
import br.com.fiap.bank.core.application.dto.CriarClienteRequest;
import br.com.fiap.bank.core.application.service.ClienteService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Listar clientes
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                clienteService.buscarPorId(id)
        );
    }

    // Buscar por CPF
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(
            @PathVariable String cpf) {

        return ResponseEntity.ok(
                clienteService.buscarPorCpf(cpf)
        );
    }

    // Criar cliente
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(
            @Valid @RequestBody CriarClienteRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.criar(request));
    }

    // Atualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CriarClienteRequest request) {

        return ResponseEntity.ok(
                clienteService.atualizar(id, request)
        );
    }

    // Excluir cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        clienteService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
