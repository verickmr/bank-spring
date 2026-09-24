package org.example.controller;

import org.example.dto.CorrentistaRequest;
import org.example.dto.CorrentistaResponse;
import org.example.service.CorrentistaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CorrentistaController.class)
class CorrentistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CorrentistaService service;

    @Test
    void deveCriarCorrentistaERetornarCreated() throws Exception {
        CorrentistaResponse response = new CorrentistaResponse(
                1L,
                "12345678900",
                "Victor Erick",
                "victor@email.com",
                Collections.emptyList()
        );

        when(service.salvar(any(CorrentistaRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"cpf\":\"12345678900\","
                                        + "\"nome\":\"Victor Erick\","
                                        + "\"email\":\"victor@email.com\"}"
                        ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.nome").value("Victor Erick"))
                .andExpect(jsonPath("$.email").value("victor@email.com"))
                .andExpect(jsonPath("$.contas").isArray());
    }

    @Test
    void deveRetornarBadRequestQuandoCpfForInvalido() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"cpf\":\"123\","
                                        + "\"nome\":\"Victor Erick\","
                                        + "\"email\":\"victor@email.com\"}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("O CPF deve conter 11 dígitos."))
                .andExpect(jsonPath("$.path")
                        .value("/api/correntistas"));

        verifyNoInteractions(service);
    }
}
