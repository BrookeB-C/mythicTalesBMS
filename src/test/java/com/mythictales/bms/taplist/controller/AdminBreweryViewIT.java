package com.mythictales.bms.taplist.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminBreweryViewIT {

  @Autowired MockMvc mvc;

  @Test
  void brewery_kegs_tab_renders_ok() throws Exception {
    mvc.perform(
            get("/admin/brewery").param("tab", "kegs").with(user("brew").roles("BREWERY_ADMIN")))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Kegs (Unassigned at Brewery)")));
  }
}
