package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.exception.BusinessRuleException;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContaCorrente extends Conta {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal limite = BigDecimal.ZERO.setScale(2);

    @Override
    public void sacar(BigDecimal valor) {
        validarValorPositivo(
                valor,
                "O valor do saque deve ser positivo."
        );

        BigDecimal saldoDisponivel = saldo.add(limite);

        if (valor.compareTo(saldoDisponivel) > 0) {
            throw new BusinessRuleException(
                    "Saldo insuficiente (saldo + limite)."
            );
        }

        saldo = saldo.subtract(valor).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal aplicarJuros(BigDecimal taxa) {
        validarValorPositivo(
                taxa,
                "A taxa de juros deve ser positiva."
        );

        if (saldo.signum() >= 0) {
            throw new BusinessRuleException(
                    "Juros só podem ser aplicados em saldo negativo."
            );
        }

        BigDecimal juros = saldo.abs()
                .multiply(taxa)
                .setScale(2, RoundingMode.HALF_EVEN);
        saldo = saldo.subtract(juros).setScale(2, RoundingMode.HALF_EVEN);

        return juros;
    }
}
