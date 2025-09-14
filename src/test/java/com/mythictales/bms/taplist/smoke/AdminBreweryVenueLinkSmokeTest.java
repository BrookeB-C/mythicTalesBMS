package com.mythictales.bms.taplist.smoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
class AdminBreweryVenueLinkSmokeTest {

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
  void taproomAdminLinkOpensTaproomAdmin() throws Exception {
    // Load brewery admin page
    MvcResult breweryPage =
        mvc.perform(get("/admin/brewery").session(session)).andExpect(status().isOk()).andReturn();

    String html = breweryPage.getResponse().getContentAsString();

    // Extract all /admin/taproom?taproomId= links present
    List<String> links = extractTaproomAdminLinks(html);
    assertThat(links).isNotEmpty();

    // Visit the first taproom admin link
    String href = links.get(0);
    String query = href.substring(href.indexOf('?'));
    mvc.perform(get("/admin/taproom" + query).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Taproom Admin")))
        .andExpect(content().string(containsString("Back to Brewery Admin")))
        .andReturn();

    // Verify Kegs tab chips/badges render
    mvc.perform(get("/admin/taproom" + query).param("tab", "kegs").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Status:")))
        .andExpect(content().string(containsString("class=\" chip")))
        .andExpect(content().string(containsString("?tab=kegs&amp;status=")))
        .andExpect(content().string(containsString("class=\"badge")));

    // Verify Inbound and Blown tabs render headings
    mvc.perform(get("/admin/taproom" + query).param("tab", "inbound").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Inbound Kegs")));
    mvc.perform(get("/admin/taproom" + query).param("tab", "blown").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Blown Kegs")));
  }

  private static List<String> extractTaproomAdminLinks(String html) {
    List<String> links = new ArrayList<>();
    Pattern p = Pattern.compile("/admin/taproom\\?taproomId=\\d+");
    Matcher m = p.matcher(html);
    while (m.find()) {
      links.add(m.group());
    }
    return links;
  }
}
