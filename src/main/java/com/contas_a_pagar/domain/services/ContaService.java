package com.contas_a_pagar.domain.services;

import com.contas_a_pagar.application.dto.conta.ContaResponse;
import com.contas_a_pagar.application.dto.conta.CreateRequest;
import com.contas_a_pagar.application.dto.conta.UpdateRequest;
import com.contas_a_pagar.domain.entity.Conta;
import com.contas_a_pagar.domain.enums.Situacao;
import com.contas_a_pagar.domain.repository.ContaRepository;
import com.contas_a_pagar.domain.repository.ContaSpec;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public Page<ContaResponse>  getContasAPagar(int pagina, int items, String descricao, LocalDate dataVencimentoInicial, LocalDate dataVencimentoFinal) {
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
}
