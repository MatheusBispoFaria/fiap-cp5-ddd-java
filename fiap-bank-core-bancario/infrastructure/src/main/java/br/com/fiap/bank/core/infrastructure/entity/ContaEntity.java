package br.com.fiap.bank.core.infrastructure.entity;

import br.com.fiap.bank.core.domain.model.TipoConta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_conta")
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conta",
            nullable = false,
            unique = true)
    private String numeroConta;

    @Column(nullable = false, length = 4)
    private String agencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false)
    private TipoConta tipoConta;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Column(nullable = false)
    private boolean ativa;

}
