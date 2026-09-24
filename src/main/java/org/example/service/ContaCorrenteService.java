package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enums.TipoTransacao;
import org.example.exception.BusinessRuleException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Conta;
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

    private ContaCorrente buscarContaCorrente(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Conta corrente não encontrada.")
                );

        if (!(conta instanceof ContaCorrente)) {
            throw new BusinessRuleException(
                    "A conta informada não é uma conta corrente."
            );
        }

        return (ContaCorrente) conta;
    }

    public ContaCorrente salvar(ContaCorrente conta) {
        return contaRepository.save(conta);
    }

    @Transactional
    public Transacao depositar(Long id, double valor) {
        ContaCorrente conta = buscarContaCorrente(id);

        conta.depositar(valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.DEPOSITO, valor, "Depósito realizado.");
    }

    @Transactional
    public Transacao sacar(Long id, double valor) {
        ContaCorrente conta = buscarContaCorrente(id);

        conta.sacar(valor);
        contaRepository.save(conta);

        return criarTransacao(conta, TipoTransacao.SAQUE, valor, "Saque realizado.");
    }

    @Transactional
    public Transacao aplicarJuros(Long id, double taxa) {
        ContaCorrente conta = buscarContaCorrente(id);

        double juros = conta.aplicarJuros(taxa);
        contaRepository.save(conta);

        return criarTransacao(
                conta,
                TipoTransacao.JUROS,
                juros,
                "Juros aplicados (" + (taxa * 100) + "%)."
        );
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
