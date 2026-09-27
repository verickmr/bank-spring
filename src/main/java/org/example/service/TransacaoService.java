package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.TransacaoResponse;
import org.example.model.Transacao;
import org.example.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public List<TransacaoResponse> listarTodas() {
        return transacaoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransacaoResponse> listarPorConta(Long contaId) {
        return transacaoRepository.findByContaId(contaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransacaoResponse toResponse(Transacao transacao) {
        return new TransacaoResponse(
                transacao.getId(),
                transacao.getTipo().name(),
                transacao.getValor(),
                transacao.getData(),
                transacao.getDescricao(),
                transacao.getConta().getId()
        );
    }
}
