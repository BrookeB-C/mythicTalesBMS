package com.mythictales.bms.taplist.catalog.api;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mythictales.bms.taplist.catalog.service.RecipeImportService;
import com.mythictales.bms.taplist.catalog.service.RecipeImportService.DuplicateRecipeException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/catalog/recipes")
@Tag(name = "Catalog: Recipes")
public class RecipeImportController {
  private final RecipeImportService importer;

  public RecipeImportController(RecipeImportService importer) {
    this.importer = importer;
  }

  public record ImportResponse(List<Long> ids) {}

  @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  @Operation(summary = "Import BeerXML or BeerSmith XML; returns created recipe IDs")
  public ResponseEntity<?> importRecipes(
      @RequestParam("breweryId") Long breweryId,
      @RequestParam(value = "force", defaultValue = "false") boolean force,
      @RequestPart("file") MultipartFile file) {
    try {
      String xml = new String(file.getBytes(), StandardCharsets.UTF_8);
      List<Long> ids = importer.importXml(breweryId, xml, force);
      return ResponseEntity.ok(new ImportResponse(ids));
    } catch (DuplicateRecipeException dup) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .contentType(MediaType.APPLICATION_JSON)
          .body("{\"error\":\"DUPLICATE_RECIPE\",\"existingId\":" + dup.getExistingId() + "}");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .contentType(MediaType.APPLICATION_JSON)
          .body(
              "{\"error\":\"IMPORT_FAILED\",\"message\":\""
                  + e.getMessage().replace("\"", "'")
                  + "\"}");
    }
  }
}
