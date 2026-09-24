package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ContaResponse {
    private Long id;
    private String numero;
    private BigDecimal saldo;
    private String tipo;
    private Long correntistaId;
}
