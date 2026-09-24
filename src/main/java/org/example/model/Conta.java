package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.exception.BusinessRuleException;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    protected double saldo;

    @ManyToOne
    @JoinColumn(name = "correntista_id")
    @JsonBackReference
    private Correntista correntista;

    public void depositar(double valor) {
        validarValorPositivo(
                valor,
                "O valor do depósito deve ser positivo."
        );

        saldo += valor;
    }

    public abstract void sacar(double valor);

    protected void validarValorPositivo(double valor, String mensagem) {
        if (valor <= 0) {
            throw new BusinessRuleException(mensagem);
        }
    }
}
