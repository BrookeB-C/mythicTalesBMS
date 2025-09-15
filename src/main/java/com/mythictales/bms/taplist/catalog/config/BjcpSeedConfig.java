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
        if (in != null) {
          importer.importCsv(in, true);
        }
      } catch (Exception ignored) {}
    };
  }
}

