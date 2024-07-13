package com.example.settlementsenrichment.integration;

import com.example.settlementsenrichment.AbstractIntegrationTest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import com.example.settlementsenrichment.repository.StandardSettlementInstructionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
public class MarketSettlementMessageControllerIntTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StandardSettlementInstructionRepository standardSettlementInstructionRepository;

    @BeforeEach
    void setUp() {
        standardSettlementInstructionRepository.save(StandardSettlementInstruction.builder()
                .code("OCBC_DBS_1")
                .payerAccountNumber("438421")
                .payerBank("OCBCSGSGXXX")
                .receiverAccountNumber("05461368")
                .receiverBank("DBSSGB2LXXX")
                .supportingInformation("BNF:FFC-4697132")
                .build());
    }

    @Test
    void shouldCreateMarketSettlementMessage() throws Exception {
        String tradeRequestJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MarketSettlementMessage responseMessage = objectMapper.readValue(result.getResponse().getContentAsString(), MarketSettlementMessage.class);
                    assertNotNull(responseMessage.getMessageId());
                    assertEquals("16846548", responseMessage.getTradeId());
                    assertEquals(new BigDecimal("12894.65").setScale(2, RoundingMode.UNNECESSARY), responseMessage.getAmount());
                    assertEquals("20022020", responseMessage.getValueDate());
                    assertEquals("USD", responseMessage.getCurrency());
                    assertEquals("438421", responseMessage.getPayerParty().getAccountNumber());
                    assertEquals("OCBCSGSGXXX", responseMessage.getPayerParty().getBankCode());
                    assertEquals("05461368", responseMessage.getReceiverParty().getAccountNumber());
                    assertEquals("DBSSGB2LXXX", responseMessage.getReceiverParty().getBankCode());
                    assertEquals("/BNF/FFC-4697132", responseMessage.getSupportingInformation());
                });
    }

    @Test
    void shouldFindMarketSettlementMessageByTradeId() throws Exception {
        String tradeRequestJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/market-settlement-messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tradeRequestJson));

        mvc.perform(MockMvcRequestBuilders.get("/api/market-settlement-messages/16846548")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MarketSettlementMessage responseMessage = objectMapper.readValue(result.getResponse().getContentAsString(), MarketSettlementMessage.class);
                    assertNotNull(responseMessage.getMessageId());
                    assertEquals("16846548", responseMessage.getTradeId());
                    assertEquals(new BigDecimal("12894.65").setScale(2, RoundingMode.UNNECESSARY), responseMessage.getAmount());
                    assertEquals("20022020", responseMessage.getValueDate());
                    assertEquals("USD", responseMessage.getCurrency());
                    assertEquals("438421", responseMessage.getPayerParty().getAccountNumber());
                    assertEquals("OCBCSGSGXXX", responseMessage.getPayerParty().getBankCode());
                    assertEquals("05461368", responseMessage.getReceiverParty().getAccountNumber());
                    assertEquals("DBSSGB2LXXX", responseMessage.getReceiverParty().getBankCode());
                    assertEquals("/BNF/FFC-4697132", responseMessage.getSupportingInformation());
                });
    }
}
