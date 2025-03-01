package com.contas_a_pagar.domain.repository;

import com.contas_a_pagar.domain.entity.Conta;
import com.contas_a_pagar.domain.enums.Situacao;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

public class ContaSpec {

    public static Specification<Conta> porContasAPagar(String descricao, LocalDate dataInicialVencimento, LocalDate datafinalVencimento) {
        return porDescricao(descricao).and(porSituacaoDiferenteDe(Situacao.Pago)).and(aposVencimento(dataInicialVencimento)).and(antesVencimento(datafinalVencimento));
    }

    public static Specification<Conta> porValorTotalPeriodo(LocalDate dataInicialPagamento, LocalDate dataFinalPagamento) {
        return porSituacao(Situacao.Pago).and(antesDataPagamento(dataFinalPagamento)).and(aposDataPagamento(dataInicialPagamento));
    }

    private static Specification<Conta> porDescricao(String descricao) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(descricao))
                return criteriaBuilder.like(root.get("descricao"), "%" + descricao + "%");
            return null;
        };
    }

    private static Specification<Conta> porSituacao(Situacao situacao) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(situacao))
                return criteriaBuilder.equal(root.get("situacao"), situacao);
            return null;
        };
    }

    private static Specification<Conta> porSituacaoDiferenteDe(Situacao situacao) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(situacao))
                return criteriaBuilder.notEqual(root.get("situacao"), situacao);
            return null;
        };
    }

    private static Specification<Conta> antesVencimento(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(date))
                return criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimento"), date);
            return null;
        };
    }

    private static Specification<Conta> aposVencimento(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(date))
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimento"), date);
            return null;
        };
    }

    private static Specification<Conta> antesDataPagamento(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(date))
                return criteriaBuilder.lessThanOrEqualTo(root.get("dataPagamento"), date);
            return null;
        };
    }

    private static Specification<Conta> aposDataPagamento(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(date))
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dataPagamento"), date);
            return null;
        };
    }
}
