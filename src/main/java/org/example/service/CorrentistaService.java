package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ContaResponse;
import org.example.dto.CorrentistaRequest;
import org.example.dto.CorrentistaResponse;
import org.example.exception.ConflictException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorrentistaService {

    private final CorrentistaRepository correntistaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<CorrentistaResponse> listarTodos() {
        return correntistaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CorrentistaResponse buscarPorId(Long id) {
        Correntista correntista = correntistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correntista não encontrado."));
        return toResponse(correntista);
    }

    @Transactional
    public CorrentistaResponse salvar(CorrentistaRequest request) {
        validarCpfDisponivel(request.getCpf());

        Correntista novo = Correntista.builder()
                .cpf(request.getCpf().trim())
                .nome(request.getNome().trim())
                .email(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .senha(passwordEncoder.encode(request.getSenha()))
                .build();

        return toResponse(correntistaRepository.save(novo));
    }

    private void validarCpfDisponivel(String cpf) {
        if (correntistaRepository.existsByCpf(cpf)) {
            throw new ConflictException("Já existe um correntista com o CPF informado.");
        }
    }

    private CorrentistaResponse toResponse(Correntista correntista) {
        List<ContaResponse> contas;

        if (correntista.getContas() == null || correntista.getContas().isEmpty()) {
            contas = Collections.emptyList();
        } else {
            contas = correntista.getContas().stream()
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
                correntista.getId(),
                correntista.getCpf(),
                correntista.getNome(),
                correntista.getEmail(),
                contas
        );
    }
}
