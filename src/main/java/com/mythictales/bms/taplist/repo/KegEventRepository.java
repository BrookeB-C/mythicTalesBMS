package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.KegEvent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface KegEventRepository extends JpaRepository<KegEvent, Long> {}
