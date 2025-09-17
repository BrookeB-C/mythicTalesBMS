package com.mythictales.bms.taplist.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mythictales.bms.taplist.catalog.domain.Recipe;
import com.mythictales.bms.taplist.catalog.repo.RecipeRepository;

/** Basic controller tests for recipe export and simple child add/remove flows. */
public class AdminCatalogControllerTest {

  private RecipeRepository repo;
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    repo = Mockito.mock(RecipeRepository.class);
    mvc =
        MockMvcBuilders.standaloneSetup(new AdminCatalogController(repo))
            .setCustomArgumentResolvers(
                new org.springframework.security.web.method.annotation
                    .AuthenticationPrincipalArgumentResolver())
            .setControllerAdvice(new com.mythictales.bms.taplist.config.RestExceptionHandler())
            .alwaysDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .build();
  }

  @Test
  void exportBeerXml_returnsXml() throws Exception {
    Recipe r = new Recipe();
    java.lang.reflect.Field f = Recipe.class.getDeclaredField("id");
    f.setAccessible(true);
    f.set(r, 1L);
    r.setName("Test IPA");
    when(repo.findById(1L)).thenReturn(Optional.of(r));

    mvc.perform(
            get("/admin/catalog/recipes/1/export")
                .param("format", "beerxml")
                .with(SecurityMockMvcRequestPostProcessors.user(dummyUser())))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/xml"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("<RECIPES>")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Test IPA")));
  }

  @Test
  void exportBeerSmith_returnsXml() throws Exception {
    Recipe r = new Recipe();
    java.lang.reflect.Field f = Recipe.class.getDeclaredField("id");
    f.setAccessible(true);
    f.set(r, 2L);
    r.setName("Pale Ale");
    when(repo.findById(2L)).thenReturn(Optional.of(r));

    mvc.perform(
            get("/admin/catalog/recipes/2/export")
                .param("format", "beersmith")
                .with(SecurityMockMvcRequestPostProcessors.user(dummyUser())))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/xml"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("<Recipes>")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Pale Ale")));
  }

  @Test
  void addFermentable_savesRecipeAndRedirects() throws Exception {
    Recipe r = new Recipe();
    java.lang.reflect.Field f = Recipe.class.getDeclaredField("id");
    f.setAccessible(true);
    f.set(r, 3L);
    when(repo.findById(3L)).thenReturn(Optional.of(r));
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    mvc.perform(
            post("/admin/catalog/recipes/3/fermentables/add")
                .param("name", "Pale Malt")
                .param("amountKg", "4.5")
                .with(SecurityMockMvcRequestPostProcessors.user(dummyUser())))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/catalog/recipes/3"));

    // verify mutation happened
    org.junit.jupiter.api.Assertions.assertEquals(1, r.getFermentables().size());
    verify(repo, times(1)).save(any());
  }

  private org.springframework.security.core.userdetails.UserDetails dummyUser() {
    com.mythictales.bms.taplist.domain.UserAccount ua =
        new com.mythictales.bms.taplist.domain.UserAccount(
            "tester", "pw", com.mythictales.bms.taplist.domain.Role.SITE_ADMIN);
    com.mythictales.bms.taplist.security.CurrentUser cu =
        new com.mythictales.bms.taplist.security.CurrentUser(ua);
    return cu;
  }
}
