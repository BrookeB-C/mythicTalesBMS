package com.mythictales.bms.taplist.catalog.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mythictales.bms.taplist.catalog.domain.BjcpStyle;
import com.mythictales.bms.taplist.catalog.repo.BjcpStyleRepository;
import com.mythictales.bms.taplist.catalog.service.StyleImportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/catalog/styles")
@Tag(name = "Catalog: BJCP Styles")
public class BjcpStyleController {
  private final BjcpStyleRepository repo;
  private final StyleImportService importer;

  public BjcpStyleController(BjcpStyleRepository repo, StyleImportService importer) {
    this.repo = repo;
    this.importer = importer;
  }

  @GetMapping
  @Operation(summary = "List BJCP styles (server-side filters: year, q on code/name)")
  public Page<BjcpStyle> list(
      @RequestParam(value = "year", required = false) Integer year,
      @RequestParam(value = "q", required = false) String q,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "50") int size,
      @RequestParam(value = "sort", defaultValue = "code,asc") String sort) {
    Sort srt = parseSort(sort, "code");
    var pr = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(200, size)), srt);
    return repo.search(year, (q != null && !q.isBlank()) ? q.trim() : null, pr);
  }

  private Sort parseSort(String sort, String def) {
    try {
      String[] p = sort.split(",");
      String prop = p[0];
      String dir = p.length > 1 ? p[1] : "asc";
      return "desc".equalsIgnoreCase(dir) ? Sort.by(prop).descending() : Sort.by(prop).ascending();
    } catch (Exception e) {
      return Sort.by(def).ascending();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<BjcpStyle> get(@PathVariable Long id) {
    return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('SITE_ADMIN')")
  @Operation(
      summary =
          "Import BJCP styles from CSV (code,name,category,subcategory,year,metrics...,notes)")
  public ResponseEntity<?> importCsv(
      @RequestPart("file") MultipartFile file,
      @RequestParam(value = "upsert", defaultValue = "true") boolean upsert) {
    try {
      var ids = importer.importCsv(file.getInputStream(), upsert);
      return ResponseEntity.ok(java.util.Map.of("count", ids.size(), "ids", ids));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .contentType(MediaType.APPLICATION_JSON)
          .body(java.util.Map.of("error", "STYLE_IMPORT_FAILED", "message", e.getMessage()));
    }
  }
}
