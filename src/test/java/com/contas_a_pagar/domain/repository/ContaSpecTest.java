package com.contas_a_pagar.domain.repository;

import com.contas_a_pagar.TestContasAPagarApplication;
import com.contas_a_pagar.domain.entity.Conta;
import com.contas_a_pagar.domain.enums.Situacao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContasAPagarApplication.class)
@Transactional
class ContaSpecTest {

    @Autowired
    private ContaRepository contaRepository;
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
        contaRepository.saveAll(contas);
    }

    @AfterEach
    void clearDatabase() {
        contaRepository.deleteAll();
    }

    @Test
    void porContasAPagarWhenNullFiltersMustReturnDebitPendingList() {
        List<Conta> result = contaRepository.findAll(ContaSpec.porContasAPagar(null, null, null));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(6);
    }

    @Test
    void porContasAPagarWhenFilteredByDescricaoMustReturnFilteredDebitPendingList() {
        String descricao = "Agua";
        List<Conta> expected = Arrays.asList(contas.get(1), contas.get(3));

        List<Conta> result = contaRepository.findAll(ContaSpec.porContasAPagar(descricao, null, null));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porContasAPagarWhenFilteredByDataInicialVencimentoMustReturnFilteredDebitPendingList() {
        LocalDate dataInicialVencimento = LocalDate.of(2025, 7, 1);
        List<Conta> expected = Arrays.asList(contas.get(5), contas.get(6));

        List<Conta> result = contaRepository.findAll(ContaSpec.porContasAPagar(null, dataInicialVencimento, null));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porContasAPagarWhenFilteredByDataFinalVencimentoMustReturnFilteredDebitPendingList() {
        LocalDate dataFinalVencimento = LocalDate.of(2025, 4, 16);
        List<Conta> expected = Arrays.asList(contas.get(1), contas.get(2));

        List<Conta> result = contaRepository.findAll(ContaSpec.porContasAPagar(null, null, dataFinalVencimento));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porContasAPagarWhenFilteredByDescriptionDataInicialVencimentoAndDataFinalVencimentoMustReturnFilteredDebitPendingList() {
        String descricao = "Aluguel";
        LocalDate dataInicialVencimento = LocalDate.of(2025, 1, 1);
        LocalDate dataFinalVencimento = LocalDate.of(2025, 6, 28);
        List<Conta> expected = Arrays.asList(contas.get(2), contas.get(4));

        List<Conta> result = contaRepository.findAll(ContaSpec.porContasAPagar(descricao, dataInicialVencimento, dataFinalVencimento));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porValorTotalPeriodoWithOnlyInitialPaymentDateReturnsDebtAccountsFromStartDate() {
        LocalDate dataInicialPagamento = LocalDate.of(2025, 5, 1);
        List<Conta> expected = Arrays.asList(contas.get(7), contas.get(8));

        List<Conta> result = contaRepository.findAll(ContaSpec.porValorTotalPeriodo(dataInicialPagamento, null));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porValorTotalPeriodoWithOnlyFinalPaymentDateReturnsDebtAccountsUntilEndDate() {
        LocalDate dataFinalPagamento = LocalDate.of(2025, 4, 30);
        List<Conta> expected = Arrays.asList(contas.get(0));

        List<Conta> result = contaRepository.findAll(ContaSpec.porValorTotalPeriodo(null, dataFinalPagamento));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porValorTotalPeriodoWithDateFilterReturnsDebtAccounts() {
        LocalDate dataInicialPagamento = LocalDate.of(2025, 2, 1);
        LocalDate dataFinalPagamento = LocalDate.of(2025, 6, 1);
        List<Conta> expected = Arrays.asList(contas.get(0));

        List<Conta> result = contaRepository.findAll(ContaSpec.porValorTotalPeriodo(dataInicialPagamento, dataFinalPagamento));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porValorTotalPeriodoWithNonMatchingDateRangeReturnsNoDebtAccounts() {
        LocalDate dataInicialPagamento = LocalDate.of(2025, 6, 1);
        LocalDate dataFinalPagamento = LocalDate.of(2025, 7, 1);
        List<Conta> expected = new ArrayList<>();

        List<Conta> result = contaRepository.findAll(ContaSpec.porValorTotalPeriodo(dataInicialPagamento, dataFinalPagamento));

        assertThat(result).isEmpty();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porValorTotalPeriodoWithFullDateRangeReturnsAllDebtAccounts() {
        LocalDate dataInicialPagamento = LocalDate.of(2025, 1, 1);
        LocalDate dataFinalPagamento = LocalDate.of(2025, 12, 31);
        List<Conta> expected = Arrays.asList(contas.get(0), contas.get(7), contas.get(8));

        List<Conta> result = contaRepository.findAll(ContaSpec.porValorTotalPeriodo(dataInicialPagamento, dataFinalPagamento));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porValorTotalPeriodoWithDateRangeOutsideDebtAccountsReturnsNoDebtAccounts() {
        LocalDate dataInicialPagamento = LocalDate.of(2025, 5, 1);
        LocalDate dataFinalPagamento = LocalDate.of(2025, 6, 1);
        List<Conta> expected = new ArrayList<>();

        List<Conta> result = contaRepository.findAll(ContaSpec.porValorTotalPeriodo(dataInicialPagamento, dataFinalPagamento));

        assertThat(result).isEmpty();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void porValorTotalPeriodoWithEqualStartAndEndDateReturnsDebtAccountsOnExactDate() {
        LocalDate dataInicialPagamento = LocalDate.of(2025, 10, 5);
        LocalDate dataFinalPagamento = LocalDate.of(2025, 10, 5);
        List<Conta> expected = Arrays.asList(contas.get(8));

        List<Conta> result = contaRepository.findAll(ContaSpec.porValorTotalPeriodo(dataInicialPagamento, dataFinalPagamento));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expected);
    }
}