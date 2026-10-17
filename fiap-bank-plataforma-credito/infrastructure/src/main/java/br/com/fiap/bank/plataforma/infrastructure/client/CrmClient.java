package br.com.fiap.bank.plataforma.infrastructure.client;
import org.springframework.stereotype.Component;
/** Simulacao explicita: nao ha um CRM real configurado neste trabalho. */
@Component
public class CrmClient {
    public String consultarHistorico(String cpf) { return "SEM_HISTORICO_CRM_SIMULADO"; }
}
