package com.mythictales.bms.taplist;
import com.mythictales.bms.taplist.domain.*; import com.mythictales.bms.taplist.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean CommandLineRunner initData(BreweryRepository breweries, TaproomRepository taprooms, BarRepository bars,
                               BeerRepository beers, KegRepository kegs, TapRepository taps,
                               UserAccountRepository users, PasswordEncoder encoder){
        return args -> {
            Brewery br = breweries.save(new Brewery("MythicTales Brewing"));
            Taproom tr = taprooms.save(new Taproom("MythicTales Taproom", br));
            Bar bar = bars.save(new Bar("MythicTales Downtown Bar", br));
            Tap tap1 = new Tap(1); tap1.setTaproom(tr); taps.save(tap1);
            Tap tap2 = new Tap(2); tap2.setTaproom(tr); taps.save(tap2);
            Tap tap3 = new Tap(3); tap3.setBar(bar); taps.save(tap3);
            Beer ipa = beers.save(new Beer("Dragon's Breath IPA", "IPA", 6.8));
            Beer stout = beers.save(new Beer("Shadow Stout", "Stout", 8.0));
            Beer lager = beers.save(new Beer("Silver Lager", "Lager", 5.0));
            Keg k1 = kegs.save(new Keg(ipa, KegSize.HALF_BARREL));
            Keg k2 = kegs.save(new Keg(stout, KegSize.SIXTEL));
            Keg k3 = kegs.save(new Keg(lager, KegSize.QUARTER_BARREL));
            users.save(new UserAccount("siteadmin", encoder.encode("password"), Role.SITE_ADMIN));
            var brew = new UserAccount("brewadmin", encoder.encode("password"), Role.BREWERY_ADMIN); brew.setBrewery(br); users.save(brew);
            var baradmin = new UserAccount("baradmin", encoder.encode("password"), Role.BAR_ADMIN); baradmin.setBar(bar); users.save(baradmin);
            var tapadmin = new UserAccount("tapadmin", encoder.encode("password"), Role.TAPROOM_ADMIN); tapadmin.setTaproom(tr); users.save(tapadmin);
            var tapuser = new UserAccount("tapuser", encoder.encode("password"), Role.TAPROOM_USER); tapuser.setTaproom(tr); users.save(tapuser);
        };
    }
}
