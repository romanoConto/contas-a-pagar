package com.contas_a_pagar.domain.services;

import com.contas_a_pagar.application.dto.conta.ContaResponse;
import com.contas_a_pagar.application.dto.conta.CreateRequest;
import com.contas_a_pagar.application.dto.conta.UpdateRequest;
import com.contas_a_pagar.domain.entity.Conta;
import com.contas_a_pagar.domain.enums.Situacao;
import com.contas_a_pagar.domain.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ContaService contaService;
    private List<Conta> contas;

    @BeforeEach
    void setUp() {
        contas = Arrays.asList(
                new Conta(LocalDate.of(2025, 3, 15), LocalDate.of(2025, 3, 5), new BigDecimal(191), "luz 1", Situacao.Pago),
                new Conta(LocalDate.of(2025, 3, 15), null, new BigDecimal(61), "Agua 2", Situacao.Atrasado),
                new Conta(LocalDate.of(2025, 4, 15), null, new BigDecimal(1201), "Aluguel 3", Situacao.Atrasado),
                new Conta(LocalDate.of(2025, 5, 15), null, new BigDecimal(62), "Agua 4", Situacao.Atrasado),
                new Conta(LocalDate.of(2025, 6, 15), null, new BigDecimal(1202), "Aluguel 5", Situacao.Pendente),
                new Conta(LocalDate.of(2025, 7, 15), null, new BigDecimal(1203), "Aluguel 6", Situacao.Pendente),
                new Conta(LocalDate.of(2025, 8, 15), null, new BigDecimal(192), "luz 7", Situacao.Pendente),
                new Conta(LocalDate.of(2025, 9, 15), LocalDate.of(2025, 9, 5), new BigDecimal(193), "luz 8", Situacao.Pago),
                new Conta(LocalDate.of(2025, 10, 15), LocalDate.of(2025, 10, 5), new BigDecimal(63), "Agua 9", Situacao.Pago)
        );
    }

    @Test
    void getAllShouldReturnPageOfContaResponse() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Conta> pageConta = new PageImpl<>(contas, pageRequest, contas.size());

        when(contaRepository.findAll(pageRequest)).thenReturn(pageConta);
        when(modelMapper.map(any(), eq(ContaResponse.class))).thenAnswer(invocation -> {
            Conta conta = invocation.getArgument(0);
            ContaResponse contaResponse = new ContaResponse();
            contaResponse.setId(conta.getId());
            contaResponse.setDescricao(conta.getDescricao());
            contaResponse.setValor(conta.getValor());
            contaResponse.setSituacao(conta.getSituacao());
            contaResponse.setDataVencimento(conta.getDataVencimento());
            contaResponse.setDataPagamento(conta.getDataPagamento());
            return contaResponse;
        });

        // Act
        Page<ContaResponse> result = contaService.getAll(0, 10);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(contas.size());
        assertThat(result.getContent().get(0).getDescricao()).isEqualTo(contas.get(0).getDescricao());
        assertThat(result.getContent().get(1).getDescricao()).isEqualTo(contas.get(1).getDescricao());
        assertThat(result.getContent().get(2).getDescricao()).isEqualTo(contas.get(2).getDescricao());
        assertThat(result.getContent().get(3).getDescricao()).isEqualTo(contas.get(3).getDescricao());
        assertThat(result.getContent().get(4).getDescricao()).isEqualTo(contas.get(4).getDescricao());
        assertThat(result.getContent().get(5).getDescricao()).isEqualTo(contas.get(5).getDescricao());
        assertThat(result.getContent().get(6).getDescricao()).isEqualTo(contas.get(6).getDescricao());
        assertThat(result.getContent().get(7).getDescricao()).isEqualTo(contas.get(7).getDescricao());
        assertThat(result.getContent().get(8).getDescricao()).isEqualTo(contas.get(8).getDescricao());
    }

    @Test
    void getShouldReturnContaResponse() {
        // Arrange
        ContaResponse contaResponse = new ContaResponse();
        contaResponse.setId(contas.get(0).getId());
        contaResponse.setDescricao(contas.get(0).getDescricao());
        contaResponse.setValor(contas.get(0).getValor());
        contaResponse.setSituacao(contas.get(0).getSituacao());
        contaResponse.setDataVencimento(contas.get(0).getDataVencimento());
        contaResponse.setDataPagamento(contas.get(0).getDataPagamento());

        when(contaRepository.findById(contas.get(0).getId())).thenReturn(Optional.of(contas.get(0)));
        when(modelMapper.map(any(), any())).thenReturn(contaResponse);

        // Act
        ContaResponse result = contaService.get(contas.get(0).getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(contaResponse);
    }

    @Test
    void getContasAPagarShouldReturnFilteredResults() {
        // Arrange
        int pagina = 0;
        int itens = 10;
        String descricao = "Agua";
        LocalDate dataVencimentoInicial = LocalDate.of(2025, 4, 15);
        LocalDate dataVencimentoFinal = LocalDate.of(2025, 6, 15);

        List<Conta> contasAPagar = contas.stream().filter(conta -> conta.getSituacao() != Situacao.Pago && conta.getDescricao().contains(descricao)
                && conta.getDataVencimento().isBefore(dataVencimentoFinal) && conta.getDataVencimento().isAfter(dataVencimentoInicial)).collect(Collectors.toList());
        Page<Conta> pagedResponse = new PageImpl(contasAPagar);
        when(contaRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(pagedResponse);
        when(modelMapper.map(any(), eq(ContaResponse.class))).thenAnswer(invocation -> {
            Conta conta = invocation.getArgument(0);
            ContaResponse contaResponse = new ContaResponse();
            contaResponse.setId(conta.getId());
            contaResponse.setDescricao(conta.getDescricao());
            contaResponse.setValor(conta.getValor());
            contaResponse.setSituacao(conta.getSituacao());
            contaResponse.setDataVencimento(conta.getDataVencimento());
            contaResponse.setDataPagamento(conta.getDataPagamento());
            return contaResponse;
        });

        // Act
        Page<ContaResponse> result = contaService.getContasAPagar(pagina, itens, descricao, dataVencimentoInicial, dataVencimentoFinal);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getDescricao()).isEqualTo(contas.get(3).getDescricao());
        assertThat(result.getContent().get(0).getDataPagamento()).isEqualTo(contas.get(3).getDataPagamento());
        assertThat(result.getContent().get(0).getDataVencimento()).isEqualTo(contas.get(3).getDataVencimento());
        assertThat(result.getContent().get(0).getValor()).isEqualTo(contas.get(3).getValor());
        assertThat(result.getContent().get(0).getSituacao()).isEqualTo(contas.get(3).getSituacao());
    }

    @Test
    void getValorTotalPeriodoShouldReturnCorrectTotal() {
        LocalDate dataPagamentoInicial = LocalDate.of(2025, 8, 15);
        LocalDate dataPagamentoFinal = LocalDate.of(2025, 12, 15);
        List<Conta> contasAPagar = contas.stream().filter(conta -> conta.getSituacao() == Situacao.Pago
                && conta.getDataPagamento().isBefore(dataPagamentoFinal) && conta.getDataPagamento().isAfter(dataPagamentoInicial)).collect(Collectors.toList());
        BigDecimal valorTotalPeriodo = contasAPagar.stream().map(conta -> conta.getValor()).reduce(BigDecimal.ZERO, BigDecimal::add);
        when(contaRepository.findAll(any(Specification.class))).thenReturn(contasAPagar);

        // Act
        BigDecimal result = contaService.getValorTotalPeriodo(dataPagamentoInicial, dataPagamentoFinal);

        // Assert
        assertThat(result).isEqualTo(valorTotalPeriodo);
    }

    @Test
    void postShouldSaveConta() {
        // Arrange
        CreateRequest createRequest = new CreateRequest();
        createRequest.setDescricao("Conta de Exemplo");
        createRequest.setValor(new BigDecimal(100.0));
        createRequest.setSituacao(Situacao.Pendente);
        createRequest.setDataVencimento(LocalDate.of(2025, 3, 1));

        Conta conta = new Conta();
        conta.setDescricao(createRequest.getDescricao());
        conta.setValor(createRequest.getValor());
        conta.setSituacao(createRequest.getSituacao());
        conta.setDataVencimento(createRequest.getDataVencimento());

        when(modelMapper.map(createRequest, Conta.class)).thenReturn(conta);

        // Act
        contaService.post(createRequest);

        // Assert
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    void putShouldNotUpdateIfContaDoesNotExist() {
        // Arrange
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setDescricao("Conta Atualizada");
        updateRequest.setValor(new BigDecimal(100.0));

        when(contaRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        contaService.put(1, updateRequest);

        // Assert
        verify(contaRepository, times(0)).save(any());
    }

    @Test
    void atualizarSituacaoShouldUpdateSituacaoToPago() {
        // Arrange
        when(contaRepository.getReferenceById(contas.get(5).getId())).thenReturn(contas.get(5));

        // Act
        contaService.atualizarSituacao(contas.get(5).getId(), Situacao.Pago);

        // Assert
        assertThat(contas.get(5).getSituacao()).isEqualTo(Situacao.Pago);
        assertThat(contas.get(5).getDataPagamento()).isNotNull();
    }
}
