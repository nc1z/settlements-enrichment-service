package com.example.settlementsenrichment.config;

import com.example.settlementsenrichment.entity.StandardSettlementInstruction;
import com.example.settlementsenrichment.repository.StandardSettlementInstructionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final StandardSettlementInstructionRepository standardSettlementInstructionRepository;

    public DataLoader(StandardSettlementInstructionRepository standardSettlementInstructionRepository) {
        this.standardSettlementInstructionRepository = standardSettlementInstructionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (standardSettlementInstructionRepository.count() == 0) {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<StandardSettlementInstruction>> typeReference = new TypeReference<>() {
            };

            try (InputStream inputStream = getClass().getResourceAsStream("/data/ssi-reference-data.json")) {
                if (inputStream == null) {
                    throw new IllegalArgumentException("File not found: ssi-reference-data.json");
                }
                List<StandardSettlementInstruction> ssiList = mapper.readValue(inputStream, typeReference);
                standardSettlementInstructionRepository.saveAll(ssiList);
                logger.info("Standard Settlement Instructions loaded from JSON file and saved to database");
            } catch (Exception e) {
                logger.error("Unable to load Standard Settlement Instructions: ", e);
            }
        }
    }
}
