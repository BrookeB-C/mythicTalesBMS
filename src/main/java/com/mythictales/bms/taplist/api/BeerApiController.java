package com.mythictales.bms.taplist.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.BeerDto;
import com.mythictales.bms.taplist.repo.BeerRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/beers")
@Tag(name = "Beers")
public class BeerApiController {
  private final BeerRepository beers;

  public BeerApiController(BeerRepository beers) {
    this.beers = beers;
  }

  @GetMapping
  public List<BeerDto> list() {
    return beers.findAll().stream().map(ApiMappers::toDto).collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BeerDto> get(@PathVariable Long id) {
    return beers
        .findById(id)
        .map(b -> ResponseEntity.ok(ApiMappers.toDto(b)))
        .orElse(ResponseEntity.notFound().build());
  }
}
