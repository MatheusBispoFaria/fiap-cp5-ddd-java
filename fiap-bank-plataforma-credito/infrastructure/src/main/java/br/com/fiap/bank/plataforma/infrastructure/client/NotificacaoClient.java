package br.com.fiap.bank.plataforma.infrastructure.client;

import org.springframework.stereotype.Service;

@Service
public class NotificacaoClient {

    /**
     * Envia notificacao externa via SMS/Email para o cliente sobre a atualizacao do status do credito.
     */
    public void enviarNotificacao(String cpf, String mensagem) {
        System.out.println("[SISTEMA DE NOTIFICACOES] SIMULACAO de notificacao para CPF " + cpf + ": " + mensagem);
    }
}
