package br.com.fiap.bank.mesa;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MesaHttpTest {
    static HttpServer stub;
    static volatile boolean unavailable;
    static volatile String lastBody;
    static final String ITEM="""
        {"id":99,"cpfCliente":"12345678900","nomeCliente":"Cliente Teste","tipoCredito":"PESSOAL",
         "valorSolicitado":10000,"quantidadeParcelas":12,"rendaMensal":2000,"scoreCredito":805,
         "status":"EM_ANALISE","parecerOperador":"Em analise","scrRestricoes":false,"scoreBureau":805,
         "dataSolicitacao":"2026-10-17T10:00:00","dataAtualizacao":"2026-10-17T10:00:00"}
        """;
    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) throws Exception {
        stub=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        stub.createContext("/api/credito", exchange -> {
            lastBody=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
            String response=exchange.getRequestURI().getPath().endsWith("listarTodasSolicitacoes") ? "["+ITEM+"]" : ITEM;
            byte[] bytes=response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type","application/json");
            exchange.sendResponseHeaders(unavailable?503:200,bytes.length);
            exchange.getResponseBody().write(bytes);exchange.close();
        });stub.start();
        registry.add("plataforma.credito.url",()->"http://127.0.0.1:"+stub.getAddress().getPort());
    }
    @Value("${local.server.port}") int port;
    @AfterAll static void stop(){stub.stop(0);}
    @BeforeEach void reset(){unavailable=false;lastBody="";}
    HttpResponse<String> get(String path)throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).GET().build(),HttpResponse.BodyHandlers.ofString());
    }
    @Test void renderizaDashboardEAnaliseComDadosReaisDoContrato()throws Exception {
        var dashboard=get("/dashboard");assertEquals(200,dashboard.statusCode());assertTrue(dashboard.body().contains("Cliente Teste"));
        var analise=get("/solicitacao/99");assertEquals(200,analise.statusCode(),analise.body());
        assertTrue(analise.body().contains("Confirmar Aprovação"));assertFalse(analise.body().contains("[ngClass]"));
    }
    @Test void mostraIndisponibilidadeSemErro500()throws Exception {
        unavailable=true;var page=get("/dashboard");assertEquals(200,page.statusCode());assertTrue(page.body().contains("Tente novamente"));
    }
    @Test void enviaFormularioPeloContratoLegado()throws Exception {
        for(String action: new String[]{"aprovar","reprovar"}) {
            var response=HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/"+action))
                .header("Content-Type","application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString("solicitacaoId=99&parecerOperador=OK&motivoReprovacao=Renda")).build(),HttpResponse.BodyHandlers.ofString());
            assertEquals(302,response.statusCode());assertTrue(lastBody.contains("99"));
            assertTrue(response.headers().firstValue("location").orElseThrow().contains("success="));
        }
    }
}
