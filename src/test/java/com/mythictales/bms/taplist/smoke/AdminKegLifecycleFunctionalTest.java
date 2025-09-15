package com.mythictales.bms.taplist.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.UserAccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AdminKegLifecycleFunctionalTest {

  @Autowired MockMvc mvc;
  @Autowired UserAccountRepository users;
  @Autowired TapRepository taps;
  @Autowired KegRepository kegs;

  private MockHttpSession brewSession;
  private MockHttpSession tapSession;

  @BeforeEach
  void loginSessions() throws Exception {
    // Brewery admin (Stone)
    MvcResult res1 =
        mvc.perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "stone_brewadmin")
                    .param("password", "password")
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andReturn();
    brewSession = (MockHttpSession) res1.getRequest().getSession(false);
    assertThat(brewSession).isNotNull();

    // Taproom admin
    MvcResult res2 =
        mvc.perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "tapadmin")
                    .param("password", "password")
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andReturn();
    tapSession = (MockHttpSession) res2.getRequest().getSession(false);
    assertThat(tapSession).isNotNull();
  }

  @Test
  void fullKegLifecycle_Distribute_Tap_Pour_Blow_Return_Clean() throws Exception {
    // Resolve taproom and an empty tap
    UserAccount tapAdmin = users.findByUsername("tapadmin").orElseThrow();
    Long taproomId = tapAdmin.getTaproom().getId();
    List<Tap> tapList = taps.findByTaproomId(taproomId);
    assertThat(tapList).isNotEmpty();
    Long tapId = tapList.get(0).getId();

    // Make sure the tap is empty
    if (tapList.get(0).getKeg() != null) {
      mvc.perform(post("/taps/{id}/blow", tapId).with(csrf()).session(tapSession))
          .andExpect(status().is3xxRedirection());
    }

    // Determine venue id from the tap
    Long venueId =
        taps.findByTaproomId(taproomId).stream().findFirst().orElseThrow().getVenue().getId();

    // Choose a FILLED, unassigned Stone keg
    Long stoneId = users.findByUsername("stone_brewadmin").orElseThrow().getBrewery().getId();
    Keg keg =
        kegs.findByBreweryIdAndAssignedVenueIsNullAndStatus(stoneId, KegStatus.FILLED).stream()
            .findFirst()
            .orElseThrow();
    String serial = keg.getSerialNumber();

    // Distribute to the taproom's venue
    mvc.perform(
            post("/admin/brewery/kegs/{id}/distribute", keg.getId())
                .session(brewSession)
                .param("venueId", String.valueOf(venueId))
                .with(csrf()))
        .andExpect(status().is3xxRedirection());

    // Taproom receives the keg
    mvc.perform(
            post("/admin/taproom/kegs/{id}/receive", keg.getId()).session(tapSession).with(csrf()))
        .andExpect(status().is3xxRedirection());

    // Tap the keg onto the tap
    mvc.perform(
            post("/taps/{id}/tapKeg", tapId)
                .param("kegId", String.valueOf(keg.getId()))
                .with(csrf())
                .session(tapSession))
        .andExpect(status().is3xxRedirection());

    // Pour a large amount to empty it (forces BLOWN status via service logic)
    mvc.perform(
            post("/taps/{id}/pour", tapId).param("ounces", "3000").with(csrf()).session(tapSession))
        .andExpect(status().is3xxRedirection());

    // Explicitly blow (no-op if already empty)
    mvc.perform(post("/taps/{id}/blow", tapId).with(csrf()).session(tapSession))
        .andExpect(status().is3xxRedirection());

    // Verify appears in Blown Kegs tab
    mvc.perform(get("/admin/taproom").param("tab", "blown").session(tapSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Blown Kegs")))
        .andExpect(content().string(containsString(serial)));

    // Return to brewery from taproom
    mvc.perform(
            post("/admin/taproom/kegs/{id}/return", keg.getId()).with(csrf()).session(tapSession))
        .andExpect(status().is3xxRedirection());

    // Brewery sees it under Returned Kegs
    mvc.perform(get("/admin/brewery").param("tab", "returned").session(brewSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Returned Kegs")))
        .andExpect(content().string(containsString(serial)));

    // Reset to EMPTY
    mvc.perform(
            post("/admin/brewery/kegs/{id}/return", keg.getId()).with(csrf()).session(brewSession))
        .andExpect(status().is3xxRedirection());

    // Mark CLEAN
    mvc.perform(
            post("/admin/brewery/kegs/{id}/clean", keg.getId()).with(csrf()).session(brewSession))
        .andExpect(status().is3xxRedirection());

    // Verify visible under Kegs tab when filtering CLEAN
    mvc.perform(
            get("/admin/brewery")
                .param("tab", "kegs")
                .param("status", "CLEAN")
                .session(brewSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(serial)))
        .andExpect(content().string(containsString("CLEAN")));
  }
}
