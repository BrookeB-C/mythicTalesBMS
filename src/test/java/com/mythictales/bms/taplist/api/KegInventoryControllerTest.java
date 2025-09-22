package com.mythictales.bms.taplist.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isNull;

import com.mythictales.bms.taplist.api.dto.KegInventorySummaryDto;
import com.mythictales.bms.taplist.api.dto.KegInventorySummaryDto.QueueItem;
import com.mythictales.bms.taplist.domain.Beer;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegEvent;
import com.mythictales.bms.taplist.domain.KegEventType;
import com.mythictales.bms.taplist.domain.KegPlacement;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.domain.VenueType;
import com.mythictales.bms.taplist.keginventory.api.KegInventoryController;
import com.mythictales.bms.taplist.keginventory.repo.KegMovementHistoryRepository;
import com.mythictales.bms.taplist.keginventory.service.KegInventoryService;
import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.KegPlacementRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

class KegInventoryControllerTest {

  @Mock private KegInventoryService kegInventoryService;
  @Mock private KegMovementHistoryRepository historyRepository;
  @Mock private KegRepository kegRepository;
  @Mock private KegPlacementRepository placementRepository;
  @Mock private KegEventRepository kegEventRepository;

  @InjectMocks private KegInventoryController controller;

  private Brewery brewery;
  private CurrentUser breweryAdmin;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    brewery = new Brewery("Mythic Brewery");
    brewery.setId(42L);

    UserAccount account = new UserAccount("brewadmin", "pw", Role.BREWERY_ADMIN);
    account.setId(100L);
    account.setBrewery(brewery);
    breweryAdmin = new CurrentUser(account);
  }

  @Test
  void summaryReturnsAggregatedDataForBreweryAdmin() {
    Keg unassigned = keg("K-100", KegStatus.CLEAN);
    Keg unassigned2 = keg("K-101", KegStatus.FILLED);

    Venue assignedVenue = venue("Downtown Taproom");
    Keg assigned = keg("K-200", KegStatus.DISTRIBUTED);
    assigned.setAssignedVenue(assignedVenue);

    Keg returned = keg("K-300", KegStatus.RETURNED);

    when(kegRepository.findByBreweryIdAndAssignedVenueIsNull(42L))
        .thenReturn(List.of(unassigned, unassigned2, returned));
    when(kegRepository.findByBreweryIdAndAssignedVenueIsNotNull(42L))
        .thenReturn(List.of(assigned));
    when(kegRepository.findByBreweryIdAndStatus(42L, KegStatus.RETURNED))
        .thenReturn(List.of(returned));

    KegEvent event = event(assigned, assignedVenue);
    Page<KegEvent> events = new PageImpl<>(List.of(event), PageRequest.of(0, 5), 1);
    when(kegEventRepository.findEventsFiltered(eq(42L), isNull(), any(PageRequest.class)))
        .thenReturn(events);

    KegInventorySummaryDto summary =
        controller.summary(breweryAdmin, null, /*queueSize*/ 5, /*activitySize*/ 3);

    assertEquals(2, summary.hero().availableKegs());
    assertEquals(1, summary.hero().distributedKegs());
    assertEquals(1, summary.hero().returnedKegs());
    assertEquals("K-100", summary.queue().get(0).serialNumber());
    QueueItem distributedItem =
        summary.queue().stream().filter(q -> "warning".equals(q.severity())).findFirst().orElseThrow();
    assertEquals("Distributed", distributedItem.status());
    assertTrue(summary.activity().get(0).summary().contains("K-200"));
    assertEquals(4, summary.quickActions().size());
    var assignAction = summary.quickActions().get(0);
    assertEquals("assign", assignAction.command().type());
    assertEquals(true, assignAction.command().requiresVenue());
  }

  @Test
  void siteAdminRequiresBreweryId() {
    UserAccount site = new UserAccount("site", "pw", Role.SITE_ADMIN);
    site.setId(1L);
    CurrentUser siteUser = new CurrentUser(site);
    assertThrows(AccessDeniedException.class, () -> controller.summary(siteUser, null, 5, 5));
  }

  @Test
  void breweryAdminCannotAccessOtherBrewery() {
    assertThrows(
        AccessDeniedException.class, () -> controller.summary(breweryAdmin, 99L, 5, 5));
  }

  private Keg keg(String serial, KegStatus status) {
    Keg keg = new Keg();
    keg.setId(Math.abs(serial.hashCode()) * 1L);
    keg.setSerialNumber(serial);
    keg.setStatus(status);
    Beer beer = new Beer("Hazy IPA", "IPA", 6.5);
    beer.setBrewery(brewery);
    keg.setBeer(beer);
    keg.setBrewery(brewery);
    return keg;
  }

  private Venue venue(String name) {
    Venue v = new Venue(name, VenueType.TAPROOM, brewery);
    v.setId(Math.abs(name.hashCode()) * 1L);
    return v;
  }

  private KegEvent event(Keg keg, Venue venue) {
    Tap tap = new Tap(3);
    tap.setId(88L);
    tap.setVenue(venue);

    KegPlacement placement = new KegPlacement();
    placement.setTap(tap);
    placement.setKeg(keg);

    KegEvent event = new KegEvent();
    event.setPlacement(placement);
    event.setType(KegEventType.POUR);
    event.setOunces(14.0);
    event.setAtTime(Instant.parse("2024-01-01T12:00:00Z"));
    return event;
  }
}
