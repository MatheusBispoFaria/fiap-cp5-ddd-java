package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.infrastructure.client.CoreBancarioFeignClient;
import org.springframework.stereotype.Service;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CoreBancarioClientService {
    private static final Logger log = LoggerFactory.getLogger(CoreBancarioClientService.class);

    private final CoreBancarioFeignClient coreBancarioFeignClient;

    public CoreBancarioClientService(
            CoreBancarioFeignClient coreBancarioFeignClient) {
        this.coreBancarioFeignClient = coreBancarioFeignClient;
    }

    /**
     * Consulta as contas do cliente no Core Bancario via OpenFeign.
     * Mantem o formato de resposta usado pela Plataforma de Credito.
     */
    public Map<String, Object> consultarContaCliente(String cpf) {
        if (cpf == null || !cpf.matches("^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$")) {
            throw new IllegalArgumentException("O CPF deve possuir 11 digitos.");
        }
        cpf = cpf.replaceAll("\\D", "");
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

        } catch (FeignException.NotFound e) {
            return respostaSemConta("SEM_CONTA");
        } catch (FeignException e) {
            log.warn("Falha na consulta ao Core Bancario (HTTP {}).", e.status());
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