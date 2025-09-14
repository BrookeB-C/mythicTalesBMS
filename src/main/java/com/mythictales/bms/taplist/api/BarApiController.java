package com.mythictales.bms.taplist.api;

import java.util.List;
import java.util.stream.Collectors;

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
  public List<BarDto> list(@RequestParam(value = "breweryId", required = false) Long breweryId) {
    var list = (breweryId != null) ? bars.findByBreweryId(breweryId) : bars.findAll();
    return list.stream().map(ApiMappers::toDto).collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BarDto> get(@PathVariable Long id) {
    return bars.findById(id)
        .map(v -> ResponseEntity.ok(ApiMappers.toDto(v)))
        .orElse(ResponseEntity.notFound().build());
  }
}
