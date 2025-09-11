package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.Taproom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TaproomRepository extends JpaRepository<Taproom, Long> {
    List<Taproom> findByBreweryId(Long breweryId);
}
