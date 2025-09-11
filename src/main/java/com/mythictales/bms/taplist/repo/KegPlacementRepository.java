package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.KegPlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface KegPlacementRepository extends JpaRepository<KegPlacement, Long> {
    Optional<KegPlacement> findFirstByTapIdAndEndedAtIsNull(Long tapId);
    List<KegPlacement> findByTapIdOrderByStartedAtDesc(Long tapId);
}
