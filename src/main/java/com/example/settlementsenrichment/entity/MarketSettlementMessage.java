package com.example.settlementsenrichment.entity;

import com.example.settlementsenrichment.util.ValidationConstants;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
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
    private UUID messageId;

    @NotNull(message = "Trade ID cannot be null")
    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;

    @NotNull(message = "Amount cannot be null")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Value date cannot be null")
    @Pattern(regexp = ValidationConstants.VALUE_DATE_REGEX, message = "Invalid value date")
    @Column(name = "value_date", nullable = false)
    private String valueDate;

    @NotNull(message = "Currency cannot be null")
    @Column(name = "currency", nullable = false)
    private String currency;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "accountNumber", column = @Column(name = "payer_account_number")),
            @AttributeOverride(name = "bankCode", column = @Column(name = "payer_bank_code"))
    })
    private Party payerParty;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "accountNumber", column = @Column(name = "receiver_account_number")),
            @AttributeOverride(name = "bankCode", column = @Column(name = "receiver_bank_code"))
    })
    private Party receiverParty;

    @Column(name = "supporting_information")
    private String supportingInformation;
}
