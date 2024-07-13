package com.example.settlementsenrichment.service;

import com.example.settlementsenrichment.dto.TradeRequest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.Party;
import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import com.example.settlementsenrichment.exception.DuplicateResourceException;
import com.example.settlementsenrichment.exception.ResourceNotFoundException;
import com.example.settlementsenrichment.repository.MarketSettlementMessageRepository;
import com.example.settlementsenrichment.repository.StandardSettlementInstructionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Service
public class MarketSettlementMessageService {
    private static final Logger logger = LoggerFactory.getLogger(MarketSettlementMessageService.class);

    private final MarketSettlementMessageRepository marketSettlementMessageRepository;
    private final StandardSettlementInstructionRepository standardSettlementInstructionRepository;

    public MarketSettlementMessageService(MarketSettlementMessageRepository marketSettlementMessageRepository, StandardSettlementInstructionRepository standardSettlementInstructionRepository) {
        this.marketSettlementMessageRepository = marketSettlementMessageRepository;
        this.standardSettlementInstructionRepository = standardSettlementInstructionRepository;
    }

    @Transactional
    public MarketSettlementMessage createMarketSettlementMessage(TradeRequest tradeRequest) {

        if (findOptionalByTradeId(tradeRequest.tradeId()).isPresent()) {
            logger.error("Duplicate Trade ID found: {}", tradeRequest.tradeId());
            throw new DuplicateResourceException("Market Settlement Message", "TradeId", tradeRequest.tradeId());
        }

        StandardSettlementInstruction ssi = standardSettlementInstructionRepository.findByCode(tradeRequest.code())
                .orElseThrow(() -> {
                    logger.error("SSI not found for code: {}", tradeRequest.code());
                    return new ResourceNotFoundException("SSI", "code", tradeRequest.code());
                });

        MarketSettlementMessage message = MarketSettlementMessage.builder()
                .tradeId(tradeRequest.tradeId())
                .messageId(UUID.randomUUID())
                .amount(tradeRequest.amount().setScale(2, RoundingMode.HALF_UP))
                .valueDate(tradeRequest.valueDate())
                .currency(tradeRequest.currency())
                .payerParty(new Party(ssi.getPayerAccountNumber(), ssi.getPayerBank()))
                .receiverParty(new Party(ssi.getReceiverAccountNumber(), ssi.getReceiverBank()))
                .supportingInformation(transformSupportingInformation(ssi.getSupportingInformation()))
                .build();

        MarketSettlementMessage savedMessage = marketSettlementMessageRepository.save(message);
        logger.info("Market Settlement Message created with Message ID: {}", savedMessage.getMessageId());

        return savedMessage;
    }

    @Transactional(readOnly = true)
    public MarketSettlementMessage findById(String tradeId) {
        return marketSettlementMessageRepository.findByTradeId(tradeId)
                .orElseThrow(() -> {
                    logger.error("Market Settlement Message not found for Trade ID: {}", tradeId);
                    return new ResourceNotFoundException("MarketSettlementMessage", "tradeId", tradeId);
                });
    }

    @Transactional(readOnly = true)
    public Optional<MarketSettlementMessage> findOptionalByTradeId(String tradeId) {
        return marketSettlementMessageRepository.findByTradeId(tradeId);
    }

    private String transformSupportingInformation(String supportingInformation) {
        if (supportingInformation == null || supportingInformation.isEmpty()) {
            return null;
        }
        return "/" + supportingInformation.replace(":", "/");
    }
}
