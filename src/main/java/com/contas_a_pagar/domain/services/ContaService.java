package com.contas_a_pagar.domain.services;

import com.contas_a_pagar.application.dto.conta.ContaResponse;
import com.contas_a_pagar.application.dto.conta.CreateRequest;
import com.contas_a_pagar.application.dto.conta.UpdateRequest;
import com.contas_a_pagar.domain.entity.Conta;
import com.contas_a_pagar.domain.enums.ColunaCsv;
import com.contas_a_pagar.domain.enums.Situacao;
import com.contas_a_pagar.domain.repository.ContaRepository;
import com.contas_a_pagar.domain.repository.ContaSpec;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {

    @Autowired
    private ContaRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ContaResponse> getAll(int pagina, int items) {
        Page<Conta> contas = repository.findAll(PageRequest.of(pagina, items));
        return contas.map(x -> modelMapper.map(x, ContaResponse.class));
    }

    public ContaResponse get(Integer id) {
        Optional<Conta> conta = repository.findById(id);
        if (conta.isEmpty())
            return null;
        return modelMapper.map(conta, ContaResponse.class);
    }

    public Page<ContaResponse> getContasAPagar(int pagina, int items, String descricao, LocalDate dataVencimentoInicial, LocalDate dataVencimentoFinal) {
        Page<Conta> contas = repository.findAll(ContaSpec.porContasAPagar(descricao, dataVencimentoInicial, dataVencimentoFinal), PageRequest.of(pagina, items));
        return contas.map(x -> modelMapper.map(x, ContaResponse.class));
    }

    public BigDecimal getValorTotalPeriodo(LocalDate dataPagamentoInicial, LocalDate dataPagamentoFinal) {
        List<Conta> contas = repository.findAll(ContaSpec.porValorTotalPeriodo(dataPagamentoInicial, dataPagamentoFinal));
        if (contas.isEmpty())
            return new BigDecimal(0);
        return contas.stream().map(Conta::getValor).reduce(BigDecimal::add).get();
    }

    public void post(CreateRequest contaDTO) {
        Conta conta = modelMapper.map(contaDTO, Conta.class);
        repository.save(conta);
    }

    public void put(Integer id, UpdateRequest contaDTO) {
        Optional<Conta> conta = repository.findById(id);
        if (conta.isEmpty())
            return;
        modelMapper.map(contaDTO, conta.get());
        repository.save(conta.get());
    }

    public void atualizarSituacao(Integer id, Situacao situacao) {
        Conta conta = repository.getReferenceById(id);
        if (situacao == Situacao.Pago && conta.getSituacao() != Situacao.Pago)
            conta.setDataPagamento(LocalDate.now());
        conta.setSituacao(situacao);
        repository.save(conta);
    }

    public Resource getModeloCsv() {
        return new ClassPathResource("modeloImportacao.csv");
    }

    public void importarPorCsv(MultipartFile file) throws IOException, CsvValidationException {
        List<Conta> contas = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] linha;
            reader.readNext();

            while ((linha = reader.readNext()) != null) {
                Conta conta = new Conta();

                conta.setDataVencimento(LocalDate.parse(linha[ColunaCsv.Data_Vencimento.ordinal()]));
                conta.setDataPagamento(linha[ColunaCsv.Data_Pagamento.ordinal()].isEmpty() ? null : LocalDate.parse(linha[ColunaCsv.Data_Pagamento.ordinal()]));
                conta.setValor(new BigDecimal(linha[ColunaCsv.Valor.ordinal()]));
                conta.setDescricao(linha[ColunaCsv.Descricao.ordinal()]);
                conta.setSituacao(Situacao.valueOf(linha[ColunaCsv.Situacao.ordinal()]));
                contas.add(conta);
            }
        }

        repository.saveAll(contas);
    }
}
