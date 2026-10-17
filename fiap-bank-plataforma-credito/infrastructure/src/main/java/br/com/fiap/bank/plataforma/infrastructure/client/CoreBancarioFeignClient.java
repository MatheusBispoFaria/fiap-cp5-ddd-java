package br.com.fiap.bank.plataforma.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "coreBancarioClient",
        url = "${core.bancario.url:http://localhost:8081/api/v1}"
)
public interface CoreBancarioFeignClient {

    @GetMapping("/contas/cliente/{cpf}")
    List<Map<String, Object>> consultarContaCliente(
            @PathVariable("cpf") String cpf
    );
}
