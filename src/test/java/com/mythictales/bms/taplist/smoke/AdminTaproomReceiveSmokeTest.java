package com.mythictales.bms.taplist.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.UserAccountRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AdminTaproomReceiveSmokeTest {

  @Autowired MockMvc mvc;
  @Autowired UserAccountRepository users;
  @Autowired TapRepository taps;
  @Autowired VenueRepository venues;
  @Autowired KegRepository kegs;

  private MockHttpSession brewSession;
  private MockHttpSession tapSession;

  @BeforeEach
  void loginSessions() throws Exception {
    // Stone brewery admin
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
  void distributeThenReceiveAppearsInTaproomLists() throws Exception {
    // Determine taproom venue ID for tapadmin
    UserAccount ua = users.findByUsername("tapadmin").orElseThrow();
    Long vid =
        taps.findByTaproomId(ua.getTaproom().getId()).stream()
            .findFirst()
            .orElseThrow()
            .getVenue()
            .getId();

    // Choose a FILLED, unassigned Stone keg
    Long stoneId = users.findByUsername("stone_brewadmin").orElseThrow().getBrewery().getId();
    Keg keg =
        kegs.findByBreweryIdAndAssignedVenueIsNullAndStatus(stoneId, KegStatus.FILLED).stream()
            .findFirst()
            .orElseThrow();
    String serial = keg.getSerialNumber();

    // Distribute to the taproom's venue (as stone brewer)
    mvc.perform(
            post("/admin/brewery/kegs/{id}/distribute", keg.getId())
                .session(brewSession)
                .param("venueId", String.valueOf(vid))
                .with(csrf()))
        .andExpect(status().is3xxRedirection());

    // Taproom sees it in inbound list (Inbound tab)
    mvc.perform(get("/admin/taproom").param("tab", "inbound").session(tapSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Inbound Kegs")))
        .andExpect(content().string(containsString(serial)));

    // Receive the keg
    mvc.perform(
            post("/admin/taproom/kegs/{id}/receive", keg.getId()).session(tapSession).with(csrf()))
        .andExpect(status().is3xxRedirection());

    // Now appears in Available Kegs (Received) on Kegs tab
    mvc.perform(get("/admin/taproom").param("tab", "kegs").session(tapSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Available Kegs")))
        .andExpect(content().string(containsString(serial)));
  }
}
