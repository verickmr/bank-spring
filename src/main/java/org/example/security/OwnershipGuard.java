package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.exception.ForbiddenException;
import org.example.exception.UnauthorizedException;
import org.example.model.Conta;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnershipGuard {

    private static final String ACESSO_NEGADO =
            "Você não possui permissão para acessar este recurso.";

    private final CorrentistaRepository correntistaRepository;

    public String cpfAtual() {
        return authentication().getName();
    }

    public Correntista correntistaAtual() {
        return correntistaRepository.findByCpf(cpfAtual())
                .orElseThrow(() -> new UnauthorizedException(
                        "Correntista autenticado não encontrado."
                ));
    }

    public Long correntistaIdAtual() {
        return correntistaAtual().getId();
    }

    public void verificarCorrentista(Correntista correntista) {
        if (correntista == null
                || !correntistaIdAtual().equals(correntista.getId())) {
            throw new ForbiddenException(ACESSO_NEGADO);
        }
    }

    public void verificarConta(Conta conta) {
        if (conta == null || conta.getCorrentista() == null
                || !correntistaIdAtual().equals(conta.getCorrentista().getId())) {
            throw new ForbiddenException(ACESSO_NEGADO);
        }
    }

    private Authentication authentication() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Autenticação necessária.");
        }

        return authentication;
    }
}
