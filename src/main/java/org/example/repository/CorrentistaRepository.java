package org.example.repository;

import org.example.model.Correntista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrentistaRepository extends JpaRepository<Correntista,Long> {
    boolean existsByCpf(String cpf);

}
