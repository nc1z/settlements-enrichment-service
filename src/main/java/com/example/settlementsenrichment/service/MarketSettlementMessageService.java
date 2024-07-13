package com.example.settlementsenrichment.service;

import com.example.settlementsenrichment.dto.TradeRequest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.Party;
import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import com.example.settlementsenrichment.repository.MarketSettlementMessageRepository;
import com.example.settlementsenrichment.repository.StandardSettlementInstructionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MarketSettlementMessageService {
    private final MarketSettlementMessageRepository marketSettlementMessageRepository;
    private final StandardSettlementInstructionRepository standardSettlementInstructionRepository;

    public MarketSettlementMessageService(MarketSettlementMessageRepository marketSettlementMessageRepository, StandardSettlementInstructionRepository standardSettlementInstructionRepository) {
        this.marketSettlementMessageRepository = marketSettlementMessageRepository;
        this.standardSettlementInstructionRepository = standardSettlementInstructionRepository;
    }

    public MarketSettlementMessage createMarketSettlementMessage(TradeRequest tradeRequest) throws Exception {
        StandardSettlementInstruction ssi = standardSettlementInstructionRepository.findByCode(tradeRequest.code())
                .orElseThrow(() -> new Exception("SSI Code not found: " + tradeRequest.code()));

        MarketSettlementMessage message = MarketSettlementMessage.builder()
                .tradeId(tradeRequest.tradeId())
                .messageId(UUID.randomUUID())
                .amount(tradeRequest.amount())
                .valueDate(tradeRequest.valueDate())
                .currency(tradeRequest.currency())
                .payerParty(new Party(ssi.getPayerAccountNumber(), ssi.getPayerBank()))
                .receiverParty(new Party(ssi.getReceiverAccountNumber(), ssi.getReceiverBank()))
                .supportingInformation(ssi.getSupportingInformation())
                .build();

        return marketSettlementMessageRepository.save(message);
    }
}
