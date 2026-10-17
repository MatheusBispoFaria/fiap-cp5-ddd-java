package br.com.fiap.bank.core.domain.model;
public record Email(String valor) {
    public Email {
        if (valor == null || !valor.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            throw new IllegalArgumentException("Email invalido.");
    }
}
