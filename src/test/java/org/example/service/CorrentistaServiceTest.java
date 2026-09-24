package org.example.service;

import org.example.dto.CorrentistaRequest;
import org.example.exception.ConflictException;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrentistaServiceTest {

    @Mock
    private CorrentistaRepository correntistaRepository;

    @InjectMocks
    private CorrentistaService service;

    @Test
    void deveRejeitarCadastroQuandoCpfJaExistir() {
        CorrentistaRequest request = new CorrentistaRequest();
        request.setCpf("12345678900");
        request.setNome("Victor Erick");
        request.setEmail("victor@email.com");

        when(correntistaRepository.existsByCpf("12345678900"))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.salvar(request)
        );

        assertEquals(
                "Já existe um correntista com o CPF informado.",
                exception.getMessage()
        );

        verify(correntistaRepository, never())
                .save(any(Correntista.class));
    }

    @Test
    void deveRejeitarAtualizacaoQuandoCpfPertencerAOutroCorrentista() {
        Correntista existente = Correntista.builder()
                .id(1L)
                .cpf("12345678900")
                .nome("Victor Erick")
                .email("victor@email.com")
                .build();

        CorrentistaRequest request = new CorrentistaRequest();
        request.setCpf("98765432100");
        request.setNome("Victor Atualizado");
        request.setEmail("novo@email.com");

        when(correntistaRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(correntistaRepository.existsByCpfAndIdNot(
                "98765432100",
                1L
        )).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.atualizar(1L, request)
        );

        assertEquals(
                "Já existe um correntista com o CPF informado.",
                exception.getMessage()
        );

        verify(correntistaRepository, never())
                .save(any(Correntista.class));
    }
}
