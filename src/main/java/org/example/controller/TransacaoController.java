package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.TransacaoResponse;
import org.example.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
@Tag(
        name = "Transações",
        description = "Consulta do histórico de operações financeiras."
)
public class TransacaoController {

    private final TransacaoService transacaoService;

    @Operation(summary = "Lista as transações do correntista autenticado")
    @GetMapping
    public ResponseEntity<List<TransacaoResponse>> listarTodas() {
        return ResponseEntity.ok(transacaoService.listarTodas());
    }

    @Operation(summary = "Consulta o extrato de uma conta própria")
    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<TransacaoResponse>> listarPorConta(@PathVariable Long contaId) {
        return ResponseEntity.ok(transacaoService.listarPorConta(contaId));
    }
}
