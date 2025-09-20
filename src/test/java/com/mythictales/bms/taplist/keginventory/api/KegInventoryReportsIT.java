package com.mythictales.bms.taplist.keginventory.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.domain.VenueType;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;
import com.mythictales.bms.taplist.security.CurrentUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KegInventoryReportsIT {

  @Autowired MockMvc mvc;
  @Autowired KegRepository kegs;
  @Autowired BreweryRepository breweries;
  @Autowired VenueRepository venues;

  private CurrentUser principalFor(Brewery b) {
    var ua = new com.mythictales.bms.taplist.domain.UserAccount();
    ua.setUsername("ituser");
    ua.setPassword("pw");
    ua.setRole(com.mythictales.bms.taplist.domain.Role.BREWERY_ADMIN);
    ua.setBrewery(b);
    return new CurrentUser(ua);
  }

  private Brewery stone() {
    return breweries.findAll().stream().findFirst().orElseThrow();
  }

  @BeforeEach
  void ensureBaseline() {
    // no-op; TestDataInitializer seeds what we need
  }

  @Test
  void statusCounts_returns_counts() throws Exception {
    mvc.perform(
            get("/api/v1/keg-inventory/reports/statusCounts")
                .with(user(principalFor(stone())))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("\"counts\"")));
  }

  @Test
  void reconciliation_lists_anomalies_after_manual_adjustments() throws Exception {
    Brewery stone = stone();
    Venue v =
        venues
            .findFirstByBreweryIdAndType(stone.getId(), VenueType.TAPROOM)
            .orElseThrow(() -> new RuntimeException("no venue"));
    // 1) RECEIVED with no venue
    Keg k1 = kegs.findByBreweryId(stone.getId()).stream().findFirst().orElseThrow();
    k1.setStatus(KegStatus.RECEIVED);
    k1.setAssignedVenue(null);
    kegs.save(k1);
    // 2) EMPTY but assigned
    Keg k2 = kegs.findByBreweryId(stone.getId()).stream().skip(1).findFirst().orElseThrow();
    k2.setStatus(KegStatus.EMPTY);
    k2.setAssignedVenue(v);
    kegs.save(k2);

    mvc.perform(
            get("/api/v1/keg-inventory/reconciliation")
                .with(user(principalFor(stone)))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ReceivedNoVenue")))
        .andExpect(content().string(containsString("EmptyButAssigned")))
        .andExpect(content().string(not(containsString("TappedWithoutPlacement"))));
  }
}
