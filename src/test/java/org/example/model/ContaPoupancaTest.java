package org.example.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContaPoupancaTest {

    @Test
    void deveSomarValoresMonetariosSemPerderPrecisao() {
        ContaPoupanca conta = new ContaPoupanca();
        conta.setSaldo(new BigDecimal("0.00"));

        conta.depositar(new BigDecimal("0.10"));
        conta.depositar(new BigDecimal("0.20"));

        assertEquals(new BigDecimal("0.30"), conta.getSaldo());
    }

    @Test
    void deveArredondarRendimentoParaDuasCasasComHalfEven() {
        ContaPoupanca conta = new ContaPoupanca();
        conta.setSaldo(new BigDecimal("100.05"));

        BigDecimal rendimento = conta.aplicarRendimento(new BigDecimal("0.10"));

        assertEquals(new BigDecimal("10.00"), rendimento);
        assertEquals(new BigDecimal("110.05"), conta.getSaldo());
    }
}
