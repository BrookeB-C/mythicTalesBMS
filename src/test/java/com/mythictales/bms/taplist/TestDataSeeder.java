package com.mythictales.bms.taplist;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;

@org.springframework.context.annotation.Configuration
@Profile("test")
public class TestDataSeeder {

  @Bean
  CommandLineRunner seed(
      BreweryRepository breweries,
      TaproomRepository taprooms,
      VenueRepository venues,
      TapRepository taps,
      BeerRepository beers,
      KegRepository kegs,
      KegSizeSpecRepository sizes,
      UserAccountRepository users,
      PasswordEncoder encoder) {
    return args -> {
      // Size specs
      KegSizeSpec half = sizes.save(new KegSizeSpec("HALF_BARREL", 15.5));

      // Brewery + taproom + venue
      Brewery stone = breweries.save(new Brewery("Stone Brewing"));
      Taproom tr = taprooms.save(new Taproom("Stone Taproom", stone));
      Venue vTaproom = venues.save(new Venue("Stone Taproom", VenueType.TAPROOM, stone));

      // Users: site admin, brewery admin, taproom admin
      users.save(new UserAccount("siteadmin", encoder.encode("password"), Role.SITE_ADMIN));
      UserAccount brew =
          new UserAccount("stone_brewadmin", encoder.encode("password"), Role.BREWERY_ADMIN);
      brew.setBrewery(stone);
      users.save(brew);
      UserAccount tapadmin =
          new UserAccount("tapadmin", encoder.encode("password"), Role.TAPROOM_ADMIN);
      tapadmin.setTaproom(tr);
      users.save(tapadmin);

      // Beer + Keg (FILLED, unassigned)
      Beer ipa = beers.save(new Beer("Test IPA", "IPA", 6.5));
      Keg keg = new Keg(ipa, half);
      keg.setBrewery(stone);
      keg.setSerialNumber("STN-00001");
      keg.setStatus(KegStatus.FILLED);
      kegs.save(keg);

      // One Tap linked to taproom + venue (needed by venue admin tests)
      Tap t1 = new Tap(1);
      t1.setTaproom(tr);
      t1.setVenue(vTaproom);
      taps.save(t1);
    };
  }
}
