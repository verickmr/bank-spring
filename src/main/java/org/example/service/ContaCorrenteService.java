package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.TipoTransacao;
import org.example.exception.BusinessRuleException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.ContaCorrente;
import org.example.model.Correntista;
import org.example.model.Transacao;
import org.example.repository.ContaRepository;
import org.example.repository.CorrentistaRepository;
import org.example.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContaCorrenteService {

    private final ContaRepository contaRepository;
    private final CorrentistaRepository correntistaRepository;
    private final TransacaoRepository transacaoRepository;

    public Correntista buscarCorrentista(Long id) {
        return correntistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correntista não encontrado."));
    }

    public ContaCorrente salvar(ContaCorrente conta) {
        return contaRepository.save(conta);
    }

    @Transactional
    public Transacao depositar(Long id, double valor) {
        if (valor <= 0) throw new BusinessRuleException("O valor do depósito deve ser positivo.");

        ContaCorrente conta = (ContaCorrente) contaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Conta corrente não encontrada."));

        conta.setSaldo(conta.getSaldo() + valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.DEPOSITO, valor, "Depósito realizado.");
    }

    @Transactional
    public Transacao sacar(Long id, double valor) {
        if (valor <= 0) throw new BusinessRuleException("O valor do saque deve ser positivo.");

        ContaCorrente conta = (ContaCorrente) contaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Conta corrente não encontrada."));

        double limiteDisponivel = conta.getSaldo() + conta.getLimite();
        if (valor > limiteDisponivel) {
            throw new BusinessRuleException("Saldo insuficiente (saldo + limite).");
        }

        conta.setSaldo(conta.getSaldo() - valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.SAQUE, valor, "Saque realizado.");
    }

    @Transactional
    public Transacao aplicarJuros(Long id, double taxa) {
        if (taxa <= 0) throw new BusinessRuleException("A taxa de juros deve ser positiva.");

        ContaCorrente conta = (ContaCorrente) contaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Conta corrente não encontrada."));

        if (conta.getSaldo() >= 0) {
            throw new BusinessRuleException("Juros só podem ser aplicados em saldo negativo.");
        }

        double juros = Math.abs(conta.getSaldo()) * taxa;
        conta.setSaldo(conta.getSaldo() - juros);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.JUROS, juros,
                "Juros aplicados (" + (taxa * 100) + "%).");
    }

    private Transacao criarTransacao(ContaCorrente conta, TipoTransacao tipo, double valor, String descricao) {
        Transacao transacao = Transacao.builder()
                .conta(conta)
                .tipo(tipo)
                .valor(valor)
                .data(LocalDateTime.now())
                .descricao(descricao)
                .build();

        return transacaoRepository.save(transacao);
    }
}
