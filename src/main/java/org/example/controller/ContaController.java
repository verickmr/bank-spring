package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ContaRequest;
import org.example.enums.TipoConta;
import org.example.model.ContaCorrente;
import org.example.model.ContaPoupanca;
import org.example.service.ContaCorrenteService;
import org.example.service.ContaPoupancaService;
import org.example.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;
    private final ContaCorrenteService contaCorrenteService;
    private final ContaPoupancaService contaPoupancaService;

    @GetMapping
    public ResponseEntity<?> listarContas() {
        return ResponseEntity.ok(contaService.listarContas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tipo}")
    public ResponseEntity<?> criarConta(
            @PathVariable String tipo,
            @RequestBody ContaRequest dto
    ) {
        try {
            TipoConta tipoConta = TipoConta.fromString(tipo);

            switch (tipoConta) {
                case CORRENTE:
                    ContaCorrente cc = new ContaCorrente();
                    cc.setNumero(dto.getNumero());
                    cc.setSaldo(dto.getSaldo());
                    cc.setLimite(dto.getLimite());
                    cc.setCorrentista(contaCorrenteService.buscarCorrentista(dto.getCorrentistaId()));
                    return ResponseEntity.ok(contaCorrenteService.salvar(cc));

                case POUPANCA:
                    ContaPoupanca cp = new ContaPoupanca();
                    cp.setNumero(dto.getNumero());
                    cp.setSaldo(dto.getSaldo());
                    cp.setCorrentista(contaPoupancaService.buscarCorrentista(dto.getCorrentistaId()));
                    return ResponseEntity.ok(contaPoupancaService.salvar(cp));

                default:
                    return ResponseEntity.badRequest().body("Tipo de conta inválido.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{tipo}/{id}/depositar")
    public ResponseEntity<?> depositar(
            @PathVariable String tipo,
            @PathVariable Long id,
            @RequestBody Map<String, Double> body
    ) {
        double valor = body.getOrDefault("valor", 0.0);
        try {
            switch (TipoConta.fromString(tipo)) {
                case CORRENTE:
                    return ResponseEntity.ok(contaCorrenteService.depositar(id, valor));
                case POUPANCA:
                    return ResponseEntity.ok(contaPoupancaService.depositar(id, valor));
                default:
                    return ResponseEntity.badRequest().body("Tipo inválido.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{tipo}/{id}/sacar")
    public ResponseEntity<?> sacar(
            @PathVariable String tipo,
            @PathVariable Long id,
            @RequestBody Map<String, Double> body
    ) {
        double valor = body.getOrDefault("valor", 0.0);
        try {
            switch (TipoConta.fromString(tipo)) {
                case CORRENTE:
                    return ResponseEntity.ok(contaCorrenteService.sacar(id, valor));
                case POUPANCA:
                    return ResponseEntity.ok(contaPoupancaService.sacar(id, valor));
                default:
                    return ResponseEntity.badRequest().body("Tipo inválido.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{tipo}/{id}/taxa")
    public ResponseEntity<?> aplicarTaxa(
            @PathVariable String tipo,
            @PathVariable Long id,
            @RequestBody Map<String, Double> body
    ) {
        double taxa = body.getOrDefault("taxa", 0.0);
        try {
            switch (TipoConta.fromString(tipo)) {
                case CORRENTE:
                    return ResponseEntity.ok(contaCorrenteService.aplicarJuros(id, taxa));
                case POUPANCA:
                    return ResponseEntity.ok(contaPoupancaService.aplicarRendimento(id, taxa));
                default:
                    return ResponseEntity.badRequest().body("Tipo inválido.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
