package com.example.settlementsenrichment.entity;

import com.example.settlementsenrichment.util.ValidationConstants;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"id", "code", "payerAccountNumber", "payerBank", "receiverAccountNumber", "receiverBank", "supportingInformation"})
public class StandardSettlementInstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(type = "Long", example = "1")
    private Long id;

    @NonNull
    @NotNull(message = "code cannot be null")
    @Column(name = "code", nullable = false, unique = true)
    @Schema(type = "String", example = "OCBC_DBS_1")
    private String code;

    @NonNull
    @NotNull(message = "Payer Account Number cannot be null")
    @Pattern(regexp = ValidationConstants.NUMERIC_REGEX, message = "Invalid payer account number")
    @Column(name = "payer_account_number", nullable = false)
    @Schema(type = "String", example = "05461368")
    private String payerAccountNumber;

    @NonNull
    @NotNull(message = "Payer Bank cannot be null")
    @Column(name = "payer_bank", nullable = false)
    @Pattern(regexp = ValidationConstants.SWIFT_CODE_REGEX, message = "Invalid SWIFT code")
    @Schema(type = "String", example = "DBSSGB2LXXX")
    private String payerBank;

    @NonNull
    @NotNull(message = "Receiver Account Number cannot be null")
    @Pattern(regexp = ValidationConstants.NUMERIC_REGEX, message = "Invalid receiver account number")
    @Column(name = "receiver_account_number", nullable = false)
    @Schema(type = "String", example = "438421")
    private String receiverAccountNumber;

    @NonNull
    @NotNull(message = "Receiver Bank cannot be null")
    @Column(name = "receiver_bank", nullable = false)
    @Pattern(regexp = ValidationConstants.SWIFT_CODE_REGEX, message = "Invalid SWIFT code")
    @Schema(type = "String", example = "OCBCSGSGXXX")
    private String receiverBank;

    @Column(name = "supporting_information")
    @Schema(type = "String", example = "BNF:PAY CLIENT")
    private String supportingInformation;
}
