package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.TransacaoResponse;
import org.example.model.Transacao;
import org.example.repository.TransacaoRepository;
import org.example.security.OwnershipGuard;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaService contaService;
    private final OwnershipGuard ownershipGuard;

    public List<TransacaoResponse> listarTodas() {
        return transacaoRepository
                .findAllByContaCorrentistaCpf(ownershipGuard.cpfAtual())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransacaoResponse> listarPorConta(Long contaId) {
        contaService.buscarPorId(contaId);
        return transacaoRepository.findByContaId(contaId)
                .stream()
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
