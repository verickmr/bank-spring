package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.TipoTransacao;
import org.example.exception.BusinessRuleException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.ContaPoupanca;
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
public class ContaPoupancaService {

    private final ContaRepository contaRepository;
    private final CorrentistaRepository correntistaRepository;
    private final TransacaoRepository transacaoRepository;

    public Correntista buscarCorrentista(Long id) {
        return correntistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correntista não encontrado."));
    }

    public ContaPoupanca salvar(ContaPoupanca conta) {
        return contaRepository.save(conta);
    }

    @Transactional
    public Transacao depositar(Long id, double valor) {
        if (valor <= 0) throw new BusinessRuleException("O valor do depósito deve ser positivo.");

        ContaPoupanca conta = (ContaPoupanca) contaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Conta poupança não encontrada."));

        conta.setSaldo(conta.getSaldo() + valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.DEPOSITO, valor, "Depósito realizado.");
    }

    @Transactional
    public Transacao sacar(Long id, double valor) {
        if (valor <= 0) throw new BusinessRuleException("O valor do saque deve ser positivo.");

        ContaPoupanca conta = (ContaPoupanca) contaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Conta poupança não encontrada."));

        if (valor > conta.getSaldo()) {
            throw new BusinessRuleException("Saldo insuficiente para saque na poupança.");
        }

        conta.setSaldo(conta.getSaldo() - valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.SAQUE, valor, "Saque realizado.");
    }

    @Transactional
    public Transacao aplicarRendimento(Long id, double taxa) {
        if (taxa <= 0) throw new BusinessRuleException("A taxa de rendimento deve ser positiva.");

        ContaPoupanca conta = (ContaPoupanca) contaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Conta poupança não encontrada."));

        double rendimento = conta.getSaldo() * taxa;
        conta.setSaldo(conta.getSaldo() + rendimento);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.RENDIMENTO, rendimento,
                "Rendimento aplicado (" + (taxa * 100) + "%).");
    }

    private Transacao criarTransacao(ContaPoupanca conta, TipoTransacao tipo, double valor, String descricao) {
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
