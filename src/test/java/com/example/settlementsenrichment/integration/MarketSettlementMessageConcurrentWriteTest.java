package com.example.settlementsenrichment.integration;

import com.example.settlementsenrichment.AbstractIntegrationTest;
import com.example.settlementsenrichment.dto.TradeRequest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import com.example.settlementsenrichment.repository.MarketSettlementMessageRepository;
import com.example.settlementsenrichment.repository.StandardSettlementInstructionRepository;
import com.example.settlementsenrichment.service.MarketSettlementMessageService;
import org.hibernate.exception.LockAcquisitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class MarketSettlementMessageConcurrentWriteTest extends AbstractIntegrationTest {

    @Autowired
    private StandardSettlementInstructionRepository standardSettlementInstructionRepository;

    @Autowired
    private MarketSettlementMessageRepository marketSettlementMessageRepository;

    @Autowired
    private MarketSettlementMessageService marketSettlementMessageService;

    @BeforeEach
    void setUp() {
        standardSettlementInstructionRepository.deleteAll();

        StandardSettlementInstruction ssi = StandardSettlementInstruction.builder()
                .code("OCBC_DBS_1")
                .payerAccountNumber("438421")
                .payerBank("OCBCSGSGXXX")
                .receiverAccountNumber("05461368")
                .receiverBank("DBSSGB2LXXX")
                .supportingInformation("BNF:FFC-4697132")
                .build();

        standardSettlementInstructionRepository.save(ssi);
    }

    @Test
    void testConcurrentWrites() throws ExecutionException {
        TradeRequest tradeRequest1 = new TradeRequest("16846548", "OCBC_DBS_1", new BigDecimal("12894.65"), "USD", "20022020");
        TradeRequest tradeRequest2 = new TradeRequest("16846548", "OCBC_DBS_1", new BigDecimal("12894.65"), "USD", "20022020");

        CompletableFuture<MarketSettlementMessage> future1 = CompletableFuture.supplyAsync(() -> marketSettlementMessageService.createMarketSettlementMessage(tradeRequest1));
        CompletableFuture<MarketSettlementMessage> future2 = CompletableFuture.supplyAsync(() -> marketSettlementMessageService.createMarketSettlementMessage(tradeRequest2));

        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            CompletableFuture.allOf(future1, future2).get();
        });

        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertNotNull(cause.getCause());
        assertEquals(LockAcquisitionException.class, cause.getCause().getClass(), "Expected LockAcquisitionException");

        List<MarketSettlementMessage> messages = marketSettlementMessageRepository.findAll();
        assertEquals(1, messages.size(), "Only one MarketSettlementMessage should exist with the given tradeId");
        assertEquals("16846548", messages.get(0).getTradeId());
    }
}
