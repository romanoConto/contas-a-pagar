package com.contas_a_pagar.application.controllers;

import com.contas_a_pagar.application.dto.conta.ContaResponse;
import com.contas_a_pagar.application.dto.conta.CreateRequest;
import com.contas_a_pagar.application.dto.conta.UpdateRequest;
import com.contas_a_pagar.domain.enums.Situacao;
import com.contas_a_pagar.domain.services.ContaService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ContaResponse>> getAll(@RequestParam int pagina, @RequestParam int items) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAll(pagina, items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> get(@PathVariable("id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.get(id));
    }

    @GetMapping("/contas-a-pagar")
    public ResponseEntity<Page<ContaResponse>> getContasAPagar(@RequestParam int pagina, @RequestParam int items,
                                                               @RequestParam(value = "descricao", required = false) String descricao,
                                                               @RequestParam(value = "dataVencimentoInicial", required = false) LocalDate dataVencimentoInicial,
                                                               @RequestParam(value = "dataVencimentoFinal", required = false) LocalDate dataVencimentoFinal) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getContasAPagar(pagina, items, descricao, dataVencimentoInicial, dataVencimentoFinal));
    }

    @GetMapping("/valor-total-periodo")
    public ResponseEntity<BigDecimal> getValorTotalPeriodo(@RequestParam(value = "dataPagamentoInicial", required = false) LocalDate dataPagamentoInicial,
                                                           @RequestParam(value = "dataPagamentoFinal", required = false) LocalDate dataPagamentoFinal) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getValorTotalPeriodo(dataPagamentoInicial, dataPagamentoFinal));
    }

    @PostMapping
    public ResponseEntity<Void> post(@RequestBody CreateRequest conta) {
        service.post(conta);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> put(@PathVariable("id") Integer id, @RequestBody UpdateRequest body) {
        service.put(id, body);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/atualizar-situacao")
    public ResponseEntity<Void> atualizarSituacao(@PathVariable("id") Integer id, @RequestBody Situacao body) {
        service.atualizarSituacao(id, body);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


