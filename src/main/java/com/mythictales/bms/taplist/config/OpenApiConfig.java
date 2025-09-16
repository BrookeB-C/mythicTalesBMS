package com.mythictales.bms.taplist.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "springdoc.enabled",
    havingValue = "true")
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
@SecurityScheme(
    name = "sessionCookie",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.COOKIE,
    paramName = "JSESSIONID")
public class OpenApiConfig {

  @Bean
  public OpenAPI taplistOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Mythic Tales BMS API")
                .description("REST API for taplists, kegs, venues, and admin flows.")
                .version("v1")
                .contact(new Contact().name("Mythic Tales").url("https://example.com"))
                .license(new License().name("Proprietary")))
        .servers(List.of(new Server().url("/").description("Default")));
  }

  // Group for planned REST endpoints under /api/**
  @Bean
  public GroupedOpenApi apiV1() {
    return GroupedOpenApi.builder().group("api-v1").pathsToMatch("/api/**").build();
  }

  // Group for existing MVC admin pages (useful while transitioning)
  @Bean
  public GroupedOpenApi adminGroup() {
    return GroupedOpenApi.builder().group("admin").pathsToMatch("/admin/**", "/taplist/**").build();
  }
}
