package org.example.dto;

import lombok.Data;
import org.example.enums.TipoConta;

@Data
public class ContaRequest {
        private String numero;
        private Double saldo;
        private Double limite;
        private Long correntistaId;
        private TipoConta tipo;
}


