package com.mythictales.bms.taplist.catalog.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mythictales.bms.taplist.catalog.domain.BjcpStyle;

public interface BjcpStyleRepository extends JpaRepository<BjcpStyle, Long> {
  Optional<BjcpStyle> findFirstByCodeAndGuidelineYear(String code, Integer guidelineYear);

  Page<BjcpStyle> findByGuidelineYear(Integer guidelineYear, Pageable pageable);

  @Query(
      "select s from BjcpStyle s "
          + "where (:year is null or s.guidelineYear = :year) "
          + "and (:q is null or lower(s.code) like lower(concat('%',:q,'%')) or lower(s.name) like lower(concat('%',:q,'%')))")
  Page<BjcpStyle> search(@Param("year") Integer year, @Param("q") String q, Pageable pageable);
}
