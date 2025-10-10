package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CorrentistaRequest;
import org.example.dto.CorrentistaResponse;
import org.example.dto.ContaResponse;
import org.example.exeption.ResourceNotFoundException;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorrentistaService {

    private final CorrentistaRepository correntistaRepository;

    public List<CorrentistaResponse> listarTodos() {
        return correntistaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CorrentistaResponse buscarPorId(Long id) {
        Correntista correntista = correntistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correntista não encontrado."));
        return toResponse(correntista);
    }

    public CorrentistaResponse salvar(CorrentistaRequest request) {
        Correntista novo = Correntista.builder()
                .cpf(request.getCpf())
                .nome(request.getNome())
                .email(request.getEmail())
                .build();

        Correntista salvo = correntistaRepository.save(novo);
        return toResponse(salvo);
    }

    public CorrentistaResponse atualizar(Long id, CorrentistaRequest atualizado) {
        Correntista existente = correntistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correntista não encontrado."));

        existente.setNome(atualizado.getNome());
        existente.setCpf(atualizado.getCpf());
        existente.setEmail(atualizado.getEmail());

        Correntista salvo = correntistaRepository.save(existente);
        return toResponse(salvo);
    }

    public void deletar(Long id) {
        Correntista existente = correntistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correntista não encontrado."));
        correntistaRepository.delete(existente);
    }

    private CorrentistaResponse toResponse(Correntista c) {
        List<ContaResponse> contas;

        if (c.getContas() == null || c.getContas().isEmpty()) {
            contas = Collections.emptyList();
        } else {
            contas = c.getContas().stream()
                    .map(conta -> new ContaResponse(
                            conta.getId(),
                            conta.getNumero(),
                            conta.getSaldo(),
                            conta.getClass().getSimpleName(),
                            conta.getCorrentista() != null ? conta.getCorrentista().getId() : null
                    ))
                    .collect(Collectors.toList());
        }

        return new CorrentistaResponse(
                c.getId(),
                c.getCpf(),
                c.getNome(),
                c.getEmail(),
                contas
        );
    }
}
