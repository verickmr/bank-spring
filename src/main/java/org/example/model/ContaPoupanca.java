package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.exception.BusinessRuleException;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class ContaPoupanca extends Conta {
    @Override
    public void sacar(BigDecimal valor) {
        validarValorPositivo(
                valor,
                "O valor do saque deve ser positivo."
        );

        if (valor.compareTo(saldo) > 0) {
            throw new BusinessRuleException(
                    "Saldo insuficiente para saque na poupança."
            );
        }

        saldo = saldo.subtract(valor).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal aplicarRendimento(BigDecimal taxa) {
        validarValorPositivo(
                taxa,
                "A taxa de rendimento deve ser positiva."
        );

        BigDecimal rendimento = saldo.multiply(taxa)
                .setScale(2, RoundingMode.HALF_EVEN);
        saldo = saldo.add(rendimento).setScale(2, RoundingMode.HALF_EVEN);

        return rendimento;
    }
}
