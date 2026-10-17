package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.infrastructure.client.CoreBancarioFeignClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CoreBancarioClientService {

    private final CoreBancarioFeignClient coreBancarioFeignClient;

    public CoreBancarioClientService(
            CoreBancarioFeignClient coreBancarioFeignClient) {
        this.coreBancarioFeignClient = coreBancarioFeignClient;
    }

    /**x'
     * Consulta as contas do cliente no Core Bancario via OpenFeign.
     * Mantem o formato de resposta usado pela Plataforma de Credito.
     */
    public Map<String, Object> consultarContaCliente(String cpf) {
        try {
            List<Map<String, Object>> contas =
                    coreBancarioFeignClient.consultarContaCliente(cpf);

            if (contas == null || contas.isEmpty()) {
                return respostaSemConta("SEM_CONTA");
            }

            // Prioriza uma conta ativa, caso o cliente tenha varias.
            Map<String, Object> conta = contas.stream()
                    .filter(c -> Boolean.TRUE.equals(c.get("ativa")))
                    .findFirst()
                    .orElse(contas.get(0));

            boolean ativa = Boolean.TRUE.equals(conta.get("ativa"));

            return Map.of(
                    "statusConta", ativa ? "ATIVA" : "INATIVA",
                    "saldo", conta.get("saldo") != null
                            ? conta.get("saldo")
                            : BigDecimal.ZERO,
                    "agencia", Objects.toString(
                            conta.get("agencia"), ""),
                    "numeroConta", Objects.toString(
                            conta.get("numeroConta"), "")
            );

        } catch (Exception e) {
            System.err.println(
                    "[CORE BANCARIO CLIENT] Falha ao consultar CPF "
                            + cpf + ": " + e.getMessage()
            );

            e.printStackTrace();

            return respostaSemConta("INDISPONIVEL");
        }
    }

    private Map<String, Object> respostaSemConta(String status) {
        return Map.of(
                "statusConta", status,
                "saldo", BigDecimal.ZERO,
                "agencia", "",
                "numeroConta", ""
        );
    }
}