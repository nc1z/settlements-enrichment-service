package com.example.settlementsenrichment.repository;

import com.example.settlementsenrichment.AbstractDataJpaTest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.Party;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MarketSettlementMessageRepositoryTest extends AbstractDataJpaTest {

    @Autowired
    private MarketSettlementMessageRepository repository;

    @Test
    void shouldSaveAndFindMarketSettlementMessage() {
        MarketSettlementMessage message = MarketSettlementMessage.builder()
                .tradeId("16846549")
                .amount(new BigDecimal("5450.00"))
                .valueDate("17032020")
                .currency("USD")
                .payerParty(new Party("185586", "DBSSSGSGXXX"))
                .receiverParty(new Party("1868422", "SCBLAU2SXXX"))
                .supportingInformation("/RFB/Test payment")
                .build();

        MarketSettlementMessage savedMessage = repository.save(message);

        assertNotNull(savedMessage);
        assertNotNull(savedMessage.getMessageId());

        MarketSettlementMessage foundMessage = repository.findById(savedMessage.getMessageId()).orElse(null);
        assertNotNull(foundMessage);
        assertEquals("16846549", foundMessage.getTradeId());
        assertEquals(new BigDecimal("5450.00"), foundMessage.getAmount());
        assertEquals("17032020", foundMessage.getValueDate());
        assertEquals("USD", foundMessage.getCurrency());
        assertEquals("185586", foundMessage.getPayerParty().getAccountNumber());
        assertEquals("DBSSSGSGXXX", foundMessage.getPayerParty().getBankCode());
        assertEquals("1868422", foundMessage.getReceiverParty().getAccountNumber());
        assertEquals("SCBLAU2SXXX", foundMessage.getReceiverParty().getBankCode());
        assertEquals("/RFB/Test payment", foundMessage.getSupportingInformation());
    }
}