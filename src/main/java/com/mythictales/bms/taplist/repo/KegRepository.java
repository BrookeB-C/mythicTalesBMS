package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface KegRepository extends JpaRepository<Keg, Long> {
    List<Keg> findByStatus(KegStatus status);
    List<Keg> findByBreweryId(Long breweryId);
    List<Keg> findByBreweryIdAndStatus(Long breweryId, KegStatus status);
    List<Keg> findByAssignedVenueId(Long venueId);
    List<Keg> findByAssignedVenueIdAndStatus(Long venueId, KegStatus status);
    List<Keg> findByBreweryIdAndAssignedVenueIsNull(Long breweryId);
    List<Keg> findByBreweryIdAndAssignedVenueIsNotNull(Long breweryId);
    List<Keg> findByBreweryIdAndAssignedVenueIsNullAndStatus(Long breweryId, KegStatus status);
    List<Keg> findByBreweryIdAndAssignedVenueIsNotNullAndStatus(Long breweryId, KegStatus status);
}
