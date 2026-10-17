package br.com.fiap.bank.plataforma.domain.model;
import java.math.BigDecimal;
import java.math.RoundingMode;
/** Politica de concessao pura, independente de HTTP, Spring e persistencia. */
public final class MotorCredito {
    private MotorCredito() {}
    public record Decisao(StatusSolicitacao status, String parecer) {}
    public static Decisao analisar(boolean temRestricaoScr, int scoreBureau, BigDecimal valor, int parcelas, BigDecimal renda) {
        if (valor == null || valor.signum() <= 0 || renda == null || renda.signum() <= 0 || parcelas <= 0)
            throw new IllegalArgumentException("Valor, renda e parcelas devem ser positivos.");
        // 4. Motor de Regras de Credito
        StatusSolicitacao status;
        String parecer;

        if (temRestricaoScr) {

            status = StatusSolicitacao.REPROVADO;
            parecer = "Solicitacao reprovada automaticamente devido "
                    + "a restricoes ativas no SCR (Banco Central).";

        } else if (scoreBureau < 500) {

            status = StatusSolicitacao.REPROVADO;
            parecer = "Solicitacao reprovada automaticamente. "
                    + "Score no Bureau de Credito insuficiente ("
                    + scoreBureau + ").";

        } else {

            BigDecimal parcelaEstimada =
                    valor.divide(
                            BigDecimal.valueOf(parcelas),
                            2,
                            RoundingMode.HALF_UP
                    );

            BigDecimal trintaPercentoRenda =
                    renda
                            .multiply(new BigDecimal("0.30"));

            if (parcelaEstimada.compareTo(trintaPercentoRenda) > 0) {

                status = StatusSolicitacao.EM_ANALISE;
                parecer = "Encaminhado para a Mesa de Operacoes de Credito. "
                        + "Parcela estimada (R$ " + parcelaEstimada
                        + ") compromete mais de 30% da renda informada.";

            } else {

                status = StatusSolicitacao.APROVADO;
                parecer = "Solicitacao aprovada automaticamente "
                        + "pelo Motor de Credito FIAP Bank!";
            }
        }

        return new Decisao(status, parecer);
    }
}
