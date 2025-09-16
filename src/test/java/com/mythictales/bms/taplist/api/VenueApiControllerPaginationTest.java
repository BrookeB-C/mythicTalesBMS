package com.mythictales.bms.taplist.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.repo.VenueRepository;

class VenueApiControllerPaginationTest extends BaseApiControllerTest {

  private VenueRepository venues;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    venues = Mockito.mock(VenueRepository.class);
    var controller = new VenueApiController(venues);
    mvc = buildMvc(controller);
  }

  @Test
  void list_all_returns_page() throws Exception {
    Venue v1 = new Venue();
    Venue v2 = new Venue();
    Pageable pageable = PageRequest.of(0, 10);
    Page<Venue> page = new PageImpl<>(java.util.List.of(v1, v2), pageable, 2);
    given(venues.findAll(any(Pageable.class))).willReturn(page);

    mvc.perform(get("/api/v1/venues").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements", is(2)));
  }

  @Test
  void list_by_brewery_uses_paged_finder() throws Exception {
    Pageable pageable = PageRequest.of(0, 5);
    Page<Venue> page = new PageImpl<>(java.util.List.of(), pageable, 0);
    given(venues.findByBreweryId(org.mockito.ArgumentMatchers.eq(9L), any(Pageable.class)))
        .willReturn(page);

    mvc.perform(get("/api/v1/venues").param("breweryId", "9").param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.totalElements", is(0)));
  }
}
