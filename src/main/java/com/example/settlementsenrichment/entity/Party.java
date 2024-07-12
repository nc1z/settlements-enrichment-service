package com.example.settlementsenrichment.entity;

import com.example.settlementsenrichment.util.ValidationConstants;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Party {
    @NotNull(message = "Account number cannot be null")
    @Pattern(regexp = ValidationConstants.NUMERIC_REGEX, message = "Invalid account number")
    private String accountNumber;

    @NotNull(message = "Bank code cannot be null")
    @Pattern(regexp = ValidationConstants.SWIFT_CODE_REGEX, message = "Invalid SWIFT code")
    private String bankCode;
}
