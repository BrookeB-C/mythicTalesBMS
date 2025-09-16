package com.mythictales.bms.taplist;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;

@Configuration
@Profile("test")
public class TestDataInitializer {

  @Bean
  CommandLineRunner seedForTests(
      BreweryRepository breweries,
      TaproomRepository taprooms,
      VenueRepository venues,
      BeerRepository beers,
      KegRepository kegs,
      TapRepository taps,
      KegSizeSpecRepository sizes) {
    return args -> {
      // Ensure a Stone Brewing brewery exists
      Brewery stone =
          breweries.findAll().stream()
              .filter(b -> "Stone Brewing".equalsIgnoreCase(b.getName()))
              .findFirst()
              .orElseGet(() -> breweries.save(new Brewery("Stone Brewing")));

      // Ensure a taproom + venue exists
      Taproom tr =
          taprooms.findAll().stream()
              .filter(t -> t.getBrewery() != null && stone.getId().equals(t.getBrewery().getId()))
              .findFirst()
              .orElseGet(() -> taprooms.save(new Taproom("Stone Taproom Test", stone)));

      Venue ven =
          venues.findAll().stream()
              .filter(
                  v ->
                      v.getBrewery() != null
                          && stone.getId().equals(v.getBrewery().getId())
                          && v.getType() == VenueType.TAPROOM)
              .findFirst()
              .orElseGet(
                  () -> venues.save(new Venue("Stone Taproom Test", VenueType.TAPROOM, stone)));

      if (taps.findByTaproomId(tr.getId()).isEmpty()) {
        Tap t = new Tap(1);
        t.setTaproom(tr);
        t.setVenue(ven);
        taps.save(t);
      }

      // Ensure a Beer exists
      Beer testBeer =
          beers.findAll().stream()
              .findFirst()
              .orElseGet(() -> beers.save(new Beer("Test Pale", "Pale Ale", 5.5)));

      // Ensure sizes exist
      KegSizeSpec sixtel =
          sizes.findByCode("SIXTEL").orElseGet(() -> sizes.save(new KegSizeSpec("SIXTEL", 5.2)));

      // Ensure multiple unassigned FILLED kegs for Stone exist to avoid test flakiness
      var existingUnassigned =
          kegs.findByBreweryIdAndAssignedVenueIsNullAndStatus(stone.getId(), KegStatus.FILLED);
      int desiredUnassignedFilled = 3;
      int toCreate = Math.max(0, desiredUnassignedFilled - existingUnassigned.size());
      for (int i = 0; i < toCreate; i++) {
        Keg k = new Keg(testBeer, sixtel);
        k.setBrewery(stone);
        k.setSerialNumber(String.format("TEST-STN-UNASSIGNED-%04d", i + 1));
        k.setStatus(KegStatus.FILLED);
        kegs.save(k);
      }

      // Debug: log seeded counts for visibility in test output
      int unassignedFilledCount =
          kegs.findByBreweryIdAndAssignedVenueIsNullAndStatus(stone.getId(), KegStatus.FILLED)
              .size();
      System.out.println(
          "[TestDataInitializer] Unassigned FILLED Stone kegs: " + unassignedFilledCount);

      // Add one RECEIVED keg assigned to the taproom venue for taproom actions
      boolean hasInbound =
          !kegs.findByAssignedVenueIdAndStatus(ven.getId(), KegStatus.RECEIVED).isEmpty();
      if (!hasInbound) {
        Keg k = new Keg(testBeer, sixtel);
        k.setBrewery(stone);
        k.setSerialNumber("TEST-STN-INBOUND-0001");
        k.setStatus(KegStatus.RECEIVED);
        k.setAssignedVenue(ven);
        kegs.save(k);
      }
    };
  }
}
