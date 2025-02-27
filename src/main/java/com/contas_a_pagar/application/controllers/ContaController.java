package com.contas_a_pagar.application.controllers;

import com.contas_a_pagar.application.dto.conta.ContaResponse;
import com.contas_a_pagar.application.dto.conta.CreateRequest;
import com.contas_a_pagar.application.dto.conta.UpdateRequest;
import com.contas_a_pagar.domain.enums.Situacao;
import com.contas_a_pagar.domain.services.ContaService;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<Page<ContaResponse>> getAll(@RequestParam int pagina, @RequestParam int itens) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAll(pagina, itens));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> get(@PathVariable("id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.get(id));
    }

    @GetMapping("/contas-a-pagar")
    public ResponseEntity<Page<ContaResponse>> getContasAPagar(@RequestParam int pagina, @RequestParam int itens,
                                                               @RequestParam(value = "descricao", required = false) String descricao,
                                                               @RequestParam(value = "dataVencimentoInicial", required = false) LocalDate dataVencimentoInicial,
                                                               @RequestParam(value = "dataVencimentoFinal", required = false) LocalDate dataVencimentoFinal) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getContasAPagar(pagina, itens, descricao, dataVencimentoInicial, dataVencimentoFinal));
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

    @GetMapping("/baixar-modelo-csv")
    public ResponseEntity<Resource> getModeloXml() {
        try {
            Resource resource = service.getModeloCsv();
            if (!resource.exists())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=contas.csv");

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/importar-por-csv",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importarPorCsv(@RequestParam("file") MultipartFile file) {
        try {
            service.importarPorCsv(file);
            return ResponseEntity.status(HttpStatus.OK).body("Dados importados com sucesso!");
        } catch (IOException | CsvValidationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo CSV");
        }
    }
}


