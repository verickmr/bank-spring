package org.example.controller;

import org.example.service.ContaCorrenteService;
import org.example.service.ContaPoupancaService;
import org.example.service.ContaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaService contaService;

    @MockBean
    private ContaCorrenteService contaCorrenteService;

    @MockBean
    private ContaPoupancaService contaPoupancaService;

    @Test
    void deveRetornarBadRequestQuandoValorNaoForInformado() throws Exception {
        mockMvc.perform(post("/api/contas/corrente/1/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("O valor é obrigatório."))
                .andExpect(jsonPath("$.path")
                        .value("/api/contas/corrente/1/depositar"));

        verificarServicosSemInteracao();
    }

    @Test
    void deveRetornarBadRequestQuandoValorNaoForPositivo() throws Exception {
        mockMvc.perform(post("/api/contas/corrente/1/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O valor deve ser positivo."));

        verificarServicosSemInteracao();
    }

    @Test
    void deveRetornarBadRequestQuandoTaxaNaoForPositiva() throws Exception {
        mockMvc.perform(post("/api/contas/poupanca/1/taxa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taxa\": -0.01}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A taxa deve ser positiva."));

        verificarServicosSemInteracao();
    }

    @Test
    void deveRetornarBadRequestQuandoJsonForInvalido() throws Exception {
        mockMvc.perform(post("/api/contas/corrente/1/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Corpo da requisição inválido ou ausente."));

        verificarServicosSemInteracao();
    }

    private void verificarServicosSemInteracao() {
        verifyNoInteractions(
                contaService,
                contaCorrenteService,
                contaPoupancaService
        );
    }
}
