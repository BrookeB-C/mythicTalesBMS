package com.mythictales.bms.taplist.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
  public Page<BeerDto> list(@ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
    return beers.findAll(pageable).map(ApiMappers::toDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BeerDto> get(@PathVariable Long id) {
    return beers
        .findById(id)
        .map(b -> ResponseEntity.ok(ApiMappers.toDto(b)))
        .orElse(ResponseEntity.notFound().build());
  }
}
