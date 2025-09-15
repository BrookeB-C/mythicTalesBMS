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

@SpringBootTest
@AutoConfigureMockMvc
class AdminBreweryUiLookSmokeTest {

  @Autowired MockMvc mvc;
  private MockHttpSession session;

  @BeforeEach
  void loginAsStoneBrewAdmin() throws Exception {
    MvcResult res =
        mvc.perform(
                post("/login")
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
  void tabsRenderWithLinks() throws Exception {
    mvc.perform(get("/admin/brewery").session(session))
        .andExpect(status().isOk())
        // Brewery info always present
        .andExpect(content().string(containsString("Brewery Admin")))
        .andExpect(content().string(containsString("Save")))
        // Tabs below info: taprooms, kegs, assigned, returned, users
        .andExpect(content().string(containsString("?tab=taprooms")))
        .andExpect(content().string(containsString("?tab=kegs")))
        .andExpect(content().string(containsString("?tab=assigned")))
        .andExpect(content().string(containsString("?tab=returned")))
        .andExpect(content().string(containsString("?tab=users")));
  }

  @Test
  void kegsTabShowsChipsAndBadges() throws Exception {
    mvc.perform(get("/admin/brewery").param("tab", "kegs").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Status:")))
        .andExpect(content().string(containsString("class=\" chip")))
        .andExpect(content().string(containsString("?tab=kegs&amp;status=")))
        .andExpect(content().string(containsString("class=\"badge")))
        .andExpect(content().string(containsString("Distribute")));
  }

  @Test
  void taproomsTabShowsSearchTableAndAdminLinks() throws Exception {
    mvc.perform(get("/admin/brewery").param("tab", "taprooms").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Search taprooms...")))
        .andExpect(content().string(containsString("<th>Name</th>")))
        .andExpect(content().string(containsString("<th>Taps</th>")))
        .andExpect(content().string(containsString("<th>Active Kegs</th>")))
        .andExpect(content().string(containsString("/admin/taproom?taproomId=")));

    // Verify search parameter echoes and still renders list
    mvc.perform(get("/admin/brewery").param("tab", "taprooms").param("q", "Stone").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Stone")))
        .andExpect(content().string(containsString("Admin")));
  }

  @Test
  void usersTabRendersWithFilters() throws Exception {
    mvc.perform(get("/admin/brewery").param("tab", "users").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Users")))
        .andExpect(content().string(containsString("Filter by Venue")));
  }
}
