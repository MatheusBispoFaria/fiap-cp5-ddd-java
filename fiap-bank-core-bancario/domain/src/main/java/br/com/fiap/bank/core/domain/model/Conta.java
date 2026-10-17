package br.com.fiap.bank.core.domain.model;

import java.math.BigDecimal;

public class Conta {

    private Long id;
    private String numeroConta;
    private String agencia;
    private TipoConta tipoConta;
    private Saldo saldo;
    private Long clienteId;
    private StatusConta status;

    public Conta() {
    }

    public Conta(Long id, String numeroConta, String agencia,
                 TipoConta tipoConta, BigDecimal saldo,
                 Long clienteId, boolean ativa) {

        this.id = id;
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.tipoConta = tipoConta;
        this.saldo = new Saldo(saldo);
        this.clienteId = clienteId;
        this.status = ativa ? StatusConta.ATIVA : StatusConta.ENCERRADA;
    }

    public void depositar(BigDecimal valor) {
        validarMovimentacao(valor);
        saldo = saldo.creditar(valor);
    }

    public void sacar(BigDecimal valor) {
        validarMovimentacao(valor);

        if (saldo.valor().compareTo(valor) < 0) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente para realizar o saque.");
        }

        saldo = saldo.debitar(valor);
    }

    public void inativar() {
        this.status = StatusConta.ENCERRADA;
    }

    private void validarMovimentacao(BigDecimal valor) {
        if (status != StatusConta.ATIVA) {
            throw new IllegalStateException(
                    "Nao e permitido movimentar uma conta inativa.");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor da movimentacao deve ser positivo.");
        }

        if (saldo == null) {
            throw new IllegalStateException(
                    "O saldo da conta nao foi inicializado.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }

    public BigDecimal getSaldo() {
        return saldo.valor();
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public boolean isAtiva() {
        return status == StatusConta.ATIVA;
    }
}
