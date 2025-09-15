package com.mythictales.bms.taplist.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mythictales.bms.taplist.domain.KegEvent;

public interface KegEventRepository extends JpaRepository<KegEvent, Long> {

  @Query(
      """
        SELECT e FROM KegEvent e
        JOIN e.placement p
        JOIN p.tap t
        WHERE t.venue.id = :venueId
        ORDER BY e.atTime DESC
    """)
  List<KegEvent> findVenueEvents(@Param("venueId") Long venueId);

  @Query(
      value =
          """
        SELECT e FROM KegEvent e
        JOIN e.placement p
        JOIN p.tap t
        WHERE t.venue.id = :venueId
        ORDER BY e.atTime DESC
    """,
      countQuery =
          """
        SELECT COUNT(e) FROM KegEvent e
        JOIN e.placement p
        JOIN p.tap t
        WHERE t.venue.id = :venueId
    """)
  Page<KegEvent> findVenueEvents(@Param("venueId") Long venueId, Pageable pageable);

  // (Optional) Back-compat if any older code still calls this name:
  @Query(
      """
        SELECT e FROM KegEvent e
        JOIN e.placement p
        JOIN p.tap t
        WHERE t.venue.id = :venueId
        ORDER BY e.atTime DESC
    """)
  List<KegEvent> findByVenueIdOrderByAtTimeDesc(@Param("venueId") Long venueId);
}
