package com.example.settlementsenrichment.util;

public class ValidationConstants {
    public static final String SWIFT_CODE_REGEX = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$";
    public static final String NUMERIC_REGEX = "^[0-9]+$";
    public static final String VALUE_DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[0-2])\\d{4}$";
    public static final String ISO4217_CURRENCY_CODE_REGEX = "[A-Z]{3}";
}
