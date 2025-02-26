package com.contas_a_pagar.domain.repository;

import com.contas_a_pagar.domain.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContaRepository extends JpaRepository<Conta, Integer>, JpaSpecificationExecutor<Conta> {
}
