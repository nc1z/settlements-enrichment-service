package com.example.settlementsenrichment.integration;

import com.example.settlementsenrichment.AbstractIntegrationTest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import com.example.settlementsenrichment.exception.ErrorResponse;
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

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
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

        String tradeRequestJsonVariant = """
                {
                  "TradeId": "16846549",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestJsonVariant))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MarketSettlementMessage responseMessage = objectMapper.readValue(result.getResponse().getContentAsString(), MarketSettlementMessage.class);
                    assertNotNull(responseMessage.getMessageId());
                    assertEquals("16846549", responseMessage.getTradeId());
                    assertEquals(new BigDecimal("12894.00").setScale(2, RoundingMode.UNNECESSARY), responseMessage.getAmount());
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

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tradeRequestJson));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/market-settlement-messages/16846548")
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

    @Test
    void shouldReturnBadRequestForInvalidTradeId() throws Exception {
        String tradeRequestWithNoTradeIdJson = """
                {
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithNoTradeIdJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("tradeId"));
                    assertEquals("TradeId cannot be blank", errorResponse.getErrors().get("tradeId"));
                });

        String tradeRequestWithInvalidTradeIdJson = """
                {
                  "TradeId": "abc123",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidTradeIdJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("tradeId"));
                    assertEquals("Trade ID must only contain digits", errorResponse.getErrors().get("tradeId"));
                });
    }

    @Test
    void shouldReturnConflictForDuplicateTradeId() throws Exception {
        String tradeRequestJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestJson))
                .andExpect(status().isCreated());

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Market Settlement Message already exists with TradeId: 16846548", errorResponse.getMessage());
                });
    }

    @Test
    void shouldReturnBadRequestForNullSSICode() throws Exception {
        String tradeRequestWithNoSSICodeJson = """
                {
                  "TradeId": "16846548",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithNoSSICodeJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("code"));
                    assertEquals("SSI Code cannot be blank", errorResponse.getErrors().get("code"));
                });
    }

    @Test
    void shouldReturnNotFoundForInvalidSSICode() throws Exception {
        String tradeRequestWithNoSSICodeJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "DOES_NOT_EXIST",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithNoSSICodeJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("SSI not found with code: DOES_NOT_EXIST", errorResponse.getMessage());
                });
    }

    @Test
    void shouldReturnBadRequestForInvalidAmount() throws Exception {
        String tradeRequestWithNoAmountJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithNoAmountJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("amount"));
                    assertEquals("Amount cannot be null", errorResponse.getErrors().get("amount"));
                });

        String tradeRequestWithInvalidAmountDecimalsJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.653,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidAmountDecimalsJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("amount"));
                    assertEquals("Amount must have at most 2 decimal places and 15 integer digits", errorResponse.getErrors().get("amount"));
                });

        String tradeRequestWithInvalidAmountDigitsJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 1234567890123456,
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidAmountDigitsJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("amount"));
                    assertEquals("Amount must have at most 2 decimal places and 15 integer digits", errorResponse.getErrors().get("amount"));
                });

        String tradeRequestWithInvalidAmountTypeJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": "abcd",
                  "Currency": "USD",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidAmountTypeJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Invalid type for field 'Amount'. Expected a numeric value.", errorResponse.getMessage());
                });
    }

    @Test
    void shouldReturnBadRequestForInvalidCurrency() throws Exception {
        String tradeRequestWithNoCurrencyJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithNoCurrencyJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("currency"));
                    assertEquals("Currency cannot be blank", errorResponse.getErrors().get("currency"));
                });

        String tradeRequestWithInvalidCurrencyJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "INVALID",
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidCurrencyJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("currency"));
                    assertEquals("Currency must be a valid ISO 4217 code", errorResponse.getErrors().get("currency"));
                });

        String tradeRequestWithInvalidCurrencyTypeJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": 1234,
                  "Value Date": "20022020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidCurrencyTypeJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Invalid type for field 'Currency'. Expected a string value.", errorResponse.getMessage());
                });
    }

    @Test
    void shouldReturnBadRequestForInvalidValueDate() throws Exception {
        String tradeRequestWithNoValueDateJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithNoValueDateJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("valueDate"));
                    assertEquals("Value Date cannot be blank", errorResponse.getErrors().get("valueDate"));
                });

        String tradeRequestWithInvalidValueDateJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": "32212020"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidValueDateJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Validation error", errorResponse.getMessage());
                    assertNotNull(errorResponse.getErrors().get("valueDate"));
                    assertEquals("Invalid value date, expected format is ddMMyyyy", errorResponse.getErrors().get("valueDate"));
                });

        String tradeRequestWithInvalidValueDateTypeJson = """
                {
                  "TradeId": "16846548",
                  "SSI Code": "OCBC_DBS_1",
                  "Amount": 12894.65,
                  "Currency": "USD",
                  "Value Date": 20022020
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/market-settlement-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeRequestWithInvalidValueDateTypeJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
                    assertEquals("Invalid type for field 'Value Date'. Expected a string value.", errorResponse.getMessage());
                });
    }
}
