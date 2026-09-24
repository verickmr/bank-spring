package org.example.service;

import org.example.dto.LoginRequest;
import org.example.exception.UnauthorizedException;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService service;

    @Test
    void deveAutenticarQuandoCpfESenhaForemValidos() {
        LoginRequest request = criarRequest();
        Correntista correntista = criarCorrentista();

        when(correntistaRepository.findByCpf("12345678900"))
                .thenReturn(Optional.of(correntista));
        when(passwordEncoder.matches("senhaSegura123", "$2a$10$hash"))
                .thenReturn(true);

        Correntista autenticado = service.autenticar(request);

        assertSame(correntista, autenticado);
        verify(passwordEncoder).matches("senhaSegura123", "$2a$10$hash");
    }

    @Test
    void deveRejeitarSenhaInvalidaComMensagemGenerica() {
        LoginRequest request = criarRequest();
        Correntista correntista = criarCorrentista();

        when(correntistaRepository.findByCpf("12345678900"))
                .thenReturn(Optional.of(correntista));
        when(passwordEncoder.matches("senhaSegura123", "$2a$10$hash"))
                .thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.autenticar(request)
        );

        assertEquals("CPF ou senha inválidos.", exception.getMessage());
    }

    @Test
    void deveRejeitarCpfInexistenteComAMesmaMensagem() {
        LoginRequest request = criarRequest();

        when(correntistaRepository.findByCpf("12345678900"))
                .thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.autenticar(request)
        );

        assertEquals("CPF ou senha inválidos.", exception.getMessage());
        verify(passwordEncoder, never()).matches(
                request.getSenha(),
                "$2a$10$hash"
        );
    }

    private LoginRequest criarRequest() {
        LoginRequest request = new LoginRequest();
        request.setCpf("12345678900");
        request.setSenha("senhaSegura123");
        return request;
    }

    private Correntista criarCorrentista() {
        return Correntista.builder()
                .id(1L)
                .cpf("12345678900")
                .senha("$2a$10$hash")
                .build();
    }
}
