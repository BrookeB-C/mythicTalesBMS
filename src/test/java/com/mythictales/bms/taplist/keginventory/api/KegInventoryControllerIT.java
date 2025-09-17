package com.mythictales.bms.taplist.keginventory.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.domain.VenueType;
import com.mythictales.bms.taplist.keginventory.api.dto.AssignRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReceiveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReturnRequest;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;
import com.mythictales.bms.taplist.security.CurrentUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KegInventoryControllerIT {

  @Autowired MockMvc mvc;
  @Autowired KegRepository kegs;
  @Autowired VenueRepository venues;
  @Autowired BreweryRepository breweries;

  private final ObjectMapper om = new ObjectMapper();

  private CurrentUser principalFor(Brewery b) {
    UserAccount ua = new UserAccount("ituser", "pw", Role.BREWERY_ADMIN);
    ua.setBrewery(b);
    return new CurrentUser(ua);
  }

  @Test
  void assign_receive_return_happy_path() throws Exception {
    Brewery stone =
        breweries.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("no brewery"));
    Venue venue =
        venues
            .findFirstByBreweryIdAndType(stone.getId(), VenueType.TAPROOM)
            .orElseThrow(() -> new RuntimeException("no venue"));
    Keg keg =
        kegs
            .findByBreweryIdAndAssignedVenueIsNullAndStatus(stone.getId(), KegStatus.FILLED)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("no FILLED unassigned keg"));

    // Assign
    String assignBody = om.writeValueAsString(new AssignRequest(keg.getId(), venue.getId()));
    mvc.perform(
            post("/api/v1/keg-inventory/assign")
                .with(user(principalFor(stone)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.assignedVenueId", is(venue.getId().intValue())))
        .andExpect(jsonPath("$.status", is("DISTRIBUTED")));

    // Receive
    String receiveBody = om.writeValueAsString(new ReceiveRequest(keg.getId(), venue.getId()));
    mvc.perform(
            post("/api/v1/keg-inventory/receive")
                .with(user(principalFor(stone)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiveBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.assignedVenueId", is(venue.getId().intValue())))
        .andExpect(jsonPath("$.status", is("RECEIVED")));

    // Return
    String returnBody = om.writeValueAsString(new ReturnRequest(keg.getId()));
    mvc.perform(
            post("/api/v1/keg-inventory/return")
                .with(user(principalFor(stone)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(returnBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.assignedVenueId").doesNotExist())
        .andExpect(jsonPath("$.status", is("EMPTY")));
  }

  @Test
  void move_same_venue_yields_422() throws Exception {
    Brewery stone =
        breweries.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("no brewery"));
    Venue venue =
        venues
            .findFirstByBreweryIdAndType(stone.getId(), VenueType.TAPROOM)
            .orElseThrow(() -> new RuntimeException("no venue"));
    Keg keg =
        kegs
            .findByBreweryIdAndAssignedVenueIsNullAndStatus(stone.getId(), KegStatus.FILLED)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("no FILLED unassigned keg"));

    // Assign first to ensure it's at venue
    String assignBody = om.writeValueAsString(new AssignRequest(keg.getId(), venue.getId()));
    mvc.perform(
            post("/api/v1/keg-inventory/assign")
                .with(user(principalFor(stone)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignBody))
        .andExpect(status().isOk());

    // Move with same from/to must 422
    String moveBody =
        om.writeValueAsString(
            new com.mythictales.bms.taplist.keginventory.api.dto.MoveRequest(
                keg.getId(), venue.getId(), venue.getId()));
    mvc.perform(
            post("/api/v1/keg-inventory/move")
                .with(user(principalFor(stone)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(moveBody))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.status", is(422)));
  }

  @Test
  void assign_forbidden_when_wrong_brewery() throws Exception {
    // Seed a different brewery for scope mismatch
    Brewery other = breweries.save(new Brewery("Other Brewery"));
    Brewery stone =
        breweries.findAll().stream()
            .filter(b -> !b.getId().equals(other.getId()))
            .findFirst()
            .orElseThrow();
    Venue venue =
        venues
            .findFirstByBreweryIdAndType(stone.getId(), VenueType.TAPROOM)
            .orElseThrow(() -> new RuntimeException("no venue"));
    Keg keg =
        kegs
            .findByBreweryIdAndAssignedVenueIsNullAndStatus(stone.getId(), KegStatus.FILLED)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("no FILLED unassigned keg"));

    String body = om.writeValueAsString(new AssignRequest(keg.getId(), venue.getId()));
    mvc.perform(
            post("/api/v1/keg-inventory/assign")
                .with(user(principalFor(other)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status", is(403)));
  }
}
