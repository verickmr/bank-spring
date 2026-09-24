package org.example.service;

import org.example.enums.TipoTransacao;
import org.example.exception.BusinessRuleException;
import org.example.model.Conta;
import org.example.model.ContaCorrente;
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
        conta.setSaldo(1000.00);
        conta.setLimite(500.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.sacar(1L, 1500.00);

        assertEquals(-500.00, conta.getSaldo(), 0.001);
        assertEquals(1500.00, transacao.getValor(), 0.001);
        assertEquals(TipoTransacao.SAQUE, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarSaqueAcimaDoSaldoMaisLimite() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(1000.00);
        conta.setLimite(500.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.sacar(1L, 1500.01)
        );

        assertEquals(
                "Saldo insuficiente (saldo + limite).",
                exception.getMessage()
        );
        assertEquals(1000.00, conta.getSaldo(), 0.001);

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveDepositarERegistrarTransacao() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(1000.00);
        conta.setLimite(500.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.depositar(1L, 250.00);

        assertEquals(1250.00, conta.getSaldo(), 0.001);
        assertEquals(250.00, transacao.getValor(), 0.001);
        assertEquals(TipoTransacao.DEPOSITO, transacao.getTipo());
        assertEquals("Depósito realizado.", transacao.getDescricao());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -10.0})
    void deveRejeitarDepositoComValorNaoPositivo(double valor) {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(1000.00);
        conta.setLimite(500.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.depositar(1L, valor)
        );

        assertEquals(
                "O valor do depósito deve ser positivo.",
                exception.getMessage()
        );
        assertEquals(1000.00, conta.getSaldo(), 0.001);

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveAplicarJurosSobreSaldoNegativo() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(-500.00);
        conta.setLimite(500.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.aplicarJuros(1L, 0.02);

        assertEquals(-510.00, conta.getSaldo(), 0.001);
        assertEquals(10.00, transacao.getValor(), 0.001);
        assertEquals(TipoTransacao.JUROS, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarJurosQuandoSaldoNaoForNegativo() {
        ContaCorrente conta = new ContaCorrente();
        conta.setId(1L);
        conta.setSaldo(0.00);
        conta.setLimite(500.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.aplicarJuros(1L, 0.02)
        );

        assertEquals(
                "Juros só podem ser aplicados em saldo negativo.",
                exception.getMessage()
        );
        assertEquals(0.00, conta.getSaldo(), 0.001);

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.01})
    void deveRejeitarTaxaDeJurosNaoPositiva(double taxa) {
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.aplicarJuros(1L, taxa)
        );

        assertEquals(
                "A taxa de juros deve ser positiva.",
                exception.getMessage()
        );

        verifyNoInteractions(contaRepository, transacaoRepository);
    }
}
