package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BeerRepository extends JpaRepository<Beer, Long> {}
