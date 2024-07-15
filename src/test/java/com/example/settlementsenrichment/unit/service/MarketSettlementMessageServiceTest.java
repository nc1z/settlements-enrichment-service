package com.example.settlementsenrichment.unit.service;

import com.example.settlementsenrichment.dto.TradeRequest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.entity.Party;
import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import com.example.settlementsenrichment.exception.DuplicateResourceException;
import com.example.settlementsenrichment.exception.ResourceNotFoundException;
import com.example.settlementsenrichment.repository.MarketSettlementMessageRepository;
import com.example.settlementsenrichment.repository.StandardSettlementInstructionRepository;
import com.example.settlementsenrichment.service.MarketSettlementMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketSettlementMessageServiceTest {

    @Mock
    private MarketSettlementMessageRepository marketSettlementMessageRepository;

    @Mock
    private StandardSettlementInstructionRepository standardSettlementInstructionRepository;

    @InjectMocks
    private MarketSettlementMessageService marketSettlementMessageService;

    private TradeRequest tradeRequest;
    private StandardSettlementInstruction ssi;
    private List<MarketSettlementMessage> marketSettlementMessages;

    @BeforeEach
    void setUp() {
        tradeRequest = TradeRequest.builder()
                .tradeId("16846548")
                .code("OCBC_DBS_1")
                .amount(new BigDecimal("12894.65"))
                .currency("USD")
                .valueDate("20022020")
                .build();

        ssi = StandardSettlementInstruction.builder()
                .code("OCBC_DBS_1")
                .payerAccountNumber("438421")
                .payerBank("OCBCSGSGXXX")
                .receiverAccountNumber("05461368")
                .receiverBank("DBSSGB2LXXX")
                .supportingInformation("BNF:FFC-4697132")
                .build();

        marketSettlementMessages = new ArrayList<>();

        MarketSettlementMessage message = MarketSettlementMessage.builder()
                .tradeId("16846549")
                .amount(new BigDecimal("5450"))
                .valueDate("17032020")
                .currency("USD")
                .payerParty(new Party("185586", "DBSSSGSGXXX"))
                .receiverParty(new Party("1868422", "SCBLAU2SXXX"))
                .supportingInformation("/RFB/Test payment")
                .build();

        marketSettlementMessages.add(message);
    }

    @Test
    void shouldCreateMarketSettlementMessage() {
        when(marketSettlementMessageRepository.findByTradeId(tradeRequest.tradeId())).thenReturn(Optional.empty());
        when(standardSettlementInstructionRepository.findByCode(tradeRequest.code())).thenReturn(Optional.of(ssi));
        when(marketSettlementMessageRepository.save(any(MarketSettlementMessage.class)))
                .thenAnswer(invocation -> {
                    MarketSettlementMessage savedMessage = invocation.getArgument(0, MarketSettlementMessage.class);
                    savedMessage.setMessageId(UUID.randomUUID()); // Simulate database UUID auto generation
                    return savedMessage;
                });

        MarketSettlementMessage result = marketSettlementMessageService.createMarketSettlementMessage(tradeRequest);

        assertNotNull(result);
        assertEquals(tradeRequest.tradeId(), result.getTradeId());
        assertDoesNotThrow(() -> UUID.fromString(result.getMessageId().toString()));
        assertEquals(tradeRequest.amount().setScale(2, RoundingMode.UNNECESSARY), result.getAmount());
        assertEquals(tradeRequest.valueDate(), result.getValueDate());
        assertEquals(tradeRequest.currency(), result.getCurrency());
        assertEquals(ssi.getPayerAccountNumber(), result.getPayerParty().getAccountNumber());
        assertEquals(ssi.getPayerBank(), result.getPayerParty().getBankCode());
        assertEquals(ssi.getReceiverAccountNumber(), result.getReceiverParty().getAccountNumber());
        assertEquals(ssi.getReceiverBank(), result.getReceiverParty().getBankCode());
        assertEquals("/BNF/FFC-4697132", result.getSupportingInformation());
    }

    @Test
    void shouldCreateMarketSettlementMessageWithAmountTwoDecimalPlaces() {
        TradeRequest newTradeRequest = TradeRequest.builder()
                .tradeId("16846549")
                .code("CITI_GS")
                .amount(new BigDecimal("5450"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        StandardSettlementInstruction newSSI = StandardSettlementInstruction
                .builder()
                .code("CITI_GS")
                .payerAccountNumber("00454983")
                .payerBank("CITIGB2LXXX")
                .receiverAccountNumber("48486414")
                .receiverBank("GSCMUS33XXX")
                .supportingInformation("Some info")
                .build();

        when(marketSettlementMessageRepository.findByTradeId(newTradeRequest.tradeId())).thenReturn(Optional.empty());
        when(standardSettlementInstructionRepository.findByCode(newTradeRequest.code())).thenReturn(Optional.of(newSSI));
        when(marketSettlementMessageRepository.save(any(MarketSettlementMessage.class)))
                .thenAnswer(invocation -> {
                    MarketSettlementMessage savedMessage = invocation.getArgument(0, MarketSettlementMessage.class);
                    savedMessage.setMessageId(UUID.randomUUID()); // Simulate database UUID auto generation
                    return savedMessage;
                });

        MarketSettlementMessage result = marketSettlementMessageService.createMarketSettlementMessage(newTradeRequest);

        assertNotNull(result);
        assertEquals(newTradeRequest.amount().setScale(2, RoundingMode.UNNECESSARY), result.getAmount());
    }

    @Test
    void shouldCreateMarketSettlementMessageWithNullSupportingInformation() {
        TradeRequest newTradeRequest = TradeRequest.builder()
                .tradeId("16846549")
                .code("CITI_GS")
                .amount(new BigDecimal("5450"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        StandardSettlementInstruction newSSI = StandardSettlementInstruction
                .builder()
                .code("CITI_GS")
                .payerAccountNumber("00454983")
                .payerBank("CITIGB2LXXX")
                .receiverAccountNumber("48486414")
                .receiverBank("GSCMUS33XXX")
                .supportingInformation(null)
                .build();

        when(marketSettlementMessageRepository.findByTradeId(newTradeRequest.tradeId())).thenReturn(Optional.empty());
        when(standardSettlementInstructionRepository.findByCode(newTradeRequest.code())).thenReturn(Optional.of(newSSI));
        when(marketSettlementMessageRepository.save(any(MarketSettlementMessage.class)))
                .thenAnswer(invocation -> {
                    MarketSettlementMessage savedMessage = invocation.getArgument(0, MarketSettlementMessage.class);
                    savedMessage.setMessageId(UUID.randomUUID()); // Simulate database UUID auto generation
                    return savedMessage;
                });

        MarketSettlementMessage result = marketSettlementMessageService.createMarketSettlementMessage(newTradeRequest);

        assertNotNull(result);
        assertNull(result.getSupportingInformation());
    }

    @Test
    void shouldThrowDuplicateResourceException() {
        TradeRequest newTradeRequest = TradeRequest.builder()
                .tradeId("16846549")
                .code("DBS_SCB")
                .amount(new BigDecimal("5450"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        when(marketSettlementMessageRepository.findByTradeId(newTradeRequest.tradeId())).thenReturn(Optional.of(marketSettlementMessages.get(0)));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            marketSettlementMessageService.createMarketSettlementMessage(newTradeRequest);
        });
        assertEquals("Market Settlement Message already exists with TradeId: 16846549", exception.getMessage());
        verify(marketSettlementMessageRepository, times(1)).findByTradeId(newTradeRequest.tradeId());
        verify(standardSettlementInstructionRepository, times(0)).findByCode(anyString());
        verify(marketSettlementMessageRepository, times(0)).save(any(MarketSettlementMessage.class));
    }

    @Test
    void shouldThrowResourceNotFoundException() {
        TradeRequest newTradeRequest = TradeRequest.builder()
                .tradeId("16846549")
                .code("DBS_SCB")
                .amount(new BigDecimal("5450"))
                .currency("USD")
                .valueDate("17032020")
                .build();

        when(marketSettlementMessageRepository.findByTradeId(newTradeRequest.tradeId())).thenReturn(Optional.empty());
        when(standardSettlementInstructionRepository.findByCode(newTradeRequest.code())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            marketSettlementMessageService.createMarketSettlementMessage(newTradeRequest);
        });

        assertEquals("SSI not found with code: DBS_SCB", exception.getMessage());
        verify(marketSettlementMessageRepository, times(1)).findByTradeId(newTradeRequest.tradeId());
        verify(standardSettlementInstructionRepository, times(1)).findByCode(anyString());
        verify(marketSettlementMessageRepository, times(0)).save(any(MarketSettlementMessage.class));
    }
}