package org.example.dto;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
public class ContaRequest {

    @NotBlank(message = "O número da conta é obrigatório.")
    private String numero;

    @PositiveOrZero(message = "O limite não pode ser negativo.")
    @Digits(integer = 17, fraction = 2, message = "O limite deve ter no máximo duas casas decimais.")
    private BigDecimal limite;

    @NotNull(message = "O correntista é obrigatório.")
    @Positive(message = "O identificador do correntista deve ser positivo.")
    private Long correntistaId;
}
