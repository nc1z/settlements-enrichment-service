package com.example.settlementsenrichment.dto;

import com.example.settlementsenrichment.util.ValidationConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

public record TradeRequest(
        @JsonProperty("TradeId")
        @NotBlank(message = "TradeId cannot be blank")
        @Pattern(regexp = ValidationConstants.NUMERIC_REGEX, message = "Trade ID must only contain digits")
        String tradeId,

        @JsonProperty("SSI Code")
        @NotBlank(message = "SSI Code cannot be blank")
        String code,

        @JsonProperty("Amount")
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.00", inclusive = false, message = "Amount must be greater than zero")
        @Digits(integer = 15, fraction = 2, message = "Amount must have at most 2 decimal places and 15 integer digits")
        BigDecimal amount,

        @JsonProperty("Currency")
        @NotBlank(message = "Currency cannot be blank")
        @Pattern(regexp = ValidationConstants.ISO4217_CURRENCY_CODE_REGEX, message = "Currency must be a valid ISO 4217 code")
        String currency,

        @JsonProperty("Value Date")
        @NotBlank(message = "Value Date cannot be blank")
        @Pattern(regexp = ValidationConstants.VALUE_DATE_REGEX, message = "Invalid value date, expected format is ddMMyyyy")
        String valueDate
) {
    @Builder
    public TradeRequest {
    }
}
