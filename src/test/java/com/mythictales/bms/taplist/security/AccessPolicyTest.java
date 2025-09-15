package com.mythictales.bms.taplist.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import com.mythictales.bms.taplist.domain.Bar;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.domain.Taproom;
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.domain.VenueType;

class AccessPolicyTest {

  private static Tap tapWith(Long breweryId, Long taproomId, Long barId) {
    Tap tap = new Tap(1);
    Brewery brewery = new Brewery("Brew");
    brewery.setId(breweryId);

    Venue venue = new Venue("Main Venue", VenueType.TAPROOM, brewery);
    tap.setVenue(venue);

    if (taproomId != null) {
      Taproom tr = new Taproom("TR", brewery);
      tr.setId(taproomId);
      tap.setTaproom(tr);
    }
    if (barId != null) {
      Bar bar = new Bar("BAR", brewery);
      bar.setId(barId);
      tap.setBar(bar);
    }
    return tap;
  }

  private static CurrentUser user(Role role, Long breweryId, Long taproomId, Long barId) {
    UserAccount ua = new UserAccount("u", "p", role);
    if (breweryId != null) {
      Brewery b = new Brewery("B");
      b.setId(breweryId);
      ua.setBrewery(b);
    }
    if (taproomId != null) {
      Taproom tr = new Taproom("TR", ua.getBrewery());
      tr.setId(taproomId);
      ua.setTaproom(tr);
    }
    if (barId != null) {
      Bar bar = new Bar("BAR", ua.getBrewery());
      bar.setId(barId);
      ua.setBar(bar);
    }
    return new CurrentUser(ua);
  }

  @Test
  void siteAdmin_can_read_and_write_any() {
    AccessPolicy p = new AccessPolicy();
    Tap tap = tapWith(1L, 10L, 100L);
    CurrentUser cu = user(Role.SITE_ADMIN, null, null, null);
    assertDoesNotThrow(() -> p.ensureCanReadTap(cu, tap));
    assertDoesNotThrow(() -> p.ensureCanWriteTap(cu, tap));
  }

  @Test
  void breweryAdmin_requires_matching_brewery() {
    AccessPolicy p = new AccessPolicy();
    Tap tap = tapWith(1L, 10L, 100L);
    assertDoesNotThrow(() -> p.ensureCanReadTap(user(Role.BREWERY_ADMIN, 1L, null, null), tap));
    assertThrows(
        AccessDeniedException.class,
        () -> p.ensureCanReadTap(user(Role.BREWERY_ADMIN, 2L, null, null), tap));
  }

  @Test
  void taproomAdmin_requires_matching_taproom() {
    AccessPolicy p = new AccessPolicy();
    Tap tap = tapWith(1L, 10L, null);
    assertDoesNotThrow(() -> p.ensureCanWriteTap(user(Role.TAPROOM_ADMIN, 1L, 10L, null), tap));
    assertThrows(
        AccessDeniedException.class,
        () -> p.ensureCanWriteTap(user(Role.TAPROOM_ADMIN, 1L, 11L, null), tap));
  }

  @Test
  void barAdmin_requires_matching_bar() {
    AccessPolicy p = new AccessPolicy();
    Tap tap = tapWith(1L, null, 100L);
    assertDoesNotThrow(() -> p.ensureCanWriteTap(user(Role.BAR_ADMIN, 1L, null, 100L), tap));
    assertThrows(
        AccessDeniedException.class,
        () -> p.ensureCanWriteTap(user(Role.BAR_ADMIN, 1L, null, 101L), tap));
  }
}
