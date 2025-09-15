package com.mythictales.bms.taplist.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mythictales.bms.taplist.config.RestExceptionHandler;
import com.mythictales.bms.taplist.service.BusinessValidationException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

class RestExceptionHandlerTest {

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    this.mvc =
        MockMvcBuilders.standaloneSetup(new TestApi())
            .setControllerAdvice(new RestExceptionHandler())
            .build();
  }

  // Minimal controller inside API package to trigger advice scoping
  @RestController
  @RequestMapping("/api/v1/test")
  static class TestApi {
    record Req(@NotNull Double amount) {}

    @GetMapping("/notfound")
    public String notfound() {
      throw new java.util.NoSuchElementException();
    }

    @GetMapping("/forbidden")
    public String forbidden() {
      throw new org.springframework.security.access.AccessDeniedException("no");
    }

    @GetMapping("/business")
    public String business() {
      throw new BusinessValidationException("Rule violated", java.util.Map.of("k", "v"));
    }

    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String validate(@Valid @RequestBody Req req) {
      return "ok";
    }
  }

  @Test
  void returns_problem_json_on_not_found() throws Exception {
    mvc.perform(get("/api/v1/test/notfound"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.error", is("Not Found")))
        .andExpect(jsonPath("$.message", notNullValue()))
        .andExpect(jsonPath("$.timestamp", notNullValue()));
  }

  @Test
  void returns_problem_json_on_forbidden() throws Exception {
    mvc.perform(get("/api/v1/test/forbidden"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status", is(403)))
        .andExpect(jsonPath("$.error", is("Forbidden")));
  }

  @Test
  void returns_problem_json_on_business_validation_422() throws Exception {
    mvc.perform(get("/api/v1/test/business"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.status", is(422)))
        .andExpect(jsonPath("$.message", is("Rule violated")))
        .andExpect(jsonPath("$.details.k", is("v")));
  }

  @Test
  void returns_problem_json_on_request_body_validation_400() throws Exception {
    // amount is required
    mvc.perform(post("/api/v1/test/validate").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Bad Request")))
        .andExpect(jsonPath("$.message", is("Validation failed")))
        .andExpect(jsonPath("$.details.amount", notNullValue()));
  }
}
