package com.mythictales.bms.taplist.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.BarDto;
import com.mythictales.bms.taplist.repo.BarRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/bars")
@Tag(name = "Bars")
public class BarApiController {
  private final BarRepository bars;

  public BarApiController(BarRepository bars) {
    this.bars = bars;
  }

  @GetMapping
  public Page<BarDto> list(
      @RequestParam(value = "breweryId", required = false) Long breweryId,
      @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
    return (breweryId != null)
        ? bars.findByBreweryId(breweryId, pageable).map(ApiMappers::toDto)
        : bars.findAll(pageable).map(ApiMappers::toDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BarDto> get(@PathVariable Long id) {
    return bars.findById(id)
        .map(v -> ResponseEntity.ok(ApiMappers.toDto(v)))
        .orElse(ResponseEntity.notFound().build());
  }
}
