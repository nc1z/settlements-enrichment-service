package com.example.settlementsenrichment.controller;

import com.example.settlementsenrichment.dto.TradeRequest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.Party;
import com.example.settlementsenrichment.service.MarketSettlementMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarketSettlementMessageController.class)
class MarketSettlementMessageControllerTest {

    private final List<MarketSettlementMessage> marketSettlementMessages = new ArrayList<>();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MarketSettlementMessageService marketSettlementMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MarketSettlementMessage message = MarketSettlementMessage.builder()
                .tradeId("16846549")
                .amount(new BigDecimal("5450.23").setScale(2, RoundingMode.UNNECESSARY))
                .valueDate("17032020")
                .currency("USD")
                .payerParty(new Party("185586", "DBSSSGSGXXX"))
                .receiverParty(new Party("1868422", "SCBLAU2SXXX"))
                .supportingInformation("/RFB/Test payment")
                .build();

        marketSettlementMessages.add(message);
    }

    @Test
    void shouldCreateMarketSettlementMessage() throws Exception {
        TradeRequest validRequest = TradeRequest.builder()
                .tradeId("16846549")
                .code("DBS_SCB")
                .amount(new BigDecimal("5450"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        when(marketSettlementMessageService.createMarketSettlementMessage(validRequest))
                .thenReturn(marketSettlementMessages.get(0)); // Mock the service response

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldFindMarketSettlementMessageByTradeId() throws Exception {
        MarketSettlementMessage message = marketSettlementMessages.get(0);
        when(marketSettlementMessageService.findByTradeId("16846549"))
                .thenReturn(message); // Mock the service response

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/market-settlement-messages/16846549")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tradeId").value(message.getTradeId()))
                .andExpect(jsonPath("$.amount").value(message.getAmount().setScale(2, RoundingMode.UNNECESSARY)))
                .andExpect(jsonPath("$.valueDate").value(message.getValueDate()))
                .andExpect(jsonPath("$.currency").value(message.getCurrency()))
                .andExpect(jsonPath("$.payerParty.accountNumber").value(message.getPayerParty().getAccountNumber()))
                .andExpect(jsonPath("$.payerParty.bankCode").value(message.getPayerParty().getBankCode()))
                .andExpect(jsonPath("$.receiverParty.accountNumber").value(message.getReceiverParty().getAccountNumber()))
                .andExpect(jsonPath("$.receiverParty.bankCode").value(message.getReceiverParty().getBankCode()))
                .andExpect(jsonPath("$.supportingInformation").value(message.getSupportingInformation()));
    }

    @Test
    void shouldReturnBadRequestForInvalidTradeId() throws Exception {
        TradeRequest invalidRequest = TradeRequest.builder()
                .tradeId(null) // Invalid tradeId
                .code("DBS_SCB")
                .amount(new BigDecimal("5450.00"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.tradeId").value("TradeId cannot be blank"));

        TradeRequest requestWithAlphabetsInTradeId = TradeRequest.builder()
                .tradeId("abc1234") // Invalid tradeId
                .code("DBS_SCB")
                .amount(new BigDecimal("5450.00"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithAlphabetsInTradeId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.tradeId").value("Trade ID must only contain digits"));
    }

    @Test
    void shouldReturnBadRequestForInvalidAmount() throws Exception {
        TradeRequest invalidRequest = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(null) // Invalid amount
                .currency("USD")
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").value("Amount cannot be null"));

        TradeRequest requestWithNegativeAmount = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(new BigDecimal("-5450")) // Invalid amount
                .currency("USD")
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithNegativeAmount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").value("Amount must be greater than zero"));

        TradeRequest requestWithAmount3DecimalPlaces = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(new BigDecimal("5450.333")) // Invalid amount
                .currency("USD")
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithAmount3DecimalPlaces)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").value("Amount must have at most 2 decimal places and 15 integer digits"));


        TradeRequest requestWithInvalidAmount = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(new BigDecimal("5450243242342231.25")) // Invalid amount
                .currency("USD")
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithInvalidAmount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").value("Amount must have at most 2 decimal places and 15 integer digits"));
    }

    @Test
    void shouldReturnBadRequestForNullCode() throws Exception {
        TradeRequest invalidRequest = TradeRequest.builder()
                .tradeId("16846550")
                .code(null) // Invalid code
                .amount(new BigDecimal("1234.25"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").value("SSI Code cannot be blank"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCurrency() throws Exception {
        TradeRequest invalidRequest = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(new BigDecimal("1234.25"))
                .currency(null) // Invalid currency
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.currency").value("Currency cannot be blank"));

        TradeRequest requestWithInvalidCurrency = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(new BigDecimal("1234.25"))
                .currency("ABCD") // Invalid currency
                .valueDate("17032020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithInvalidCurrency)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.currency").value("Currency must be a valid ISO 4217 code"));
    }

    @Test
    void shouldReturnBadRequestForInvalidDate() throws Exception {
        TradeRequest invalidRequest = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(new BigDecimal("1234.25"))
                .currency("USD")
                .valueDate(null)
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.valueDate").value("Value Date cannot be blank"));

        TradeRequest invalidDateRequest = TradeRequest.builder()
                .tradeId("16846550")
                .code("DBS_SCB")
                .amount(new BigDecimal("1234.25"))
                .currency("USD")
                .valueDate("32012020")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.valueDate").value("Invalid value date, expected format is ddMMyyyy"));
    }

}
