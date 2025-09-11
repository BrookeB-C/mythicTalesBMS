package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByBreweryId(Long breweryId);
}
