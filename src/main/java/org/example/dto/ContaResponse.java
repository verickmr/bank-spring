package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContaResponse {
    private Long id;
    private String numero;
    private Double saldo;
    private String tipo;
    private Long correntistaId;
}
