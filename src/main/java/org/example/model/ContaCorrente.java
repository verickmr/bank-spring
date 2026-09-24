package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.exception.BusinessRuleException;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContaCorrente extends Conta {

    private double limite;

    @Override
    public void sacar(double valor) {
        validarValorPositivo(
                valor,
                "O valor do saque deve ser positivo."
        );

        double saldoDisponivel = saldo + limite;

        if (valor > saldoDisponivel) {
            throw new BusinessRuleException(
                    "Saldo insuficiente (saldo + limite)."
            );
        }

        saldo -= valor;
    }
}
