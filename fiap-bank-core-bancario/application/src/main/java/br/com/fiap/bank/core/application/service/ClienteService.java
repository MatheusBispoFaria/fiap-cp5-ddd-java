package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.ClienteResponseDTO;
import br.com.fiap.bank.core.application.dto.CriarClienteRequest;
import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.domain.repository.ContaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            ContaRepository contaRepository) {

        this.clienteRepository = clienteRepository;
        this.contaRepository = contaRepository;
    }

    // Cadastrar cliente
    @Transactional
    public ClienteResponseDTO criar(CriarClienteRequest request) {

        String cpf = normalizarCpf(request.cpf());

        if (clienteRepository.existePorCpf(cpf)) {
            throw new IllegalStateException(
                    "Ja existe um cliente cadastrado com este CPF.");
        }

        Cliente cliente = new Cliente(
                null,
                cpf,
                request.nome(),
                request.email(),
                request.dataNascimento(),
                request.rendaMensal()
        );

        Cliente salvo = clienteRepository.salvar(cliente);

        return ClienteResponseDTO.fromDomain(salvo);
    }

    // Listar todos os clientes
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {

        return clienteRepository.listarTodos()
                .stream()
                .map(ClienteResponseDTO::fromDomain)
                .toList();
    }

    // Buscar cliente por ID
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {

        Cliente cliente = obterCliente(id);

        return ClienteResponseDTO.fromDomain(cliente);
    }

    // Buscar cliente por CPF
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorCpf(String cpf) {

        String cpfLimpo = normalizarCpf(cpf);

        Cliente cliente = clienteRepository.buscarPorCpf(cpfLimpo)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cliente nao encontrado com CPF: " + cpfLimpo
                ));

        return ClienteResponseDTO.fromDomain(cliente);
    }

    // Atualizar dados cadastrais
    @Transactional
    public ClienteResponseDTO atualizar(
            Long id,
            CriarClienteRequest request) {

        Cliente cliente = obterCliente(id);
        String novoCpf = normalizarCpf(request.cpf());

        if (!cliente.getCpf().equals(novoCpf)
                && clienteRepository.existePorCpf(novoCpf)) {

            throw new IllegalStateException(
                    "O CPF informado ja pertence a outro cliente.");
        }

        cliente.setCpf(novoCpf);
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setDataNascimento(request.dataNascimento());
        cliente.setRendaMensal(request.rendaMensal());

        Cliente atualizado = clienteRepository.salvar(cliente);

        return ClienteResponseDTO.fromDomain(atualizado);
    }

    // Excluir cliente
    @Transactional
    public void excluir(Long id) {

        obterCliente(id);

        if (!contaRepository.buscarPorClienteId(id).isEmpty()) {
            throw new IllegalStateException(
                    "Nao e possivel excluir um cliente com contas vinculadas.");
        }

        clienteRepository.excluirPorId(id);
    }

    // Metodo auxiliar de busca
    private Cliente obterCliente(Long id) {

        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cliente nao encontrado com ID: " + id
                ));
    }

    // Remove a mascara do CPF
    private String normalizarCpf(String cpf) {

        if (cpf == null) {
            throw new IllegalArgumentException(
                    "O CPF e obrigatorio.");
        }

        String cpfLimpo = cpf.replaceAll("\\D", "");

        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException(
                    "O CPF deve possuir 11 digitos.");
        }

        return cpfLimpo;
    }
}
