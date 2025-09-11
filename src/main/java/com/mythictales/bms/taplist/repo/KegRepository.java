package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface KegRepository extends JpaRepository<Keg, Long> {
    List<Keg> findByStatus(KegStatus status);
}
