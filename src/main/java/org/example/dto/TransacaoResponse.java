package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransacaoResponse {
    private Long id;
    private String tipo;
    private Double valor;
    private LocalDateTime data;
    private String descricao;
    private Long contaId;
}
