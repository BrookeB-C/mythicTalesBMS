package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.KegSizeSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface KegSizeSpecRepository extends JpaRepository<KegSizeSpec, Long> {
    Optional<KegSizeSpec> findByCode(String code);
}
