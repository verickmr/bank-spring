package org.example.service;

import org.example.dto.CorrentistaRequest;
import org.example.exception.ConflictException;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.example.security.OwnershipGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrentistaServiceTest {

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OwnershipGuard ownershipGuard;

    @InjectMocks
    private CorrentistaService service;

    @Test
    void deveRejeitarCadastroQuandoCpfJaExistir() {
        CorrentistaRequest request = criarRequest();
        when(correntistaRepository.existsByCpf(request.getCpf())).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.salvar(request)
        );

        assertEquals(
                "Já existe um correntista com o CPF informado.",
                exception.getMessage()
        );
        verify(correntistaRepository, never()).save(any(Correntista.class));
    }

    @Test
    void deveCriptografarSenhaAntesDeSalvar() {
        CorrentistaRequest request = criarRequest();
        when(passwordEncoder.encode(request.getSenha()))
                .thenReturn("$2a$10$hashGeradoParaTeste");
        when(correntistaRepository.save(any(Correntista.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.salvar(request);

        ArgumentCaptor<Correntista> captor = ArgumentCaptor.forClass(Correntista.class);
        verify(correntistaRepository).save(captor.capture());
        assertEquals("$2a$10$hashGeradoParaTeste", captor.getValue().getSenha());
        assertNotEquals(request.getSenha(), captor.getValue().getSenha());
    }

    private CorrentistaRequest criarRequest() {
        CorrentistaRequest request = new CorrentistaRequest();
        request.setCpf("12345678900");
        request.setNome("Victor Erick");
        request.setEmail("victor@email.com");
        request.setSenha("senhaSegura123");
        return request;
    }
}
