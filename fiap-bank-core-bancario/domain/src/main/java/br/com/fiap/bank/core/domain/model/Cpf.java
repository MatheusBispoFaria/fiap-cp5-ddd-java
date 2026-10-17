package br.com.fiap.bank.core.domain.model;
/** Documento imutavel; o contrato academico exige formato, nao digitos verificadores. */
public record Cpf(String valor) {
    public Cpf {
        if (valor == null || !valor.matches("^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$"))
            throw new IllegalArgumentException("O CPF deve possuir 11 digitos.");
        valor = valor.replaceAll("\\D", "");
    }
}
