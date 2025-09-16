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

import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.repo.BeerRepository;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.KegSizeSpecRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;

class KegApiControllerPaginationTest extends BaseApiControllerTest {

  private KegRepository kegs;
  private VenueRepository venues;
  private BreweryRepository breweries;
  private BeerRepository beers;
  private KegSizeSpecRepository sizes;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    kegs = Mockito.mock(KegRepository.class);
    venues = Mockito.mock(VenueRepository.class);
    breweries = Mockito.mock(BreweryRepository.class);
    beers = Mockito.mock(BeerRepository.class);
    sizes = Mockito.mock(KegSizeSpecRepository.class);
    var controller = new KegApiController(kegs, venues, breweries, beers, sizes);
    mvc = buildMvc(controller);
  }

  @Test
  void list_all_returns_page() throws Exception {
    Keg k1 = new Keg();
    k1.setId(1L);
    Keg k2 = new Keg();
    k2.setId(2L);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Keg> page = new PageImpl<>(java.util.List.of(k1, k2), pageable, 2);
    given(kegs.findAll(any(Pageable.class))).willReturn(page);

    mvc.perform(get("/api/v1/kegs").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements", is(2)));
  }

  @Test
  void list_by_brewery_uses_paged_finder() throws Exception {
    Pageable pageable = PageRequest.of(0, 1);
    Page<Keg> page = new PageImpl<>(java.util.List.of(), pageable, 0);
    given(kegs.findByBreweryId(org.mockito.ArgumentMatchers.eq(5L), any(Pageable.class)))
        .willReturn(page);

    mvc.perform(get("/api/v1/kegs").param("breweryId", "5").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.totalElements", is(0)));
  }
}
