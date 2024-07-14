package com.example.settlementsenrichment.controller;

import com.example.settlementsenrichment.dto.TradeRequest;
import com.example.settlementsenrichment.entity.MarketSettlementMessage;
import com.example.settlementsenrichment.service.MarketSettlementMessageService;
import com.example.settlementsenrichment.validator.DigitsOnly;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/market-settlement-messages")
public class MarketSettlementMessageController {
    private final MarketSettlementMessageService service;

    public MarketSettlementMessageController(MarketSettlementMessageService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<MarketSettlementMessage> createMessage(@Valid @RequestBody TradeRequest tradeRequest) {
        MarketSettlementMessage message = service.createMarketSettlementMessage(tradeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{tradeId}")
    public ResponseEntity<MarketSettlementMessage> getMessageByTradeId(@PathVariable @DigitsOnly(field = "tradeId") String tradeId) {
        MarketSettlementMessage message = service.findByTradeId(tradeId);
        return ResponseEntity.ok(message);
    }
}
