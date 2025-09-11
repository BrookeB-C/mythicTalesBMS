package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.Bar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BarRepository extends JpaRepository<Bar, Long> {
    List<Bar> findByBreweryId(Long breweryId);
}
