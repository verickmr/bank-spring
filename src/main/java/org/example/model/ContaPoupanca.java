package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ContaPoupanca extends Conta {
}
