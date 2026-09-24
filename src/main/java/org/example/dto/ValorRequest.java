package org.example.dto;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class ValorRequest {

    @NotNull(message = "O valor é obrigatório.")
    @Positive(message = "O valor deve ser positivo.")
    @Digits(integer = 17, fraction = 2, message = "O valor deve ter no máximo duas casas decimais.")
    private BigDecimal valor;
}
