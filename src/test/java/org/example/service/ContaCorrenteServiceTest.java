package org.example.service;

import org.example.enums.TipoTransacao;
import org.example.exception.BusinessRuleException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Conta;
import org.example.model.ContaCorrente;
import org.example.model.ContaPoupanca;
import org.example.model.Transacao;
import org.example.repository.ContaRepository;
import org.example.repository.CorrentistaRepository;
import org.example.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaCorrenteServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private ContaCorrenteService service;

    @Test
    void devePermitirSaqueAteSaldoMaisLimite() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(decimal("1000.00"));
        conta.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.sacar(1L, decimal("1500.00"));

        assertEquals(decimal("-500.00"), conta.getSaldo());
        assertEquals(decimal("1500.00"), transacao.getValor());
        assertEquals(TipoTransacao.SAQUE, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarSaqueAcimaDoSaldoMaisLimite() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(decimal("1000.00"));
        conta.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.sacar(1L, decimal("1500.01"))
        );

        assertEquals(
                "Saldo insuficiente (saldo + limite).",
                exception.getMessage()
        );
        assertEquals(decimal("1000.00"), conta.getSaldo());

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveDepositarERegistrarTransacao() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(decimal("1000.00"));
        conta.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.depositar(1L, decimal("250.00"));

        assertEquals(decimal("1250.00"), conta.getSaldo());
        assertEquals(decimal("250.00"), transacao.getValor());
        assertEquals(TipoTransacao.DEPOSITO, transacao.getTipo());
        assertEquals("Depósito realizado.", transacao.getDescricao());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-10.00"})
    void deveRejeitarDepositoComValorNaoPositivo(String valor) {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(decimal("1000.00"));
        conta.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.depositar(1L, decimal(valor))
        );

        assertEquals(
                "O valor do depósito deve ser positivo.",
                exception.getMessage()
        );
        assertEquals(decimal("1000.00"), conta.getSaldo());

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveAplicarJurosSobreSaldoNegativo() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(decimal("-500.00"));
        conta.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.aplicarJuros(1L, decimal("0.02"));

        assertEquals(decimal("-510.00"), conta.getSaldo());
        assertEquals(decimal("10.00"), transacao.getValor());
        assertEquals(TipoTransacao.JUROS, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarJurosQuandoSaldoNaoForNegativo() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(decimal("0.00"));
        conta.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.aplicarJuros(1L, decimal("0.02"))
        );

        assertEquals(
                "Juros só podem ser aplicados em saldo negativo.",
                exception.getMessage()
        );
        assertEquals(decimal("0.00"), conta.getSaldo());

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarOperacaoQuandoContaCorrenteNaoExistir() {
        when(contaRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.depositar(99L, decimal("100.00"))
        );

        assertEquals(
                "Conta corrente não encontrada.",
                exception.getMessage()
        );

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarOperacaoQuandoContaNaoForCorrente() {
        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setId(1L);
        contaPoupanca.setSaldo(decimal("1000.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(contaPoupanca));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.depositar(1L, decimal("100.00"))
        );

        assertEquals(
                "A conta informada não é uma conta corrente.",
                exception.getMessage()
        );
        assertEquals(decimal("1000.00"), contaPoupanca.getSaldo());

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-0.01"})
    void deveRejeitarTaxaDeJurosNaoPositiva(String taxa) {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(decimal("-500.00"));
        conta.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.aplicarJuros(1L, decimal(taxa))
        );

        assertEquals(
                "A taxa de juros deve ser positiva.",
                exception.getMessage()
        );

        assertEquals(decimal("-500.00"), conta.getSaldo());
        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }
    private BigDecimal decimal(String valor) {
        return new BigDecimal(valor);
    }
}
