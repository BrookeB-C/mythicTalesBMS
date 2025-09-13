package com.mythictales.bms.taplist.smoke;

import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminBrewerySmokeTest {

    @Autowired MockMvc mvc;
    @Autowired BreweryRepository breweries;
    @Autowired KegRepository kegs;
    @Autowired VenueRepository venues;

    private MockHttpSession session;

    @BeforeEach
    void loginAsStoneBrewAdmin() throws Exception {
        MvcResult res = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "stone_brewadmin")
                .param("password", "password")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andReturn();
        session = (MockHttpSession) res.getRequest().getSession(false);
        assertThat(session).isNotNull();
    }

    @Test
    void sectionsRender() throws Exception {
        // Default view shows brewery info and taprooms tab by default
        mvc.perform(get("/admin/brewery").session(session))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Brewery Admin")))
           .andExpect(content().string(containsString("Save")))
           .andExpect(content().string(containsString("Your Taprooms")));

        // Kegs tab shows unassigned section
        mvc.perform(get("/admin/brewery").param("tab","kegs").session(session))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Kegs (Unassigned at Brewery)")));

        // Assigned tab shows assigned section
        mvc.perform(get("/admin/brewery").param("tab","assigned").session(session))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Assigned Kegs (Sent to Venues)")));

        // Users tab shows Users section
        mvc.perform(get("/admin/brewery").param("tab","users").session(session))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Users")));
    }

    @Test
    void distributeAndReturnKegFlow() throws Exception {
        Brewery stone = breweries.findAll().stream()
                .filter(b -> b.getName().equals("Stone Brewing"))
                .findFirst().orElseThrow();

        // Find a FILLED, unassigned keg
        Keg keg = kegs.findByBreweryIdAndAssignedVenueIsNullAndStatus(stone.getId(), KegStatus.FILLED)
                .stream().findFirst().orElseThrow();

        String serial = keg.getSerialNumber();

        // Pick a Stone venue to distribute to
        Long venueId = venues.findByBreweryId(stone.getId()).stream()
                .findFirst().orElseThrow().getId();

        // Distribute
        mvc.perform(post("/admin/brewery/kegs/{id}/distribute", keg.getId())
                .param("venueId", String.valueOf(venueId))
                .with(csrf())
                .session(session))
            .andExpect(status().is3xxRedirection());

        // Verify assigned list (Assigned tab) shows the keg serial
        mvc.perform(get("/admin/brewery").param("tab","assigned").session(session))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString(serial)));

        // Return
        mvc.perform(post("/admin/brewery/kegs/{id}/return", keg.getId())
                .with(csrf())
                .session(session))
           .andExpect(status().is3xxRedirection());

        // Verify serial appears again on the unassigned section (and status updated)
        mvc.perform(get("/admin/brewery").param("tab","kegs").session(session))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString(serial)))
           .andExpect(content().string(containsString("EMPTY")));
    }
}
