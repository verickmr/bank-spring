package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ContaResponse;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Conta;
import org.example.repository.ContaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    public List<ContaResponse> listarContas() {
        return contaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));
    }


    private ContaResponse toResponse(Conta conta) {
        return ContaResponse.builder()
                .id(conta.getId())
                .numero(conta.getNumero())
                .saldo(conta.getSaldo())
                .tipo(conta.getClass().getSimpleName())
                .build();
    }
}
