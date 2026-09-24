package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.exception.BusinessRuleException;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class ContaPoupanca extends Conta {
    @Override
    public void sacar(double valor) {
        validarValorPositivo(
                valor,
                "O valor do saque deve ser positivo."
        );

        if (valor > saldo) {
            throw new BusinessRuleException(
                    "Saldo insuficiente para saque na poupança."
            );
        }

        saldo -= valor;
    }

    public double aplicarRendimento(double taxa) {
        validarValorPositivo(
                taxa,
                "A taxa de rendimento deve ser positiva."
        );

        double rendimento = saldo * taxa;
        saldo += rendimento;

        return rendimento;
    }
}
