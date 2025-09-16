package com.mythictales.bms.taplist;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;

@Configuration
@Profile("staging")
public class StagingDataInitializer {
  @Bean
  CommandLineRunner initStagingData(
      BreweryRepository breweries,
      TaproomRepository taprooms,
      TapRepository taps,
      KegRepository kegs,
      UserAccountRepository users,
      PasswordEncoder encoder,
      KegSizeSpecRepository sizes) {
    return args -> {
      // Brewery
      Brewery br = breweries.save(new Brewery("MythicTales Brewing"));
      // Taproom
      Taproom tr = taprooms.save(new Taproom("MythicTales Taproom", br));
      // 6 Taps
      for (int i = 1; i <= 6; i++) {
        Tap tap = new Tap(i);
        tap.setTaproom(tr);
        taps.save(tap);
      }
      // 10 empty kegs
      KegSizeSpec half =
          sizes
              .findByCode("HALF_BARREL")
              .orElseGet(() -> sizes.save(new KegSizeSpec("HALF_BARREL", 15.5)));
      for (int i = 1; i <= 10; i++) {
        Keg keg = new Keg(null, half);
        keg.setBrewery(br);
        keg.setSerialNumber("MTB-EMPTY-" + i);
        keg.setRemainingOunces(0);
        keg.setStatus(KegStatus.EMPTY);
        kegs.save(keg);
      }
      // Users
      if (users.findByUsername("siteadmin").isEmpty()) {
        users.save(new UserAccount("siteadmin", encoder.encode("stgpassword"), Role.SITE_ADMIN));
      }
      if (users.findByUsername("brewadmin").isEmpty()) {
        UserAccount brewadmin =
            new UserAccount("brewadmin", encoder.encode("stgpassword"), Role.BREWERY_ADMIN);
        brewadmin.setBrewery(br);
        users.save(brewadmin);
      }
      if (users.findByUsername("tapadmin").isEmpty()) {
        UserAccount tapadmin =
            new UserAccount("tapadmin", encoder.encode("stgpassword"), Role.TAPROOM_ADMIN);
        tapadmin.setTaproom(tr);
        users.save(tapadmin);
      }
      if (users.findByUsername("tapuser").isEmpty()) {
        UserAccount tapuser =
            new UserAccount("tapuser", encoder.encode("stgpassword"), Role.TAPROOM_USER);
        tapuser.setTaproom(tr);
        users.save(tapuser);
      }
    };
  }
}
