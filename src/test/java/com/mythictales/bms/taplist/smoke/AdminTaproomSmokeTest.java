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
class AdminTaproomSmokeTest {

  @Autowired MockMvc mvc;
  @Autowired UserAccountRepository users;
  @Autowired TapRepository taps;
  @Autowired KegRepository kegs;

  private MockHttpSession loginSession;

  @BeforeEach
  void loginAsTapAdmin() throws Exception {
    // Perform form login to establish a session with the seeded user
    MvcResult res =
        mvc.perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "tapadmin")
                    .param("password", "password")
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andReturn();
    loginSession = (MockHttpSession) res.getRequest().getSession(false);
    assertThat(loginSession).isNotNull();
  }

  @Test
  void adminTaproomPageLoads() throws Exception {
    mvc.perform(get("/admin/taproom").session(loginSession)).andExpect(status().isOk());
  }

  @Test
  void taproomKegsTabShowsChipsAndBadges() throws Exception {
    mvc.perform(get("/admin/taproom").param("tab", "kegs").session(loginSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Status:")))
        .andExpect(content().string(containsString("class=\" chip")))
        .andExpect(content().string(containsString("?tab=kegs&amp;status=")))
        .andExpect(content().string(containsString("class=\"badge")));
  }

  @Test
  void taproomInboundTabShowsList() throws Exception {
    mvc.perform(get("/admin/taproom").param("tab", "inbound").session(loginSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Inbound Kegs")));
  }

  @Test
  void taproomTaplistTabShowsTable() throws Exception {
    mvc.perform(get("/admin/taproom").param("tab", "taplist").session(loginSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Your Taplist")))
        .andExpect(content().string(containsString("Tap #")))
        .andExpect(content().string(containsString("Actions")));
  }

  @Test
  void taproomEventsTabShowsTableOrEmpty() throws Exception {
    mvc.perform(get("/admin/taproom").param("tab", "events").session(loginSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Events")));
  }

  @Test
  void tappingPouringAndBlowingCreatesEvents() throws Exception {
    // Resolve taproom and a tap for the logged-in user
    UserAccount ua = users.findByUsername("tapadmin").orElseThrow();
    Long taproomId = ua.getTaproom().getId();
    List<Tap> tapList = taps.findByTaproomId(taproomId);
    assertThat(tapList).isNotEmpty();
    Long tapId = tapList.get(0).getId();

    // Find an available keg to tap (FILLED in inventory)
    Keg keg = kegs.findByStatus(KegStatus.FILLED).stream().findFirst().orElseThrow();

    // Tap the keg
    mvc.perform(
            post("/taps/{id}/tapKeg", tapId)
                .param("kegId", String.valueOf(keg.getId()))
                .with(csrf())
                .session(loginSession))
        .andExpect(status().is3xxRedirection());

    // Pour 16 oz
    mvc.perform(
            post("/taps/{id}/pour", tapId).param("ounces", "16").with(csrf()).session(loginSession))
        .andExpect(status().is3xxRedirection());

    // Blow the keg
    mvc.perform(post("/taps/{id}/blow", tapId).with(csrf()).session(loginSession))
        .andExpect(status().is3xxRedirection());

    // Verify events page renders and includes types
    Long venueId = tapList.get(0).getVenue() != null ? tapList.get(0).getVenue().getId() : null;
    assertThat(venueId).as("Venue should be set on tap").isNotNull();
    mvc.perform(get("/admin/venue/{venueId}/events", venueId).session(loginSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("TAP")))
        .andExpect(content().string(containsString("POUR")))
        .andExpect(content().string(containsString("BLOW")));
  }

  @Test
  void lastEventBadgeUpdatesAfterActions() throws Exception {
    // Arrange: pick a tap in the taproom
    UserAccount ua = users.findByUsername("tapadmin").orElseThrow();
    Long taproomId = ua.getTaproom().getId();
    List<Tap> tapList = taps.findByTaproomId(taproomId);
    assertThat(tapList).isNotEmpty();
    Long tapId = tapList.get(0).getId();

    // Ensure the tap has a keg; if empty, tap an untapped keg
    if (tapList.get(0).getKeg() == null) {
      Keg keg =
          kegs.findByStatus(KegStatus.FILLED).stream()
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("No untapped kegs available for test"));
      mvc.perform(
              post("/taps/{id}/tapKeg", tapId)
                  .param("kegId", String.valueOf(keg.getId()))
                  .with(csrf())
                  .session(loginSession))
          .andExpect(status().is3xxRedirection());
    }

    // Act: pour to create a new most-recent event
    mvc.perform(
            post("/taps/{id}/pour", tapId).param("ounces", "8").with(csrf()).session(loginSession))
        .andExpect(status().is3xxRedirection());

    // Assert: admin taproom shows a last-event badge with POUR
    mvc.perform(get("/admin/taproom").session(loginSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Last event")))
        .andExpect(content().string(containsString("POUR")));
  }

  @Test
  void eventsPageShowsNewestFirst() throws Exception {
    // Arrange: pick a tap and ensure we have an untapped keg
    UserAccount ua = users.findByUsername("tapadmin").orElseThrow();
    Long taproomId = ua.getTaproom().getId();
    List<Tap> tapList = taps.findByTaproomId(taproomId);
    assertThat(tapList).isNotEmpty();
    Long tapId = tapList.get(0).getId();

    Keg keg =
        kegs.findByStatus(KegStatus.FILLED).stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No untapped kegs available for test"));

    // Act: create a clear sequence of events for ordering
    mvc.perform(
            post("/taps/{id}/tapKeg", tapId)
                .param("kegId", String.valueOf(keg.getId()))
                .with(csrf())
                .session(loginSession))
        .andExpect(status().is3xxRedirection());

    mvc.perform(
            post("/taps/{id}/pour", tapId).param("ounces", "8").with(csrf()).session(loginSession))
        .andExpect(status().is3xxRedirection());

    mvc.perform(post("/taps/{id}/blow", tapId).with(csrf()).session(loginSession))
        .andExpect(status().is3xxRedirection());

    // Assert: events page shows BLOW, then POUR, then TAP (newest first)
    Long venueId = tapList.get(0).getVenue() != null ? tapList.get(0).getVenue().getId() : null;
    assertThat(venueId).isNotNull();

    MvcResult res =
        mvc.perform(get("/admin/venue/{venueId}/events", venueId).session(loginSession))
            .andExpect(status().isOk())
            .andReturn();

    String html = res.getResponse().getContentAsString();
    int iBlow = html.indexOf(">BLOW<");
    int iPour = html.indexOf(">POUR<");
    int iTap = html.indexOf(">TAP<");
    assertThat(iBlow).as("BLOW should be first").isGreaterThanOrEqualTo(0);
    assertThat(iPour).as("POUR should be present").isGreaterThan(0);
    assertThat(iTap).as("TAP should be present").isGreaterThan(0);
    assertThat(iBlow).isLessThan(iPour);
    assertThat(iPour).isLessThan(iTap);
  }
}
