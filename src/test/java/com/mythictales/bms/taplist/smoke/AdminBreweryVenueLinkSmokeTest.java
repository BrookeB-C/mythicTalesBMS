package com.mythictales.bms.taplist.smoke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminBreweryVenueLinkSmokeTest {

    @Autowired MockMvc mvc;
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
    void taproomAdminLinkOpensCorrectVenueAndBackLinkWorks() throws Exception {
        // Load brewery admin page
        MvcResult breweryPage = mvc.perform(get("/admin/brewery").session(session))
                .andExpect(status().isOk())
                .andReturn();

        String html = breweryPage.getResponse().getContentAsString();

        // Extract all /admin/venue/{id} links present
        List<String> venueIds = extractVenueIds(html);
        assertThat(venueIds).isNotEmpty();

        boolean found = false;
        for (String id : venueIds) {
            MvcResult venuePage = mvc.perform(get("/admin/venue/{venueId}", id).session(session))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Venue Admin")))
                    .andExpect(content().string(containsString("/admin/brewery")))
                    .andReturn();
            String vhtml = venuePage.getResponse().getContentAsString();
            if (vhtml.contains("Stone Taproom 1")) {
                found = true;
                break;
            }
        }

        assertThat(found)
                .as("Expected at least one venue admin page to be for 'Stone Taproom 1'")
                .isTrue();
    }

    private static List<String> extractVenueIds(String html) {
        List<String> ids = new ArrayList<>();
        Pattern p = Pattern.compile("/admin/venue/([0-9]+)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            ids.add(m.group(1));
        }
        return ids;
    }
}

