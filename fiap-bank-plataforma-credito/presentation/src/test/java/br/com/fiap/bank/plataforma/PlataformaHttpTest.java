package br.com.fiap.bank.plataforma;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"core.bancario.url=http://127.0.0.1:1/api/v1", "spring.jpa.show-sql=false"})
class PlataformaHttpTest {
    @Value("${local.server.port}") int port;
    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
            .header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    @Test void legadoValidaPayloadEIdInexistente() throws Exception {
        assertEquals(400, request("POST","/api/credito/solicitarCredito","{}").statusCode());
        assertEquals(400, request("POST","/api/credito/consultarClienteCore","{}").statusCode());
        assertEquals(404, request("POST","/api/credito/buscarSolicitacaoPorId","{\"solicitacaoId\":999999}").statusCode());
        assertEquals(400, request("POST","/api/credito/consultarStatusCredito","{\"cpfCliente\":\"invalido\"}").statusCode());
    }
    @Test void coreIndisponivelNaoInventaConta() throws Exception {
        var response=request("POST","/api/credito/consultarClienteCore","{\"cpfCliente\":\"12345678900\"}");
        assertEquals(200,response.statusCode());
        assertTrue(response.body().contains("INDISPONIVEL"));
    }
    @Test void fluxoRestELegadoCompartilhamEstado() throws Exception {
        String body="""
            {"cpfCliente":"12345678900","nomeCliente":"Teste Integracao","tipoCredito":"PESSOAL",
             "valorSolicitado":100000,"quantidadeParcelas":12,"rendaMensal":1000}
            """;
        var created=request("POST","/api/v1/creditos",body);
        assertEquals(201,created.statusCode(),created.body());
        assertTrue(created.body().contains("EM_ANALISE"));
        var matcher=java.util.regex.Pattern.compile("\"id\":(\\d+)").matcher(created.body());
        assertTrue(matcher.find());String id=matcher.group(1);
        assertEquals(200,request("POST","/api/credito/aprovarSolicitacao","{\"solicitacaoId\":"+id+",\"parecerOperador\":\"OK\"}").statusCode());
        assertTrue(request("GET","/api/v1/creditos/"+id,"").body().contains("APROVADO"));
        assertEquals(400,request("POST","/api/credito/reprovarSolicitacao","{\"solicitacaoId\":"+id+"}").statusCode());
    }
    @Test void statusUsaRecursoSemVerboNaUri() throws Exception {
        assertEquals(400, request("PUT","/api/v1/creditos/1/status","{\"status\":\"INVALIDO\"}").statusCode());
        assertEquals(404, request("PUT","/api/v1/creditos/999999/status","{\"status\":\"APROVADO\"}").statusCode());
    }
}
