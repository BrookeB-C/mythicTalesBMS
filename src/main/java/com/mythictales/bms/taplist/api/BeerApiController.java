package com.mythictales.bms.taplist.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.BeerDto;
import com.mythictales.bms.taplist.catalog.repo.BjcpStyleRepository;
import com.mythictales.bms.taplist.domain.Beer;
import com.mythictales.bms.taplist.repo.BeerRepository;
import com.mythictales.bms.taplist.service.BusinessValidationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/beers")
@Tag(name = "Beers")
public class BeerApiController {
  private final BeerRepository beers;
  private final BjcpStyleRepository styles;

  public BeerApiController(BeerRepository beers, BjcpStyleRepository styles) {
    this.beers = beers;
    this.styles = styles;
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

  public record StyleLinkRequest(Long styleId) {}

  @PatchMapping(value = "/{id}/styleRef", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SITE_ADMIN') or hasRole('BREWERY_ADMIN')")
  @Operation(summary = "Link a BJCP style to a beer")
  public ResponseEntity<BeerDto> linkStyle(
      @PathVariable Long id, @RequestBody StyleLinkRequest req) {
    if (req == null || req.styleId == null) {
      throw new BusinessValidationException("styleId is required");
    }
    Beer b = beers.findById(id).orElseThrow(java.util.NoSuchElementException::new);
    var style = styles.findById(req.styleId()).orElseThrow(java.util.NoSuchElementException::new);
    b.setStyleRef(style);
    beers.save(b);
    return ResponseEntity.ok(ApiMappers.toDto(b));
  }

  @DeleteMapping("/{id}/styleRef")
  @PreAuthorize("hasRole('SITE_ADMIN') or hasRole('BREWERY_ADMIN')")
  @Operation(summary = "Unlink BJCP style from a beer")
  public ResponseEntity<BeerDto> unlinkStyle(@PathVariable Long id) {
    Beer b = beers.findById(id).orElseThrow(java.util.NoSuchElementException::new);
    b.setStyleRef(null);
    beers.save(b);
    return ResponseEntity.ok(ApiMappers.toDto(b));
  }
}
