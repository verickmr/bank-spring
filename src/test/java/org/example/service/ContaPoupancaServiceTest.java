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
import org.example.security.OwnershipGuard;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaPoupancaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private OwnershipGuard ownershipGuard;

    @InjectMocks
    private ContaPoupancaService service;

    @Test
    void devePermitirSaqueAteOSaldoDisponivel() {
        ContaPoupanca conta = criarConta("1000.00");

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.sacar(1L, decimal("1000.00"));

        assertEquals(decimal("0.00"), conta.getSaldo());
        assertEquals(decimal("1000.00"), transacao.getValor());
        assertEquals(TipoTransacao.SAQUE, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarSaqueAcimaDoSaldo() {
        ContaPoupanca conta = criarConta("1000.00");

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.sacar(1L, decimal("1000.01"))
        );

        assertEquals(
                "Saldo insuficiente para saque na poupança.",
                exception.getMessage()
        );
        assertEquals(decimal("1000.00"), conta.getSaldo());

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveAplicarRendimentoAoSaldo() {
        ContaPoupanca conta = criarConta("1000.00");

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.aplicarRendimento(1L, decimal("0.05"));

        assertEquals(decimal("1050.00"), conta.getSaldo());
        assertEquals(decimal("50.00"), transacao.getValor());
        assertEquals(TipoTransacao.RENDIMENTO, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarOperacaoQuandoContaPoupancaNaoExistir() {
        when(contaRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.depositar(99L, decimal("100.00"))
        );

        assertEquals(
                "Conta poupança não encontrada.",
                exception.getMessage()
        );

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarOperacaoQuandoContaNaoForPoupanca() {
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setId(1L);
        contaCorrente.setSaldo(decimal("1000.00"));
        contaCorrente.setLimite(decimal("500.00"));

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(contaCorrente));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.depositar(1L, decimal("100.00"))
        );

        assertEquals(
                "A conta informada não é uma conta poupança.",
                exception.getMessage()
        );
        assertEquals(decimal("1000.00"), contaCorrente.getSaldo());

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-0.01"})
    void deveRejeitarTaxaDeRendimentoNaoPositiva(String taxa) {
        ContaPoupanca conta = criarConta("1000.00");

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.aplicarRendimento(1L, decimal(taxa))
        );

        assertEquals(
                "A taxa de rendimento deve ser positiva.",
                exception.getMessage()
        );

        assertEquals(decimal("1000.00"), conta.getSaldo());
        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    private ContaPoupanca criarConta(String saldo) {
        ContaPoupanca conta = new ContaPoupanca();
        conta.setId(1L);
        conta.setSaldo(decimal(saldo));
        return conta;
    }
    private BigDecimal decimal(String valor) {
        return new BigDecimal(valor);
    }
}
