package br.com.fiap.bank.core;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties="spring.jpa.show-sql=false")
class CoreHttpTest {
    @Value("${local.server.port}") int port;
    HttpResponse<String> request(String method,String path,String body)throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
            .header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    String field(String body,String name){
        var m=java.util.regex.Pattern.compile("\""+name+"\":\"?(\\d+)").matcher(body);assertTrue(m.find(),body);return m.group(1);
    }
    @Test void cicloClienteContaSaldoEInativacao()throws Exception {
        String cliente="""
          {"cpf":"123.456.789-00","nome":"Cliente Integracao","email":"cliente@example.com","dataNascimento":"1990-01-01","rendaMensal":7500}
          """;
        var c=request("POST","/api/v1/clientes",cliente);assertEquals(201,c.statusCode(),c.body());String id=field(c.body(),"id");
        assertEquals(409,request("POST","/api/v1/clientes",cliente).statusCode());
        assertEquals(200,request("PUT","/api/v1/clientes/"+id,cliente.replace("Cliente Integracao","Cliente Atualizado")).statusCode());
        var conta=request("POST","/api/v1/contas","{\"cpfCliente\":\"12345678900\",\"tipoConta\":\"CORRENTE\",\"saldoInicial\":12500}");
        assertEquals(201,conta.statusCode(),conta.body());String numero=field(conta.body(),"numeroConta");
        assertTrue(conta.body().contains("0001"));
        var saldo=request("PUT","/api/v1/contas/"+numero+"/saldo","{\"operacao\":\"DEPOSITO\",\"valor\":200}");
        assertEquals(200,saldo.statusCode());assertTrue(saldo.body().contains("12700"));
        assertEquals(400,request("PUT","/api/v1/contas/"+numero+"/saldo","{\"operacao\":\"SAQUE\",\"valor\":999999}").statusCode());
        assertEquals(204,request("DELETE","/api/v1/contas/"+numero,"").statusCode());
        assertEquals(204,request("DELETE","/api/v1/contas/"+numero,"").statusCode());
        assertTrue(request("GET","/api/v1/contas/cliente/12345678900","").body().contains("\"ativa\":false"));
        assertEquals(409,request("PUT","/api/v1/contas/"+numero+"/saldo","{\"operacao\":\"DEPOSITO\",\"valor\":1}").statusCode());
        assertEquals(409,request("DELETE","/api/v1/clientes/"+id,"").statusCode());
    }
    @Test void validacaoENaoEncontrado()throws Exception {
        assertEquals(400,request("POST","/api/v1/clientes","{}").statusCode());
        assertEquals(400,request("POST","/api/v1/contas","{}").statusCode());
        assertEquals(404,request("GET","/api/v1/clientes/999999","").statusCode());
        assertEquals(404,request("GET","/api/v1/contas/cliente/11122233344","").statusCode());
        assertEquals(400,request("GET","/api/v1/contas/cliente/abc12345678900","").statusCode());
    }
    @Test void excluiClienteSemContas()throws Exception {
        var c=request("POST","/api/v1/clientes","{\"cpf\":\"11122233355\",\"nome\":\"Cliente Removido\",\"email\":\"teste@example.com\",\"dataNascimento\":\"1990-01-01\",\"rendaMensal\":0}");
        assertEquals(201,c.statusCode());String id=field(c.body(),"id");
        assertEquals(204,request("DELETE","/api/v1/clientes/"+id,"").statusCode());
        assertEquals(404,request("GET","/api/v1/clientes/"+id,"").statusCode());
    }
}
