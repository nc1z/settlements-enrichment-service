package com.example.settlementsenrichment.entity;

import com.example.settlementsenrichment.util.ValidationConstants;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"tradeId", "messageId", "amount", "valueDate", "currency", "payerParty", "receiverParty", "supportingInformation"})
public class MarketSettlementMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(type = "UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID messageId;

    @NotNull(message = "Trade ID cannot be null")
    @Pattern(regexp = ValidationConstants.NUMERIC_REGEX, message = "Trade ID must only contain digits")
    @Column(name = "trade_id", nullable = false, unique = true)
    @Schema(type = "String", example = "16846548")
    private String tradeId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.00", inclusive = false, message = "Amount must be greater than zero")
    @Digits(integer = 15, fraction = 2, message = "Amount must have at most 2 decimal places and 15 integer digits")
    @Column(name = "amount", nullable = false)
    @Schema(type = "BigDecimal", example = "12894.65")
    private BigDecimal amount;

    @NotNull(message = "Value date cannot be null")
    @Pattern(regexp = ValidationConstants.VALUE_DATE_REGEX, message = "Invalid value date")
    @Column(name = "value_date", nullable = false)
    @Schema(type = "String", example = "20022020")
    private String valueDate;

    @NotNull(message = "Currency cannot be null")
    @Pattern(regexp = ValidationConstants.ISO4217_CURRENCY_CODE_REGEX, message = "Currency must be a valid ISO 4217 code")
    @Column(name = "currency", nullable = false)
    @Schema(type = "String", example = "USD")
    private String currency;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "accountNumber", column = @Column(name = "payer_account_number")),
            @AttributeOverride(name = "bankCode", column = @Column(name = "payer_bank_code"))
    })
    @Schema(type = "Object", example = "{\"accountNumber\": \"438421\", \"bankCode\": \"OCBCSGSGXXX\"}")
    private Party payerParty;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "accountNumber", column = @Column(name = "receiver_account_number")),
            @AttributeOverride(name = "bankCode", column = @Column(name = "receiver_bank_code"))
    })
    @Schema(type = "Object", example = "{\"accountNumber\": \"05461368\", \"bankCode\": \"DBSSGB2LXXX\"}")
    private Party receiverParty;

    @Column(name = "supporting_information")
    @Schema(type = "String", example = "/BNF/FFC-4697132")
    private String supportingInformation;
}
