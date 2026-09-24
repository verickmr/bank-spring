package org.example.controller;

import org.example.model.ContaCorrente;
import org.example.model.Correntista;
import org.example.service.ContaCorrenteService;
import org.example.service.ContaPoupancaService;
import org.example.service.ContaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
    void deveCriarContaCorrenteComSaldoZero() throws Exception {
        Correntista correntista = new Correntista();
        correntista.setId(1L);

        when(contaCorrenteService.buscarCorrentista(1L))
                .thenReturn(correntista);
        when(contaCorrenteService.salvar(any(ContaCorrente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/contas/corrente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"0001-01\",\"limite\":500.0,\"correntistaId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("0001-01"))
                .andExpect(jsonPath("$.saldo").value(0.0))
                .andExpect(jsonPath("$.limite").value(500.0));

        ArgumentCaptor<ContaCorrente> contaCaptor =
                ArgumentCaptor.forClass(ContaCorrente.class);

        verify(contaCorrenteService).salvar(contaCaptor.capture());
        assertEquals(0.0, contaCaptor.getValue().getSaldo(), 0.001);
    }

    @Test
    void deveRejeitarAberturaSemNumeroDaConta() throws Exception {
        mockMvc.perform(post("/api/contas/poupanca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correntistaId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("O número da conta é obrigatório."));

        verificarServicosSemInteracao();
    }

    @Test
    void deveRejeitarAberturaDeContaCorrenteSemLimite() throws Exception {
        mockMvc.perform(post("/api/contas/corrente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"0001-01\",\"correntistaId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("O limite é obrigatório para conta corrente."));

        verificarServicosSemInteracao();
    }

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
