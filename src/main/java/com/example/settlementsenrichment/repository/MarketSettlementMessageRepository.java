package com.example.settlementsenrichment.repository;

import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MarketSettlementMessageRepository extends JpaRepository<MarketSettlementMessage, UUID> {
}
