package com.mythictales.bms.taplist.catalog.repo;

import com.mythictales.bms.taplist.catalog.domain.BjcpStyle;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BjcpStyleRepository extends JpaRepository<BjcpStyle, Long> {
  Optional<BjcpStyle> findFirstByCodeAndYear(String code, Integer year);

  Page<BjcpStyle> findByYear(Integer year, Pageable pageable);

  @Query(
      "select s from BjcpStyle s "
          + "where (:year is null or s.year = :year) "
          + "and (:q is null or lower(s.code) like lower(concat('%',:q,'%')) or lower(s.name) like lower(concat('%',:q,'%')))"
  )
  Page<BjcpStyle> search(@Param("year") Integer year, @Param("q") String q, Pageable pageable);
}
