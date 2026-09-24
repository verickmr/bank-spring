package org.example.repository;

import org.example.model.Correntista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorrentistaRepository extends JpaRepository<Correntista,Long> {
    boolean existsByCpf(String cpf);

    Optional<Correntista> findByCpf(String cpf);
}
