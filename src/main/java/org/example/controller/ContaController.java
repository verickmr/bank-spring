package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.ContaRequest;
import org.example.dto.TaxaRequest;
import org.example.dto.ValorRequest;
import org.example.enums.TipoConta;
import org.example.model.ContaCorrente;
import org.example.model.ContaPoupanca;
import org.example.service.ContaCorrenteService;
import org.example.service.ContaPoupancaService;
import org.example.service.ContaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
@Tag(
        name = "Contas",
        description = "Abertura de contas e realização de operações financeiras."
)
public class ContaController {

    private final ContaService contaService;
    private final ContaCorrenteService contaCorrenteService;
    private final ContaPoupancaService contaPoupancaService;

    @Operation(summary = "Lista todas as contas")
    @GetMapping
    public ResponseEntity<?> listarContas() {
        return ResponseEntity.ok(contaService.listarContas());
    }

    @Operation(summary = "Busca uma conta pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @Operation(summary = "Exclui uma conta")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Abre uma conta corrente ou poupança")
    @PostMapping("/{tipo}")
    public ResponseEntity<?> criarConta(
            @PathVariable String tipo,
            @Valid @RequestBody ContaRequest dto
    ) {
        switch (TipoConta.fromString(tipo)) {
            case CORRENTE:
                if (dto.getLimite() == null) {
                    throw new IllegalArgumentException(
                            "O limite é obrigatório para conta corrente."
                    );
                }

                ContaCorrente contaCorrente = new ContaCorrente();
                contaCorrente.setNumero(dto.getNumero());
                contaCorrente.setSaldo(BigDecimal.ZERO.setScale(2));
                contaCorrente.setLimite(dto.getLimite());
                contaCorrente.setCorrentista(
                        contaCorrenteService.buscarCorrentista(dto.getCorrentistaId())
                );

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(contaCorrenteService.salvar(contaCorrente));

            case POUPANCA:
                ContaPoupanca contaPoupanca = new ContaPoupanca();
                contaPoupanca.setNumero(dto.getNumero());
                contaPoupanca.setSaldo(BigDecimal.ZERO.setScale(2));
                contaPoupanca.setCorrentista(
                        contaPoupancaService.buscarCorrentista(dto.getCorrentistaId())
                );

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(contaPoupancaService.salvar(contaPoupanca));

            default:
                throw new IllegalArgumentException("Tipo de conta inválido.");
        }
    }

    @Operation(summary = "Realiza um depósito")
    @PostMapping("/{tipo}/{id}/depositar")
    public ResponseEntity<?> depositar(
            @PathVariable String tipo,
            @PathVariable Long id,
            @Valid @RequestBody ValorRequest body
    ) {
        switch (TipoConta.fromString(tipo)) {
            case CORRENTE:
                return ResponseEntity.ok(
                        contaCorrenteService.depositar(id, body.getValor())
                );
            case POUPANCA:
                return ResponseEntity.ok(
                        contaPoupancaService.depositar(id, body.getValor())
                );
            default:
                throw new IllegalArgumentException("Tipo de conta inválido.");
        }
    }

    @Operation(summary = "Realiza um saque")
    @PostMapping("/{tipo}/{id}/sacar")
    public ResponseEntity<?> sacar(
            @PathVariable String tipo,
            @PathVariable Long id,
            @Valid @RequestBody ValorRequest body
    ) {
        switch (TipoConta.fromString(tipo)) {
            case CORRENTE:
                return ResponseEntity.ok(
                        contaCorrenteService.sacar(id, body.getValor())
                );
            case POUPANCA:
                return ResponseEntity.ok(
                        contaPoupancaService.sacar(id, body.getValor())
                );
            default:
                throw new IllegalArgumentException("Tipo de conta inválido.");
        }
    }

    @Operation(summary = "Aplica juros ou rendimento à conta")
    @PostMapping("/{tipo}/{id}/taxa")
    public ResponseEntity<?> aplicarTaxa(
            @PathVariable String tipo,
            @PathVariable Long id,
            @Valid @RequestBody TaxaRequest body
    ) {
        switch (TipoConta.fromString(tipo)) {
            case CORRENTE:
                return ResponseEntity.ok(
                        contaCorrenteService.aplicarJuros(id, body.getTaxa())
                );
            case POUPANCA:
                return ResponseEntity.ok(
                        contaPoupancaService.aplicarRendimento(id, body.getTaxa())
                );
            default:
                throw new IllegalArgumentException("Tipo de conta inválido.");
        }
    }
}
