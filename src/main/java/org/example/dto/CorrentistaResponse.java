package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorrentistaResponse {
    private Long id;
    private String cpf;
    private String nome;
    private String email;
    private List<ContaResponse> contas;
}
