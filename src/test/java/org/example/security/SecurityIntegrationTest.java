package org.example.security;

import org.example.model.Correntista;
import org.example.model.ContaCorrente;
import org.example.model.Transacao;
import org.example.repository.ContaRepository;
import org.example.repository.CorrentistaRepository;
import org.example.repository.TransacaoRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.example.enums.TipoTransacao.DEPOSITO;

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
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

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

    @Test
    void deveIsolarOsRecursosPorCorrentista() throws Exception {
        Correntista proprietario = salvarCorrentista(
                "12345678900",
                "senha-segura"
        );
        Correntista outroCorrentista = salvarCorrentista(
                "98765432100",
                "outra-senha"
        );
        ContaCorrente contaPropria = salvarConta(proprietario, "0001-01");
        ContaCorrente contaDeTerceiro = salvarConta(outroCorrentista, "0001-02");
        salvarTransacao(contaPropria, "Operação própria");
        salvarTransacao(contaDeTerceiro, "Operação de terceiro");
        String authorization = "Bearer " + jwtService.gerarToken(proprietario);

        mockMvc.perform(get("/api/correntistas")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(proprietario.getId()));

        mockMvc.perform(get("/api/contas")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(contaPropria.getId()));

        mockMvc.perform(get("/api/contas/" + contaPropria.getId())
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/transacoes")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].conta.id").value(contaPropria.getId()))
                .andExpect(jsonPath("$[0].descricao").value("Operação própria"));

        mockMvc.perform(get("/api/correntistas/" + outroCorrentista.getId())
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(get("/api/contas/" + contaDeTerceiro.getId())
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(post(
                                "/api/contas/corrente/"
                                        + contaDeTerceiro.getId()
                                        + "/sacar"
                        )
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":1.00}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get(
                                "/api/transacoes/conta/"
                                        + contaDeTerceiro.getId()
                        )
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/contas/999999")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBloquearAberturaDeContaParaOutroCorrentista() throws Exception {
        Correntista proprietario = salvarCorrentista(
                "12345678900",
                "senha-segura"
        );
        Correntista outroCorrentista = salvarCorrentista(
                "98765432100",
                "outra-senha"
        );
        String authorization = "Bearer " + jwtService.gerarToken(proprietario);

        mockMvc.perform(post("/api/contas/corrente")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"0001-03\","
                                + "\"limite\":500.00,"
                                + "\"correntistaId\":"
                                + outroCorrentista.getId()
                                + "}"))
                .andExpect(status().isForbidden());
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

    private ContaCorrente salvarConta(Correntista correntista, String numero) {
        ContaCorrente conta = new ContaCorrente();
        conta.setNumero(numero);
        conta.setSaldo(new BigDecimal("100.00"));
        conta.setLimite(new BigDecimal("500.00"));
        conta.setCorrentista(correntista);
        return contaRepository.save(conta);
    }

    private void salvarTransacao(ContaCorrente conta, String descricao) {
        transacaoRepository.save(Transacao.builder()
                .conta(conta)
                .tipo(DEPOSITO)
                .valor(new BigDecimal("10.00"))
                .data(LocalDateTime.now())
                .descricao(descricao)
                .build());
    }
}
