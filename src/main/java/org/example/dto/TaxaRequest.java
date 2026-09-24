package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class TaxaRequest {

    @NotNull(message = "A taxa é obrigatória.")
    @Positive(message = "A taxa deve ser positiva.")
    private BigDecimal taxa;
}
