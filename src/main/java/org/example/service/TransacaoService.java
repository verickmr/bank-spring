package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Transacao;
import org.example.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public List<Transacao> listarTodas() {
        return transacaoRepository.findAll();
    }

    public List<Transacao> listarPorConta(Long contaId) {
        return transacaoRepository.findByContaId(contaId);
    }
}
