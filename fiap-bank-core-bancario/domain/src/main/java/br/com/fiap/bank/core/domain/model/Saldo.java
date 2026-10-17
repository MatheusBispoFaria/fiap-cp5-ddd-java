package br.com.fiap.bank.core.domain.model;
import java.math.BigDecimal;
public record Saldo(BigDecimal valor) {
    public Saldo {
        if (valor == null || valor.signum() < 0) throw new IllegalArgumentException("Saldo nao pode ser negativo.");
    }
    public Saldo creditar(BigDecimal quantia) { validar(quantia); return new Saldo(valor.add(quantia)); }
    public Saldo debitar(BigDecimal quantia) {
        validar(quantia);
        if (valor.compareTo(quantia) < 0) throw new IllegalArgumentException("Saldo insuficiente para realizar o saque.");
        return new Saldo(valor.subtract(quantia));
    }
    private static void validar(BigDecimal quantia) {
        if (quantia == null || quantia.signum() <= 0) throw new IllegalArgumentException("O valor deve ser positivo.");
    }
}
