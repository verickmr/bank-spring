package org.example.repository;


import org.example.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta,Long> {
    List<Conta> findAllByCorrentistaCpf(String cpf);
}
