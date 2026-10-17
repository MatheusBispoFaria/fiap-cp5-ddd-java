package br.com.fiap.bank.plataforma.application.exception;

public class SolicitacaoNaoEncontradaException extends RuntimeException {

    public SolicitacaoNaoEncontradaException(Long id) {
        super("Solicitacao de credito nao encontrada com ID: " + id);
    }
}
