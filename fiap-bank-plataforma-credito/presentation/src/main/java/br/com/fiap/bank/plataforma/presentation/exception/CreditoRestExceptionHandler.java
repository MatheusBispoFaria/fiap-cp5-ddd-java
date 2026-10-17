package br.com.fiap.bank.plataforma.presentation.exception;

import br.com.fiap.bank.plataforma.application.exception.SolicitacaoNaoEncontradaException;
import br.com.fiap.bank.plataforma.presentation.controller.CreditoRestController;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = CreditoRestController.class)
public class CreditoRestExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> tratarValidacao(
                MethodArgumentNotValidException ex) {

                Map<String, String> campos = new LinkedHashMap<>();

                ex.getBindingResult().getFieldErrors().forEach(erro ->
                        campos.putIfAbsent(
                                erro.getField(),
                                erro.getDefaultMessage()
                        )
                );

                Map<String, Object> resposta = new LinkedHashMap<>();
                resposta.put("status", 400);
                resposta.put("erro", "Dados invalidos");
                resposta.put("campos", campos);

                return ResponseEntity.badRequest().body(resposta);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Map<String, Object>> tratarJsonInvalido(
                HttpMessageNotReadableException ex) {

                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "erro", "Corpo da requisicao invalido"
                ));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(
                IllegalArgumentException ex) {

                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "erro", ex.getMessage() != null
                                ? ex.getMessage()
                                : "Argumento invalido"
                ));
        }


        @ExceptionHandler(SolicitacaoNaoEncontradaException.class)
        public ResponseEntity<Map<String, Object>> tratarNaoEncontrada(
                SolicitacaoNaoEncontradaException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "status", 404,
                        "erro", ex.getMessage()
                )
        );
        }

}
