package com.example.settlementsenrichment.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
@JsonPropertyOrder({ "id", "code", "payerAccountNumber", "payerBank", "receiverAccountNumber", "receiverBank", "supportingInformation" })
public class StandardSettlementInstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @NotNull(message = "code cannot be null")
    @Column(name = "code", nullable = false, unique = true)
    private final String code;

    @NotNull(message = "payerAccountNumber cannot be null")
    @Column(name = "payer_account_number", nullable = false)
    private final Long payerAccountNumber;

    @NotNull(message = "payerBank cannot be null")
    @Column(name = "payer_bank", nullable = false)
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "Invalid SWIFT code")
    private final String payerBank;

    @NotNull(message = "receiverAccountNumber cannot be null")
    @Column(name = "receiver_account_number", nullable = false)
    private final Long receiverAccountNumber;

    @NotNull(message = "receiverBank cannot be null")
    @Column(name = "receiver_bank", nullable = false)
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "Invalid SWIFT code")
    private final String receiverBank;

    @Column(name = "supporting_information")
    private final String supportingInformation;
}
