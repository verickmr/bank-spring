package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.exception.BusinessRuleException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @Column(nullable = false, precision = 19, scale = 2)
    protected BigDecimal saldo = BigDecimal.ZERO.setScale(2);

    @ManyToOne
    @JoinColumn(name = "correntista_id")
    @JsonBackReference
    private Correntista correntista;

    public void depositar(BigDecimal valor) {
        validarValorPositivo(
                valor,
                "O valor do depósito deve ser positivo."
        );

        saldo = saldo.add(valor).setScale(2, RoundingMode.HALF_EVEN);
    }

    public abstract void sacar(BigDecimal valor);

    protected void validarValorPositivo(BigDecimal valor, String mensagem) {
        if (valor == null || valor.signum() <= 0) {
            throw new BusinessRuleException(mensagem);
        }
    }
}
