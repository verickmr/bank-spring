package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.TipoTransacao;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conta_id")
    private Conta conta;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private double valor;

    private LocalDateTime data;

    private String descricao;
}