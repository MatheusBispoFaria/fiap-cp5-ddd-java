package br.com.fiap.bank.plataforma;
import br.com.fiap.bank.plataforma.application.service.CoreBancarioClientService;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CoreClientTest {
    @Test void priorizaContaAtivaENormalizaCpf() {
        var service = new CoreBancarioClientService(cpf -> {
            assertEquals("12345678900", cpf);
            return List.of(Map.of("ativa", false), Map.of("ativa", true, "saldo", 12700, "agencia", "0001", "numeroConta", "123"));
        });
        var result = service.consultarContaCliente("123.456.789-00");
        assertEquals("ATIVA", result.get("statusConta"));
        assertEquals(12700, result.get("saldo"));
        assertEquals("123", result.get("numeroConta"));
    }
    @Test void preservaContaInativa() {
        var service = new CoreBancarioClientService(cpf -> List.of(Map.of("ativa", false, "saldo", 12700)));
        assertEquals("INATIVA", service.consultarContaCliente("12345678900").get("statusConta"));
    }
    @Test void listaVaziaSignificaSemConta() {
        assertEquals("SEM_CONTA", new CoreBancarioClientService(cpf -> List.of()).consultarContaCliente("12345678900").get("statusConta"));
    }
    @Test void distingue404DeFalhaDoServidor() {
        assertEquals("SEM_CONTA", failing(404).consultarContaCliente("12345678900").get("statusConta"));
        assertEquals("INDISPONIVEL", failing(503).consultarContaCliente("12345678900").get("statusConta"));
    }
    @Test void rejeitaCpfInvalidoAntesDaIntegracao() {
        var service = new CoreBancarioClientService(cpf -> { fail("Nao deve chamar o Core"); return List.of(); });
        assertThrows(IllegalArgumentException.class, () -> service.consultarContaCliente(null));
        assertThrows(IllegalArgumentException.class, () -> service.consultarContaCliente("abc12345678900"));
    }
    private CoreBancarioClientService failing(int status) {
        return new CoreBancarioClientService(cpf -> {
            var request = Request.create(Request.HttpMethod.GET, "http://localhost/contas", Map.of(), null, java.nio.charset.StandardCharsets.UTF_8, null);
            throw FeignException.errorStatus("consultar", Response.builder().request(request).status(status).headers(Map.of()).build());
        });
    }
}
