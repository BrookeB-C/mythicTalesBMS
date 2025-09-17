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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Beer updated",
        content =
            @Content(
                mediaType = "application/json",
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.BeerDto.class),
                examples =
                    @ExampleObject(
                        value =
                            "{\"id\":1,\"name\":\"Pale Ale\",\"style\":\"American Pale Ale\",\"styleRefId\":42,\"abv\":5.5}")))
  })
  public ResponseEntity<BeerDto> linkStyle(
      @PathVariable Long id,
      @RequestBody StyleLinkRequest req,
      @org.springframework.security.core.annotation.AuthenticationPrincipal
          com.mythictales.bms.taplist.security.CurrentUser user) {
    if (req == null || req.styleId == null) {
      throw new BusinessValidationException("styleId is required");
    }
    Beer b = beers.findById(id).orElseThrow(java.util.NoSuchElementException::new);
    // Tenant scope: unless SITE_ADMIN, user's brewery must match beer.brewery
    boolean siteAdmin =
        user != null
            && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SITE_ADMIN"));
    if (!siteAdmin) {
      Long userBrewery = user != null ? user.getBreweryId() : null;
      Long beerBrewery = b.getBrewery() != null ? b.getBrewery().getId() : null;
      if (userBrewery == null || beerBrewery == null || !userBrewery.equals(beerBrewery)) {
        throw new org.springframework.security.access.AccessDeniedException(
            "Cross-tenant update forbidden");
      }
    }
    var style = styles.findById(req.styleId()).orElseThrow(java.util.NoSuchElementException::new);
    b.setStyleRef(style);
    beers.save(b);
    return ResponseEntity.ok(ApiMappers.toDto(b));
  }

  @DeleteMapping("/{id}/styleRef")
  @PreAuthorize("hasRole('SITE_ADMIN') or hasRole('BREWERY_ADMIN')")
  @Operation(summary = "Unlink BJCP style from a beer")
  @ApiResponse(
      responseCode = "200",
      description = "Beer updated",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = com.mythictales.bms.taplist.api.dto.BeerDto.class)))
  public ResponseEntity<BeerDto> unlinkStyle(
      @PathVariable Long id,
      @org.springframework.security.core.annotation.AuthenticationPrincipal
          com.mythictales.bms.taplist.security.CurrentUser user) {
    Beer b = beers.findById(id).orElseThrow(java.util.NoSuchElementException::new);
    boolean siteAdmin =
        user != null
            && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SITE_ADMIN"));
    if (!siteAdmin) {
      Long userBrewery = user != null ? user.getBreweryId() : null;
      Long beerBrewery = b.getBrewery() != null ? b.getBrewery().getId() : null;
      if (userBrewery == null || beerBrewery == null || !userBrewery.equals(beerBrewery)) {
        throw new org.springframework.security.access.AccessDeniedException(
            "Cross-tenant update forbidden");
      }
    }
    b.setStyleRef(null);
    beers.save(b);
    return ResponseEntity.ok(ApiMappers.toDto(b));
  }
}
