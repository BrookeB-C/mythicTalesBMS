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
class AdminBreweryReturnedAndCleanUiSmokeTest {

  @Autowired MockMvc mvc;
  private MockHttpSession session;

  @BeforeEach
  void loginAsBrewAdmin() throws Exception {
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
  void returnedTabRendersAndKegsTabHasMarkClean() throws Exception {
    // Returned Kegs tab should render the heading even if empty
    mvc.perform(get("/admin/brewery").param("tab", "returned").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Returned Kegs")));

    // Kegs tab should include Mark Clean action in the row actions
    mvc.perform(get("/admin/brewery").param("tab", "kegs").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Mark Clean")));
  }
}
