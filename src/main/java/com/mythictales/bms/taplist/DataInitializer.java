package com.mythictales.bms.taplist;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;
import com.mythictales.bms.taplist.service.TapService;

@Configuration
@org.springframework.context.annotation.Profile("!test")
public class DataInitializer {
  @Bean
  CommandLineRunner initData(
      BreweryRepository breweries,
      TaproomRepository taprooms,
      BarRepository bars,
      BeerRepository beers,
      KegRepository kegs,
      TapRepository taps,
      UserAccountRepository users,
      PasswordEncoder encoder,
      VenueRepository venues,
      KegSizeSpecRepository sizes,
      TapService tapService) {
    return args -> {
      // Ensure size specs exist (gallons → typical US kegs)
      KegSizeSpec half =
          sizes
              .findByCode("HALF_BARREL")
              .orElseGet(() -> sizes.save(new KegSizeSpec("HALF_BARREL", 15.5)));
      KegSizeSpec quarter =
          sizes
              .findByCode("QUARTER_BARREL")
              .orElseGet(() -> sizes.save(new KegSizeSpec("QUARTER_BARREL", 7.75)));
      KegSizeSpec sixtel =
          sizes.findByCode("SIXTEL").orElseGet(() -> sizes.save(new KegSizeSpec("SIXTEL", 5.2)));

      // Seed a small baseline brewery, taproom and bar (existing demo)
      Brewery br = breweries.save(new Brewery("MythicTales Brewing"));
      Taproom tr = taprooms.save(new Taproom("MythicTales Taproom", br));
      Venue vTaproom = venues.save(new Venue("MythicTales Taproom", VenueType.TAPROOM, br));
      Bar bar = bars.save(new Bar("MythicTales Downtown Bar", br));
      Venue vBar = venues.save(new Venue("MythicTales Downtown Bar", VenueType.BAR, br));
      Tap tap1 = new Tap(1);
      tap1.setTaproom(tr);
      tap1.setVenue(vTaproom);
      taps.save(tap1);
      Tap tap2 = new Tap(2);
      tap2.setTaproom(tr);
      tap2.setVenue(vTaproom);
      taps.save(tap2);
      Tap tap3 = new Tap(3);
      tap3.setBar(bar);
      tap3.setVenue(vBar);
      taps.save(tap3);
      Beer ipa = beers.save(new Beer("Dragon's Breath IPA", "IPA", 6.8));
      Beer stout = beers.save(new Beer("Shadow Stout", "Stout", 8.0));
      Beer lager = beers.save(new Beer("Silver Lager", "Lager", 5.0));
      java.util.Map<Long, Integer> serialSeq = new java.util.HashMap<>();
      java.util.function.BiFunction<Brewery, String, String> nextSerial =
          (brewery, prefix) -> {
            int n = serialSeq.merge(brewery.getId(), 1, Integer::sum);
            return prefix + String.format("-%05d", n);
          };
      Keg k1 = new Keg(ipa, half);
      k1.setBrewery(br);
      k1.setSerialNumber(nextSerial.apply(br, "MTB"));
      k1.setStatus(KegStatus.FILLED);
      kegs.save(k1);
      Keg k2 = new Keg(stout, sixtel);
      k2.setBrewery(br);
      k2.setSerialNumber(nextSerial.apply(br, "MTB"));
      k2.setStatus(KegStatus.FILLED);
      kegs.save(k2);
      Keg k3 = new Keg(lager, quarter);
      k3.setBrewery(br);
      k3.setSerialNumber(nextSerial.apply(br, "MTB"));
      k3.setStatus(KegStatus.FILLED);
      kegs.save(k3);
      users.save(new UserAccount("siteadmin", encoder.encode("password"), Role.SITE_ADMIN));
      var brew = new UserAccount("brewadmin", encoder.encode("password"), Role.BREWERY_ADMIN);
      brew.setBrewery(br);
      users.save(brew);
      var baradmin = new UserAccount("baradmin", encoder.encode("password"), Role.BAR_ADMIN);
      baradmin.setBar(bar);
      users.save(baradmin);
      var tapadmin = new UserAccount("tapadmin", encoder.encode("password"), Role.TAPROOM_ADMIN);
      tapadmin.setTaproom(tr);
      users.save(tapadmin);
      var tapuser = new UserAccount("tapuser", encoder.encode("password"), Role.TAPROOM_USER);
      tapuser.setTaproom(tr);
      users.save(tapuser);

      // Helper lambdas
      java.util.function.Function<Integer, Double> abvFor =
          idx -> 4.2 + (idx % 7) * 0.6; // vary ABV
      java.util.function.Function<Integer, KegSizeSpec> sizeFor =
          idx ->
              switch (idx % 3) {
                case 0 -> half;
                case 1 -> quarter;
                default -> sixtel;
              };

      // Builder helpers
      java.util.function.BiFunction<Brewery, String[], java.util.List<Beer>> createBeers =
          (brewery, names) -> {
            java.util.List<Beer> out = new java.util.ArrayList<>();
            for (int i = 0; i < names.length; i++) {
              String n = names[i];
              Beer brr =
                  new Beer(
                      n, i % 2 == 0 ? "IPA" : (i % 3 == 0 ? "Lager" : "Stout"), abvFor.apply(i));
              out.add(beers.save(brr));
            }
            return out;
          };

      java.util.function.Function<
              Brewery, java.util.function.BiConsumer<java.util.List<Tap>, java.util.List<Beer>>>
          stocker =
              (breweryOwner) ->
                  (tapList, beerList) -> {
                    int beerIdx = 0;
                    for (Tap t : tapList) {
                      Beer sel = beerList.get(beerIdx % beerList.size());
                      beerIdx++;
                      Keg newKeg = new Keg(sel, sizeFor.apply(beerIdx));
                      newKeg.setBrewery(breweryOwner);
                      newKeg.setSerialNumber(
                          nextSerial.apply(
                              breweryOwner,
                              breweryOwner.getName().replaceAll("[^A-Z]", " ").trim().isEmpty()
                                  ? "BRW"
                                  : breweryOwner.getName().replaceAll("[^A-Z]", "")));
                      newKeg.setStatus(KegStatus.RECEIVED);
                      kegs.save(newKeg);
                      // Tap the keg and pour tapNumber*10 oz to create TAP and POUR events
                      tapService.tapKeg(t.getId(), newKeg.getId());
                      double ounces = Math.max(4, t.getNumber() * 10);
                      tapService.pour(t.getId(), ounces);
                    }
                  };

      // Brewery 1: Stone Brewing (San Diego)
      Brewery stone = breweries.save(new Brewery("Stone Brewing"));
      // Beers
      String[] stoneBeerNames = new String[25];
      for (int i = 0; i < 25; i++) stoneBeerNames[i] = "Stone Beer #" + (i + 1);
      var stoneBeers = createBeers.apply(stone, stoneBeerNames);
      // Kegs inventory: 10 of each beer
      for (int i = 0; i < stoneBeers.size(); i++) {
        for (int kx = 0; kx < 10; kx++) {
          Keg kg = new Keg(stoneBeers.get(i), sizeFor.apply(i + kx));
          kg.setBrewery(stone);
          kg.setSerialNumber(nextSerial.apply(stone, "STN"));
          kg.setStatus(KegStatus.FILLED);
          kegs.save(kg);
        }
      }
      // 10 empty kegs (modeled as EMPTY status for test data)
      for (int i = 0; i < 10; i++) {
        Keg ek = new Keg(stoneBeers.get(i % stoneBeers.size()), sizeFor.apply(i));
        ek.setBrewery(stone);
        ek.setSerialNumber(nextSerial.apply(stone, "STN"));
        ek.setRemainingOunces(0);
        ek.setStatus(KegStatus.EMPTY);
        kegs.save(ek);
      }
      // Taprooms 4, each 10..15 taps
      int[] stoneTapCounts = new int[] {10, 12, 13, 15};
      for (int i = 0; i < stoneTapCounts.length; i++) {
        Taproom trm = taprooms.save(new Taproom("Stone Taproom " + (i + 1), stone));
        Venue v = venues.save(new Venue("Stone Taproom " + (i + 1), VenueType.TAPROOM, stone));
        java.util.List<Tap> trTaps = new java.util.ArrayList<>();
        for (int n = 1; n <= stoneTapCounts[i]; n++) {
          var t = new Tap(n);
          t.setTaproom(trm);
          t.setVenue(v);
          trTaps.add(taps.save(t));
        }
        stocker.apply(stone).accept(trTaps, stoneBeers);
        // Users: 1 tap admin + 3 tap users each taproom
        var trAdmin =
            new UserAccount(
                "stone_tapadmin_" + (i + 1), encoder.encode("password"), Role.TAPROOM_ADMIN);
        trAdmin.setTaproom(trm);
        users.save(trAdmin);
        for (int u = 1; u <= 3; u++) {
          var tu =
              new UserAccount(
                  "stone_tapuser_" + (i + 1) + "_" + u,
                  encoder.encode("password"),
                  Role.TAPROOM_USER);
          tu.setTaproom(trm);
          users.save(tu);
        }
      }
      // Brewery admin
      var stoneAdmin =
          new UserAccount("stone_brewadmin", encoder.encode("password"), Role.BREWERY_ADMIN);
      stoneAdmin.setBrewery(stone);
      users.save(stoneAdmin);

      // Brewery 2: Sierra Nevada (Chico, CA)
      Brewery sierra = breweries.save(new Brewery("Sierra Nevada"));
      String[] sierraBeerNames = new String[15];
      for (int i = 0; i < 15; i++) sierraBeerNames[i] = "Sierra Beer #" + (i + 1);
      var sierraBeers = createBeers.apply(sierra, sierraBeerNames);
      for (int i = 0; i < sierraBeers.size(); i++) {
        for (int kx = 0; kx < 12; kx++) {
          Keg kg = new Keg(sierraBeers.get(i), sizeFor.apply(i + kx));
          kg.setBrewery(sierra);
          kg.setSerialNumber(nextSerial.apply(sierra, "SNE"));
          kg.setStatus(KegStatus.FILLED);
          kegs.save(kg);
        }
      }
      for (int i = 0; i < 12; i++) {
        Keg ek = new Keg(sierraBeers.get(i % sierraBeers.size()), sizeFor.apply(i));
        ek.setBrewery(sierra);
        ek.setSerialNumber(nextSerial.apply(sierra, "SNE"));
        ek.setRemainingOunces(0);
        ek.setStatus(KegStatus.EMPTY);
        kegs.save(ek);
      }
      int[] sierraTapCounts = new int[] {6, 10};
      for (int i = 0; i < sierraTapCounts.length; i++) {
        Taproom trm = taprooms.save(new Taproom("Sierra Taproom " + (i + 1), sierra));
        Venue v = venues.save(new Venue("Sierra Taproom " + (i + 1), VenueType.TAPROOM, sierra));
        java.util.List<Tap> trTaps = new java.util.ArrayList<>();
        for (int n = 1; n <= sierraTapCounts[i]; n++) {
          var t = new Tap(n);
          t.setTaproom(trm);
          t.setVenue(v);
          trTaps.add(taps.save(t));
        }
        stocker.apply(sierra).accept(trTaps, sierraBeers);
        var trAdmin =
            new UserAccount(
                "sierra_tapadmin_" + (i + 1), encoder.encode("password"), Role.TAPROOM_ADMIN);
        trAdmin.setTaproom(trm);
        users.save(trAdmin);
        for (int u = 1; u <= 3; u++) {
          var tu =
              new UserAccount(
                  "sierra_tapuser_" + (i + 1) + "_" + u,
                  encoder.encode("password"),
                  Role.TAPROOM_USER);
          tu.setTaproom(trm);
          users.save(tu);
        }
      }
      var sierraAdmin =
          new UserAccount("sierra_brewadmin", encoder.encode("password"), Role.BREWERY_ADMIN);
      sierraAdmin.setBrewery(sierra);
      users.save(sierraAdmin);

      // Brewery 3: Reuben's (Seattle, WA)
      Brewery reubens = breweries.save(new Brewery("Reuben's Brews"));
      String[] reubensBeerNames = new String[20];
      for (int i = 0; i < 20; i++) reubensBeerNames[i] = "Reuben's Beer #" + (i + 1);
      var reubensBeers = createBeers.apply(reubens, reubensBeerNames);
      for (int i = 0; i < reubensBeers.size(); i++) {
        for (int kx = 0; kx < 20; kx++) {
          Keg kg = new Keg(reubensBeers.get(i), sizeFor.apply(i + kx));
          kg.setBrewery(reubens);
          kg.setSerialNumber(nextSerial.apply(reubens, "RBN"));
          kg.setStatus(KegStatus.FILLED);
          kegs.save(kg);
        }
      }
      for (int i = 0; i < 20; i++) {
        Keg ek = new Keg(reubensBeers.get(i % reubensBeers.size()), sizeFor.apply(i));
        ek.setBrewery(reubens);
        ek.setSerialNumber(nextSerial.apply(reubens, "RBN"));
        ek.setRemainingOunces(0);
        ek.setStatus(KegStatus.EMPTY);
        kegs.save(ek);
      }
      int[] reubensTapCounts = new int[] {12, 14, 16, 18, 15};
      for (int i = 0; i < reubensTapCounts.length; i++) {
        Taproom trm = taprooms.save(new Taproom("Reuben's Taproom " + (i + 1), reubens));
        Venue v = venues.save(new Venue("Reuben's Taproom " + (i + 1), VenueType.TAPROOM, reubens));
        java.util.List<Tap> trTaps = new java.util.ArrayList<>();
        for (int n = 1; n <= reubensTapCounts[i]; n++) {
          var t = new Tap(n);
          t.setTaproom(trm);
          t.setVenue(v);
          trTaps.add(taps.save(t));
        }
        stocker.apply(reubens).accept(trTaps, reubensBeers);
        var trAdmin =
            new UserAccount(
                "reubens_tapadmin_" + (i + 1), encoder.encode("password"), Role.TAPROOM_ADMIN);
        trAdmin.setTaproom(trm);
        users.save(trAdmin);
        for (int u = 1; u <= 3; u++) {
          var tu =
              new UserAccount(
                  "reubens_tapuser_" + (i + 1) + "_" + u,
                  encoder.encode("password"),
                  Role.TAPROOM_USER);
          tu.setTaproom(trm);
          users.save(tu);
        }
      }
      var reubensAdmin =
          new UserAccount("reubens_brewadmin", encoder.encode("password"), Role.BREWERY_ADMIN);
      reubensAdmin.setBrewery(reubens);
      users.save(reubensAdmin);

      // Bars
      Bar remedy = bars.save(new Bar("Remedy Speakeasy", stone));
      Venue remedyVenue = venues.save(new Venue("Remedy Speakeasy", VenueType.BAR, stone));
      java.util.List<Tap> remedyTaps = new java.util.ArrayList<>();
      for (int i = 1; i <= 6; i++) {
        var t = new Tap(i);
        t.setBar(remedy);
        t.setVenue(remedyVenue);
        remedyTaps.add(taps.save(t));
      }
      // 3 kegs from each brewery, tap 2 of them
      java.util.List<Keg> remedyKegs = new java.util.ArrayList<>();
      for (int i = 0; i < 3; i++) {
        Keg kg = new Keg(stoneBeers.get(i), half);
        kg.setBrewery(stone);
        kg.setSerialNumber(nextSerial.apply(stone, "STN"));
        kg.setStatus(KegStatus.FILLED);
        remedyKegs.add(kegs.save(kg));
      }
      for (int i = 0; i < 3; i++) {
        Keg kg = new Keg(sierraBeers.get(i), quarter);
        kg.setBrewery(sierra);
        kg.setSerialNumber(nextSerial.apply(sierra, "SNE"));
        kg.setStatus(KegStatus.FILLED);
        remedyKegs.add(kegs.save(kg));
      }
      for (int i = 0; i < 3; i++) {
        Keg kg = new Keg(reubensBeers.get(i), sixtel);
        kg.setBrewery(reubens);
        kg.setSerialNumber(nextSerial.apply(reubens, "RBN"));
        kg.setStatus(KegStatus.FILLED);
        remedyKegs.add(kegs.save(kg));
      }
      // Tap first two taps
      for (int i = 0; i < 2; i++) {
        tapService.tapKeg(remedyTaps.get(i).getId(), remedyKegs.get(i).getId());
        tapService.pour(remedyTaps.get(i).getId(), remedyTaps.get(i).getNumber() * 10);
      }
      // Users
      for (int i = 1; i <= 2; i++) {
        var u = new UserAccount("remedy_baradmin_" + i, encoder.encode("password"), Role.BAR_ADMIN);
        u.setBar(remedy);
        users.save(u);
      }
      for (int i = 1; i <= 2; i++) {
        var u =
            new UserAccount("remedy_baruser_" + i, encoder.encode("password"), Role.TAPROOM_USER);
        u.setBar(remedy);
        users.save(u);
      }

      Bar beerBar = bars.save(new Bar("Beer Bar", sierra));
      Venue beerBarVenue = venues.save(new Venue("Beer Bar", VenueType.BAR, sierra));
      java.util.List<Tap> beerBarTaps = new java.util.ArrayList<>();
      for (int i = 1; i <= 20; i++) {
        var t = new Tap(i);
        t.setBar(beerBar);
        t.setVenue(beerBarVenue);
        beerBarTaps.add(taps.save(t));
      }
      // 10 beers from each brewery, tap 6 from each (total 18 tapped), leave 2 empty taps
      java.util.List<Keg> beerBarKegs = new java.util.ArrayList<>();
      for (int i = 0; i < 10; i++) {
        Keg kg = new Keg(stoneBeers.get(i), sizeFor.apply(i));
        kg.setBrewery(stone);
        kg.setSerialNumber(nextSerial.apply(stone, "STN"));
        kg.setStatus(KegStatus.FILLED);
        beerBarKegs.add(kegs.save(kg));
      }
      for (int i = 0; i < 10; i++) {
        Keg kg = new Keg(sierraBeers.get(i), sizeFor.apply(i + 10));
        kg.setBrewery(sierra);
        kg.setSerialNumber(nextSerial.apply(sierra, "SNE"));
        kg.setStatus(KegStatus.FILLED);
        beerBarKegs.add(kegs.save(kg));
      }
      for (int i = 0; i < 10; i++) {
        Keg kg = new Keg(reubensBeers.get(i), sizeFor.apply(i + 20));
        kg.setBrewery(reubens);
        kg.setSerialNumber(nextSerial.apply(reubens, "RBN"));
        kg.setStatus(KegStatus.FILLED);
        beerBarKegs.add(kegs.save(kg));
      }
      int tapIdx = 0;
      for (int i = 0; i < 18; i++) {
        tapService.tapKeg(beerBarTaps.get(tapIdx).getId(), beerBarKegs.get(i).getId());
        tapService.pour(beerBarTaps.get(tapIdx).getId(), beerBarTaps.get(tapIdx).getNumber() * 10);
        tapIdx++;
      }
      // Users: 3 admins, 5 users
      for (int i = 1; i <= 3; i++) {
        var u = new UserAccount("beerbar_admin_" + i, encoder.encode("password"), Role.BAR_ADMIN);
        u.setBar(beerBar);
        users.save(u);
      }
      for (int i = 1; i <= 5; i++) {
        var u = new UserAccount("beerbar_user_" + i, encoder.encode("password"), Role.TAPROOM_USER);
        u.setBar(beerBar);
        users.save(u);
      }
    };
  }
}
