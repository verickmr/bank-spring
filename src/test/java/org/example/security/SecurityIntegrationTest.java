package org.example.security;

import org.example.model.Correntista;
import org.example.repository.CorrentistaRepository;
import org.example.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CorrentistaRepository correntistaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Test
    void devePermitirLoginSemToken() throws Exception {
        salvarCorrentista("12345678900", "senha-segura");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"12345678900\",\"senha\":\"senha-segura\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void deveBloquearRotaProtegidaSemToken() throws Exception {
        mockMvc.perform(get("/api/contas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Autenticação necessária."));
    }

    @Test
    void deveBloquearRotaProtegidaComTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/contas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void devePermitirRotaProtegidaComTokenValido() throws Exception {
        Correntista correntista = salvarCorrentista("12345678900", "senha-segura");
        String token = jwtService.gerarToken(correntista);

        mockMvc.perform(get("/api/contas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    private Correntista salvarCorrentista(String cpf, String senha) {
        Correntista correntista = Correntista.builder()
                .cpf(cpf)
                .nome("Victor Erick")
                .email("victor@email.com")
                .senha(passwordEncoder.encode(senha))
                .build();

        return correntistaRepository.save(correntista);
    }
}
