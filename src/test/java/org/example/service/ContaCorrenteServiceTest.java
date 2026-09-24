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
}