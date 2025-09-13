package com.mythictales.bms.taplist.repo;

import com.mythictales.bms.taplist.domain.Tap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TapRepository extends JpaRepository<Tap, Long> {
    List<Tap> findByTaproomId(Long taproomId);
    List<Tap> findByBarId(Long barId);
    List<Tap> findByVenueId(Long venueId);

    List<Tap> findByTaproomIdAndKegIsNull(Long taproomId);
    List<Tap> findByBarIdAndKegIsNull(Long barId);
    List<Tap> findByVenueIdAndKegIsNull(Long venueId);
}
