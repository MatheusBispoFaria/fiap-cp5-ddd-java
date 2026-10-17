
package br.com.fiap.bank.plataforma.presentation.config;

import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.service.PlataformaCreditoService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PlataformaDataInitializer implements CommandLineRunner {

    private final PlataformaCreditoService plataformaService;

    public PlataformaDataInitializer(
            PlataformaCreditoService plataformaService) {
        this.plataformaService = plataformaService;
    }

    @Override
    public void run(String... args) {
        try {
            // Solicitacao 1 - Emprestimo pessoal
            plataformaService.solicitarCredito(new SolicitarCreditoRequest(
                    "12345678900",
                    "Ana Silva",
                    "EMPRESTIMO_PESSOAL",
                    new BigDecimal("15000.00"),
                    24,
                    new BigDecimal("7500.00")
            ));

            // Solicitacao 2 - Credito imobiliario
            plataformaService.solicitarCredito(new SolicitarCreditoRequest(
                    "98765432100",
                    "Carlos Eduardo Oliveira",
                    "CREDITO_IMOBILIARIO",
                    new BigDecimal("350000.00"),
                    180,
                    new BigDecimal("12000.00")
            ));

            // Solicitacao 3 - Credito automotivo
            plataformaService.solicitarCredito(new SolicitarCreditoRequest(
                    "55544433399",
                    "Mariana Souza",
                    "CREDITO_AUTOMOTIVO",
                    new BigDecimal("45000.00"),
                    48,
                    new BigDecimal("4200.00")
            ));

            System.out.println(
                    ">>> Plataforma de Credito: Dados iniciais carregados com sucesso!"
            );

        } catch (Exception e) {
            System.err.println(
                    ">>> Erro ao carregar dados na Plataforma de Credito: "
                            + e.getMessage()
            );
        }
    }
}
