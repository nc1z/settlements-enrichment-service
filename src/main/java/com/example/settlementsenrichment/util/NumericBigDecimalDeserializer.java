package com.example.settlementsenrichment.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;

public class NumericBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (!node.isNumber()) {
            String fieldName = jsonParser.currentName();
            throw new IOException(String.format("Invalid type for field '%s'. Expected a numeric value.", fieldName));
        }
        return new BigDecimal(node.asText());
    }
}
