package com.mythictales.bms.taplist.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(BmsCorsProperties.class)
public class WebCorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource(BmsCorsProperties props) {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(props.getAllowedOrigins());
    cfg.setAllowedMethods(props.getAllowedMethods());
    cfg.setAllowedHeaders(props.getAllowedHeaders());
    cfg.setAllowCredentials(props.isAllowCredentials());
    cfg.setMaxAge(props.getMaxAgeSeconds());

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
