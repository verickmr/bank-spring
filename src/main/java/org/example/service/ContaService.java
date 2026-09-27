package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ContaResponse;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Conta;
import org.example.repository.ContaRepository;
import org.example.security.OwnershipGuard;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final OwnershipGuard ownershipGuard;

    public List<ContaResponse> listarContas() {
        return contaRepository.findAllByCorrentistaCpf(ownershipGuard.cpfAtual())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Conta buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));
        ownershipGuard.verificarConta(conta);
        return conta;
    }

    private ContaResponse toResponse(Conta conta) {
        return ContaResponse.builder()
                .id(conta.getId())
                .numero(conta.getNumero())
                .saldo(conta.getSaldo())
                .tipo(conta.getClass().getSimpleName())
                .correntistaId(conta.getCorrentista().getId())
                .build();
    }
}
