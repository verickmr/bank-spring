package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.TipoTransacao;
import org.example.exception.BusinessRuleException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Conta;
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

    private ContaPoupanca buscarContaPoupanca(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Conta poupança não encontrada.")
                );

        if (!(conta instanceof ContaPoupanca)) {
            throw new BusinessRuleException(
                    "A conta informada não é uma conta poupança."
            );
        }

        return (ContaPoupanca) conta;
    }

    public ContaPoupanca salvar(ContaPoupanca conta) {
        return contaRepository.save(conta);
    }

    @Transactional
    public Transacao depositar(Long id, double valor) {
        ContaPoupanca conta = buscarContaPoupanca(id);

        conta.depositar(valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.DEPOSITO, valor, "Depósito realizado.");
    }

    @Transactional
    public Transacao sacar(Long id, double valor) {
        ContaPoupanca conta = buscarContaPoupanca(id);

        conta.sacar(valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.SAQUE, valor, "Saque realizado.");
    }

    @Transactional
    public Transacao aplicarRendimento(Long id, double taxa) {
        ContaPoupanca conta = buscarContaPoupanca(id);

        double rendimento = conta.aplicarRendimento(taxa);
        contaRepository.save(conta);

        return criarTransacao(
                conta,
                TipoTransacao.RENDIMENTO,
                rendimento,
                "Rendimento aplicado (" + (taxa * 100) + "%)."
        );
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
