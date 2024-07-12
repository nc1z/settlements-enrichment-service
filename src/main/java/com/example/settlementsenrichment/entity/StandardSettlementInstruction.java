package com.example.settlementsenrichment.entity;

import com.example.settlementsenrichment.util.ValidationConstants;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@JsonPropertyOrder({"id", "code", "payerAccountNumber", "payerBank", "receiverAccountNumber", "receiverBank", "supportingInformation"})
public class StandardSettlementInstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotNull(message = "code cannot be null")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NonNull
    @NotNull(message = "payerAccountNumber cannot be null")
    @Column(name = "payer_account_number", nullable = false)
    private String payerAccountNumber;

    @NonNull
    @NotNull(message = "payerBank cannot be null")
    @Column(name = "payer_bank", nullable = false)
    @Pattern(regexp = ValidationConstants.SWIFT_CODE_REGEX, message = "Invalid SWIFT code")
    private String payerBank;

    @NonNull
    @NotNull(message = "receiverAccountNumber cannot be null")
    @Column(name = "receiver_account_number", nullable = false)
    private String receiverAccountNumber;

    @NonNull
    @NotNull(message = "receiverBank cannot be null")
    @Column(name = "receiver_bank", nullable = false)
    @Pattern(regexp = ValidationConstants.SWIFT_CODE_REGEX, message = "Invalid SWIFT code")
    private String receiverBank;

    @Column(name = "supporting_information")
    private String supportingInformation;
}
