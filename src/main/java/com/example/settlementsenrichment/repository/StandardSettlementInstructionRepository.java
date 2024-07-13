package com.example.settlementsenrichment.repository;

import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StandardSettlementInstructionRepository extends JpaRepository<StandardSettlementInstruction, Long> {
    Optional<StandardSettlementInstruction> findByCode(String code);
}
