package com.mythictales.bms.taplist.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.domain.Tap;

@Component
public class AccessPolicy {

  public void ensureCanReadTap(CurrentUser user, Tap tap) {
    if (user == null) throw new AccessDeniedException("Unauthenticated");
    if (user.getRole() == Role.SITE_ADMIN) return;

    Long barId = tap.getBar() != null ? tap.getBar().getId() : null;
    Long taproomId = tap.getTaproom() != null ? tap.getTaproom().getId() : null;
    Long breweryId =
        tap.getVenue() != null && tap.getVenue().getBrewery() != null
            ? tap.getVenue().getBrewery().getId()
            : null;

    switch (user.getRole()) {
      case BAR_ADMIN -> {
        if (user.getBarId() == null || !user.getBarId().equals(barId))
          throw new AccessDeniedException("Forbidden");
      }
      case TAPROOM_ADMIN, TAPROOM_USER -> {
        if (user.getTaproomId() == null || !user.getTaproomId().equals(taproomId))
          throw new AccessDeniedException("Forbidden");
      }
      case BREWERY_ADMIN -> {
        if (user.getBreweryId() == null || !user.getBreweryId().equals(breweryId))
          throw new AccessDeniedException("Forbidden");
      }
      default -> throw new AccessDeniedException("Forbidden");
    }
  }

  public void ensureCanWriteTap(CurrentUser user, Tap tap) {
    // same scoping as read for now
    ensureCanReadTap(user, tap);
  }
}

