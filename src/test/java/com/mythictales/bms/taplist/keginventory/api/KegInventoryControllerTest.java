package com.mythictales.bms.taplist.keginventory.api;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mythictales.bms.taplist.api.BaseApiControllerTest;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.keginventory.api.dto.AssignRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.MoveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReturnRequest;
import com.mythictales.bms.taplist.keginventory.service.KegInventoryService;

class KegInventoryControllerTest extends BaseApiControllerTest {

  private KegInventoryService service;
  private MockMvc mvc;
  private final ObjectMapper om = new ObjectMapper();

  @BeforeEach
  void setUp() {
    service = Mockito.mock(KegInventoryService.class);
    var controller = new KegInventoryController(service);
    mvc = buildMvc(controller);
  }

  @Test
  void assign_ok_returns_keg() throws Exception {
    Keg keg = new Keg();
    keg.setId(99L);
    given(service.assignToVenue(any(), eq(1L), eq(10L))).willReturn(keg);

    String body = om.writeValueAsString(new AssignRequest(1L, 10L));
    mvc.perform(
            post("/api/v1/keg-inventory/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(99)));
  }

  @Test
  void receive_missing_kegId_yields_400() throws Exception {
    // venueId present but kegId null
    String badBody = "{\n  \"venueId\": 10\n}";
    mvc.perform(
            post("/api/v1/keg-inventory/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Bad Request")));
  }

  @Test
  void move_same_from_to_yields_422() throws Exception {
    // Let service throw to exercise 422 mapping
    given(service.move(any(), eq(1L), eq(10L), eq(10L)))
        .willThrow(
            new com.mythictales.bms.taplist.service.BusinessValidationException(
                "fromVenueId and toVenueId must differ"));

    String body = om.writeValueAsString(new MoveRequest(1L, 10L, 10L));
    mvc.perform(
            post("/api/v1/keg-inventory/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.status", is(422)))
        .andExpect(jsonPath("$.message", is("fromVenueId and toVenueId must differ")));
  }

  @Test
  void return_ok_returns_keg() throws Exception {
    Keg keg = new Keg();
    keg.setId(5L);
    given(service.returnToBrewery(any(), eq(1L))).willReturn(keg);

    String body = om.writeValueAsString(new ReturnRequest(1L));
    mvc.perform(
            post("/api/v1/keg-inventory/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(5)));
  }
}
