package com.mythictales.bms.taplist.api;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.security.AccessPolicy;
import com.mythictales.bms.taplist.service.TapService;

class TapApiControllerSprint2Test extends BaseApiControllerTest {

  private TapRepository tapRepository;
  private TapService tapService;
  private KegRepository kegRepository;
  private AccessPolicy accessPolicy;
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    tapRepository = Mockito.mock(TapRepository.class);
    tapService = Mockito.mock(TapService.class);
    kegRepository = Mockito.mock(KegRepository.class);
    accessPolicy = Mockito.mock(AccessPolicy.class);
    TapApiController controller =
        new TapApiController(tapRepository, tapService, kegRepository, accessPolicy);
    mvc = buildMvc(controller);
  }

  @Test
  void pour_with_expectedVersion_mismatch_returns_409() throws Exception {
    // Given a Tap with default version 0L
    Tap tap = new Tap(7);
    // repository returns the tap
    given(tapRepository.findById(10L)).willReturn(Optional.of(tap));

    // When expectedVersion != actual (0)
    String body = "{\n" + "  \"ounces\": 1.0,\n" + "  \"expectedVersion\": 1\n" + "}";

    mvc.perform(post("/api/v1/taps/10/pour").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(409)))
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  void listTaps_returns_paginated_content_sorted_by_number() throws Exception {
    Tap t1 = new Tap(1);
    Tap t2 = new Tap(2);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Tap> page = new PageImpl<>(List.of(t1, t2), pageable, 2);
    given(tapRepository.findAll(any(Pageable.class))).willReturn(page);

    mvc.perform(get("/api/v1/taps").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0].number", is(1)))
        .andExpect(jsonPath("$.content[1].number", is(2)))
        .andExpect(jsonPath("$.totalElements", is(2)));
  }

  @Test
  void blow_with_expectedVersion_mismatch_returns_409() throws Exception {
    Tap tap = new Tap(3);
    given(tapRepository.findById(20L)).willReturn(Optional.of(tap));

    String body = "{\n" + "  \"expectedVersion\": 9\n" + "}";

    mvc.perform(post("/api/v1/taps/20/blow").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(409)))
        .andExpect(jsonPath("$.error").exists());
  }
}
