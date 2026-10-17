package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponseDTO;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.Conta;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.domain.repository.ContaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    public ContaService(
            ContaRepository contaRepository,
            ClienteRepository clienteRepository) {

        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
    }

    // Abrir uma nova conta bancaria
    @Transactional
    public ContaResponseDTO criar(CriarContaRequest request) {

        String cpf = normalizarCpf(request.cpfCliente());

        Cliente cliente = clienteRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cliente nao encontrado com CPF: " + cpf
                ));

        String agencia = request.agencia();

        if (agencia == null) {
            agencia = "0001";
        }

        if (!agencia.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "A agencia deve possuir exatamente 4 digitos.");
        }

        if (request.tipoConta() == null) {
            throw new IllegalArgumentException(
                    "O tipo de conta e obrigatorio.");
        }

        BigDecimal saldoInicial = request.saldoInicial();

        if (saldoInicial == null
                || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O saldo inicial nao pode ser negativo.");
        }

        Conta conta = new Conta(
                null,
                gerarNumeroConta(),
                agencia,
                request.tipoConta(),
                saldoInicial,
                cliente.getId(),
                true
        );

        Conta salva = contaRepository.salvar(conta);

        return ContaResponseDTO.fromDomain(salva);
    }

    // Listar todas as contas
    @Transactional(readOnly = true)
    public List<ContaResponseDTO> listarTodas() {

        return contaRepository.listarTodas()
                .stream()
                .map(ContaResponseDTO::fromDomain)
                .toList();
    }

    // Buscar conta pelo numero
    @Transactional(readOnly = true)
    public ContaResponseDTO buscarPorNumeroConta(String numeroConta) {

        Conta conta = obterConta(numeroConta);

        return ContaResponseDTO.fromDomain(conta);
    }

    // Consultar contas de um cliente pelo CPF
    @Transactional(readOnly = true)
    public List<ContaResponseDTO> buscarPorCpfCliente(String cpf) {

        String cpfLimpo = normalizarCpf(cpf);

        Cliente cliente = clienteRepository.buscarPorCpf(cpfLimpo)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cliente nao encontrado com CPF: " + cpfLimpo
                ));

        return contaRepository.buscarPorClienteId(cliente.getId())
                .stream()
                .map(ContaResponseDTO::fromDomain)
                .toList();
    }

    // Realizar deposito ou saque
    @Transactional
    public ContaResponseDTO atualizarSaldo(
            String numeroConta,
            AtualizarSaldoRequest request) {

        Conta conta = obterConta(numeroConta);

        if (request.operacao() == null) {
            throw new IllegalArgumentException(
                    "A operacao e obrigatoria.");
        }

        String operacao = request.operacao()
                .toUpperCase(Locale.ROOT);

        switch (operacao) {

            case "DEPOSITO" ->
                    conta.depositar(request.valor());

            case "SAQUE" ->
                    conta.sacar(request.valor());

            default -> throw new IllegalArgumentException(
                    "Operacao invalida. Utilize DEPOSITO ou SAQUE.");
        }

        Conta atualizada = contaRepository.salvar(conta);

        return ContaResponseDTO.fromDomain(atualizada);
    }

    // Inativar conta bancaria
    @Transactional
    public void inativar(String numeroConta) {

        Conta conta = obterConta(numeroConta);

        if (!conta.isAtiva()) {
            return;
        }

        conta.inativar();

        contaRepository.salvar(conta);
    }

    // Localizar conta existente
    private Conta obterConta(String numeroConta) {

        return contaRepository.buscarPorNumeroConta(numeroConta)
                .orElseThrow(() -> new NoSuchElementException(
                        "Conta nao encontrada: " + numeroConta
                ));
    }

    // Gerar numero de conta com 10 digitos
    private String gerarNumeroConta() {

        for (int tentativa = 0; tentativa < 10; tentativa++) {

            String numero = Long.toString(
                    ThreadLocalRandom.current().nextLong(
                            1_000_000_000L,
                            10_000_000_000L
                    )
            );

            if (!contaRepository.existePorNumeroConta(numero)) {
                return numero;
            }
        }

        throw new IllegalStateException(
                "Nao foi possivel gerar um numero de conta unico.");
    }

    // Remover mascara do CPF
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
