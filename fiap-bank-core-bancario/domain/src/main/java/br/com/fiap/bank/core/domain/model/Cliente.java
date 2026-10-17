package br.com.fiap.bank.core.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cliente {

    private Long id;
    private Cpf cpf;
    private String nome;
    private Email email;
    private LocalDate dataNascimento;
    private BigDecimal rendaMensal;

    public Cliente() {
    }

    public Cliente(Long id, String cpf, String nome,
                   String email, LocalDate dataNascimento,
                   BigDecimal rendaMensal) {
        this.id = id;
        this.cpf = new Cpf(cpf);
        this.nome = nome;
        this.email = new Email(email);
        this.dataNascimento = dataNascimento;
        this.rendaMensal = rendaMensal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf.valor();
    }

    public void setCpf(String cpf) {
        this.cpf = new Cpf(cpf);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email.valor();
    }

    public void setEmail(String email) {
        this.email = new Email(email);
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public BigDecimal getRendaMensal() {
        return rendaMensal;
    }

    public void setRendaMensal(BigDecimal rendaMensal) {
        this.rendaMensal = rendaMensal;
    }
}
