package org.example.dto;

import lombok.Data;

@Data
public class CorrentistaRequest {
    private String cpf;
    private String nome;
    private String email;
}
