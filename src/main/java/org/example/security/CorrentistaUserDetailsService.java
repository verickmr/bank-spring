package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CorrentistaUserDetailsService implements UserDetailsService {

    private final CorrentistaRepository correntistaRepository;

    @Override
    public UserDetails loadUserByUsername(String cpf) {
        Correntista correntista = correntistaRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Correntista não encontrado."
                ));

        return new User(
                correntista.getCpf(),
                correntista.getSenha(),
                Collections.emptyList()
        );
    }
}
