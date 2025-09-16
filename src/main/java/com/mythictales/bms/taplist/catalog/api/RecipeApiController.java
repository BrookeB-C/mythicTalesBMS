package com.mythictales.bms.taplist.catalog.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.catalog.api.dto.RecipeDto;
import com.mythictales.bms.taplist.catalog.repo.RecipeRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/catalog/recipes")
@Tag(name = "Catalog: Recipes")
public class RecipeApiController {
  private final RecipeRepository recipes;

  public RecipeApiController(RecipeRepository recipes) {
    this.recipes = recipes;
  }

  @GetMapping
  @PreAuthorize(
      "hasAnyRole('SITE_ADMIN','BREWERY_ADMIN') and (#breweryId == principal.breweryId or hasRole('SITE_ADMIN'))")
  @Operation(summary = "List recipes for a brewery")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Paged recipes",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        name = "RecipePage",
                        value =
                            "{\n  \"content\": [{\n    \"id\": 1, \"breweryId\": 9, \"name\": \"House IPA\", \"styleName\": \"American IPA\", \"type\": \"All Grain\", \"batchSizeLiters\": 20.0, \"boilTimeMinutes\": 60, \"abv\": 6.5\n  }],\n  \"pageable\": {\"pageNumber\":0,\"pageSize\":20},\n  \"totalElements\": 1, \"totalPages\": 1\n}")))
  })
  public Page<RecipeDto> list(
      @RequestParam("breweryId") Long breweryId,
      @ParameterObject @PageableDefault(sort = "createdAt") Pageable pageable) {
    return recipes.findByBrewery_Id(breweryId, pageable).map(CatalogApiMappers::toDto);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  @Operation(summary = "Get a recipe by id (with children)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Recipe",
        content =
            @Content(
                mediaType = "application/json",
                schema =
                    @Schema(
                        implementation =
                            com.mythictales.bms.taplist.catalog.api.dto.RecipeDto.class),
                examples =
                    @ExampleObject(
                        name = "Recipe",
                        value =
                            "{\n  \"id\": 1, \"breweryId\": 9, \"name\": \"House IPA\", \"styleName\": \"American IPA\",\n  \"hops\": [{\"name\": \"Cascade\", \"amountGrams\": 28.0, \"timeMinutes\": 60}]\n}")))
  })
  public ResponseEntity<RecipeDto> get(
      @PathVariable Long id,
      @org.springframework.security.core.annotation.AuthenticationPrincipal
          com.mythictales.bms.taplist.security.CurrentUser user) {
    return recipes
        .findById(id)
        .map(
            r -> {
              Long userBrewery = user != null ? user.getBreweryId() : null;
              Long recipeBrewery = r.getBrewery() != null ? r.getBrewery().getId() : null;
              boolean siteAdmin =
                  user != null
                      && user.getAuthorities().stream()
                          .anyMatch(a -> a.getAuthority().equals("ROLE_SITE_ADMIN"));
              if (!siteAdmin && userBrewery != null && !userBrewery.equals(recipeBrewery)) {
                throw new org.springframework.security.access.AccessDeniedException(
                    "Cross-tenant access forbidden");
              }
              return ResponseEntity.ok(CatalogApiMappers.toDto(r));
            })
        .orElse(ResponseEntity.notFound().build());
  }
}
