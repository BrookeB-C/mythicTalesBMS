package com.mythictales.bms.taplist.repo;

import com.mythictales.bms.taplist.domain.KegEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KegEventRepository extends CrudRepository<KegEvent, Long> {

    @Query("""
        SELECT e FROM KegEvent e
        JOIN e.placement p
        JOIN p.tap t
        WHERE t.venue.id = :venueId
        ORDER BY e.atTime DESC
    """)
    List<KegEvent> findVenueEvents(@Param("venueId") Long venueId);

    // (Optional) Back-compat if any older code still calls this name:
    @Query("""
        SELECT e FROM KegEvent e
        JOIN e.placement p
        JOIN p.tap t
        WHERE t.venue.id = :venueId
        ORDER BY e.atTime DESC
    """)
    List<KegEvent> findByVenueIdOrderByAtTimeDesc(@Param("venueId") Long venueId);
}
