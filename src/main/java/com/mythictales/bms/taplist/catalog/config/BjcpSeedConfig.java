package com.mythictales.bms.taplist.catalog.config;

import com.mythictales.bms.taplist.catalog.service.StyleImportService;
import java.io.InputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BjcpSeedConfig {
  @Bean
  @Profile("dev")
  CommandLineRunner seedBjcpStyles(StyleImportService importer){
    return args -> {
      try (InputStream in = getClass().getResourceAsStream("/bjcp/bjcp-2021.csv")) {
        // Prefer 2015 if present; fallback to 2021 sample
        InputStream src = getClass().getResourceAsStream("/bjcp/bjcp-2015.csv");
        InputStream use = src != null ? src : in;
        if (use != null) importer.importCsv(use, true);
      } catch (Exception ignored) {}
    };
  }
}
