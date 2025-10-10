package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.CorrentistaRequest;
import org.example.dto.CorrentistaResponse;
import org.example.service.CorrentistaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/correntistas")
@RequiredArgsConstructor
public class CorrentistaController {

    private final CorrentistaService service;

    @GetMapping
    public ResponseEntity<List<CorrentistaResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorrentistaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CorrentistaResponse> salvar(@RequestBody CorrentistaRequest dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CorrentistaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody CorrentistaRequest dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
