package com.mythictales.bms.taplist.catalog.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mythictales.bms.taplist.catalog.service.RecipeImportService;

/** Tests validation and responses for RecipeImportController. */
public class RecipeImportControllerTest {
  private RecipeImportService service;
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    service = Mockito.mock(RecipeImportService.class);
    mvc =
        MockMvcBuilders.standaloneSetup(new RecipeImportController(service))
            .setControllerAdvice(new com.mythictales.bms.taplist.config.RestExceptionHandler())
            .alwaysDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .build();
  }

  @Test
  void emptyFile_returns400() throws Exception {
    MockMultipartFile f = new MockMultipartFile("file", new byte[0]);
    mvc.perform(
            multipart("/api/v1/catalog/recipes/import")
                .file(f)
                .param("breweryId", "9")
                .param("force", "false")
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType("application/problem+json"));
  }

  @Test
  void ok_returnsIds() throws Exception {
    when(service.importXml(eq(9L), any(String.class), eq(false))).thenReturn(List.of(1L, 2L));
    MockMultipartFile f =
        new MockMultipartFile(
            "file", "r.xml", "application/xml", "<RECIPES></RECIPES>".getBytes(StandardCharsets.UTF_8));
    mvc.perform(
            multipart("/api/v1/catalog/recipes/import")
                .file(f)
                .param("breweryId", "9")
                .param("force", "false")
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ids").isArray());
  }

  @Test
  void duplicate_returns409() throws Exception {
    when(service.importXml(eq(9L), any(String.class), eq(false)))
        .thenThrow(new RecipeImportService.DuplicateRecipeException(99L));
    MockMultipartFile f =
        new MockMultipartFile(
            "file", "r.xml", "application/xml", "<RECIPES><RECIPE/></RECIPES>".getBytes(StandardCharsets.UTF_8));
    mvc.perform(
            multipart("/api/v1/catalog/recipes/import")
                .file(f)
                .param("breweryId", "9")
                .param("force", "false")
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict())
        .andExpect(content().contentType("application/problem+json"));
  }
}

