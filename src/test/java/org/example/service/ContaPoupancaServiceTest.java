package org.example.service;

import org.example.enums.TipoTransacao;
import org.example.exception.BusinessRuleException;
import org.example.model.Conta;
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

    @InjectMocks
    private ContaPoupancaService service;

    @Test
    void devePermitirSaqueAteOSaldoDisponivel() {
        ContaPoupanca conta = criarConta(1000.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.sacar(1L, 1000.00);

        assertEquals(0.00, conta.getSaldo(), 0.001);
        assertEquals(1000.00, transacao.getValor(), 0.001);
        assertEquals(TipoTransacao.SAQUE, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveRejeitarSaqueAcimaDoSaldo() {
        ContaPoupanca conta = criarConta(1000.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.sacar(1L, 1000.01)
        );

        assertEquals(
                "Saldo insuficiente para saque na poupança.",
                exception.getMessage()
        );
        assertEquals(1000.00, conta.getSaldo(), 0.001);

        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void deveAplicarRendimentoAoSaldo() {
        ContaPoupanca conta = criarConta(1000.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transacao transacao = service.aplicarRendimento(1L, 0.05);

        assertEquals(1050.00, conta.getSaldo(), 0.001);
        assertEquals(50.00, transacao.getValor(), 0.001);
        assertEquals(TipoTransacao.RENDIMENTO, transacao.getTipo());

        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.01})
    void deveRejeitarTaxaDeRendimentoNaoPositiva(double taxa) {
        ContaPoupanca conta = criarConta(1000.00);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.aplicarRendimento(1L, taxa)
        );

        assertEquals(
                "A taxa de rendimento deve ser positiva.",
                exception.getMessage()
        );

        assertEquals(1000.00, conta.getSaldo(), 0.001);
        verify(contaRepository, never()).save(any(Conta.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    private ContaPoupanca criarConta(double saldo) {
        ContaPoupanca conta = new ContaPoupanca();
        conta.setId(1L);
        conta.setSaldo(saldo);
        return conta;
    }
}