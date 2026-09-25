package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.CorrentistaRequest;
import org.example.dto.CorrentistaResponse;
import org.example.service.CorrentistaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/correntistas")
@RequiredArgsConstructor
@Tag(
        name = "Correntistas",
        description = "Cadastro e consulta dos correntistas da cooperativa."
)
public class CorrentistaController {

    private final CorrentistaService service;

    @Operation(summary = "Lista todos os correntistas")
    @GetMapping
    public ResponseEntity<List<CorrentistaResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Busca um correntista pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<CorrentistaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Cadastra um novo correntista")
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<CorrentistaResponse> salvar(
            @Valid @RequestBody CorrentistaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.salvar(dto));
    }
}
