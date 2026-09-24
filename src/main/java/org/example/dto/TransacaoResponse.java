package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransacaoResponse {
    private Long id;
    private String tipo;
    private BigDecimal valor;
    private LocalDateTime data;
    private String descricao;
    private Long contaId;
}
