package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Transacao;
import org.example.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @GetMapping
    public ResponseEntity<List<Transacao>> listarTodas() {
        return ResponseEntity.ok(transacaoService.listarTodas());
    }

    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<Transacao>> listarPorConta(@PathVariable Long contaId) {
        return ResponseEntity.ok(transacaoService.listarPorConta(contaId));
    }
}
