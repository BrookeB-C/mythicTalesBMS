package com.mythictales.bms.taplist.api;

import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.BreweryDto;
import com.mythictales.bms.taplist.repo.BreweryRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/breweries")
@Tag(name = "Breweries")
public class BreweryApiController {
  private final BreweryRepository breweries;

  public BreweryApiController(BreweryRepository breweries) {
    this.breweries = breweries;
  }

  @GetMapping
  public Page<BreweryDto> list(@ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
    return breweries.findAll(pageable).map(ApiMappers::toDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BreweryDto> get(@PathVariable Long id) {
    return breweries
        .findById(id)
        .map(b -> ResponseEntity.ok(ApiMappers.toDto(b)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  public ResponseEntity<BreweryDto> patch(
      @PathVariable Long id, @RequestBody Map<String, Object> body) {
    return breweries
        .findById(id)
        .map(
            b -> {
              if (body.containsKey("name") && body.get("name") != null) {
                b.setName(body.get("name").toString().trim());
              }
              return ResponseEntity.ok(ApiMappers.toDto(breweries.save(b)));
            })
        .orElse(ResponseEntity.notFound().build());
  }
}
