package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public class ContaRequest {

    @NotBlank(message = "O número da conta é obrigatório.")
    private String numero;

    @PositiveOrZero(message = "O limite não pode ser negativo.")
    private Double limite;

    @NotNull(message = "O correntista é obrigatório.")
    @Positive(message = "O identificador do correntista deve ser positivo.")
    private Long correntistaId;
}

