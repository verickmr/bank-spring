package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.LoginRequest;
import org.example.dto.TokenResponse;
import org.example.exception.UnauthorizedException;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String CREDENCIAIS_INVALIDAS = "CPF ou senha inválidos.";

    private final CorrentistaRepository correntistaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Correntista correntista = validarCredenciais(request);
        String token = jwtService.gerarToken(correntista);
        return new TokenResponse(token, "Bearer");
    }

    private Correntista validarCredenciais(LoginRequest request) {
        Correntista correntista = correntistaRepository
                .findByCpf(request.getCpf().trim())
                .orElseThrow(() -> new UnauthorizedException(CREDENCIAIS_INVALIDAS));

        if (!passwordEncoder.matches(request.getSenha(), correntista.getSenha())) {
            throw new UnauthorizedException(CREDENCIAIS_INVALIDAS);
        }

        return correntista;
    }
}
