package com.mythictales.bms.taplist.smoke;

import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminVenueSmokeTest {

    @Autowired MockMvc mvc;
    @Autowired UserAccountRepository users;
    @Autowired TapRepository taps;

    private MockHttpSession loginSession;

    @BeforeEach
    void loginAsTapAdmin() throws Exception {
        MvcResult res = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "tapadmin")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        loginSession = (MockHttpSession) res.getRequest().getSession(false);
        assertThat(loginSession).isNotNull();
    }

    private Long resolveVenueIdForTapadmin() {
        UserAccount ua = users.findByUsername("tapadmin").orElseThrow();
        List<Tap> tapList = taps.findByTaproomId(ua.getTaproom().getId());
        assertThat(tapList).isNotEmpty();
        assertThat(tapList.get(0).getVenue()).as("Tap should have venue set").isNotNull();
        return tapList.get(0).getVenue().getId();
    }

    @Test
    void venueAdminLoads() throws Exception {
        Long venueId = resolveVenueIdForTapadmin();
        mvc.perform(get("/admin/venue/{venueId}", venueId).session(loginSession))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Venue Admin")));
    }

    @Test
    void eventsBackLinkResolvesToVenueAdmin() throws Exception {
        Long venueId = resolveVenueIdForTapadmin();
        // Load events page and ensure it contains the Back to Venue Admin link
        mvc.perform(get("/admin/venue/{venueId}/events", venueId).session(loginSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/admin/venue/" + venueId)));

        // Now request the back link target and ensure it loads
        mvc.perform(get("/admin/venue/{venueId}", venueId).session(loginSession))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Venue Admin")));
    }
}
