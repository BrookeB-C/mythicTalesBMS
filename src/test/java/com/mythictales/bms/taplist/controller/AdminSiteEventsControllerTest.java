package com.mythictales.bms.taplist.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.repo.TaproomRepository;

class AdminSiteEventsControllerTest {

  private KegEventRepository events;
  private BreweryRepository breweries;
  private TaproomRepository taprooms;
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    events = mock(KegEventRepository.class);
    breweries = mock(BreweryRepository.class);
    taprooms = mock(TaproomRepository.class);

    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/templates/");
    viewResolver.setSuffix(".html");

    mvc =
        MockMvcBuilders.standaloneSetup(new AdminSiteEventsController(events, breweries, taprooms))
            .setViewResolvers(viewResolver)
            .build();
  }

  @Test
  void listsEventsWithoutFilters() throws Exception {
    when(events.findEventsFiltered(isNull(), isNull(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));
    when(breweries.findAll(any(Sort.class))).thenReturn(List.of());
    when(taprooms.findAll(any(Sort.class))).thenReturn(List.of());

    mvc.perform(get("/admin/site/events"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("events", "breweries", "taprooms"));

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(events).findEventsFiltered(isNull(), isNull(), pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(25);

    verify(taprooms).findAll(any(Sort.class));
    verifyNoMoreInteractions(taprooms);
  }

  @Test
  void filtersTaproomsWhenBrewerySelected() throws Exception {
    when(events.findEventsFiltered(eq(5L), isNull(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));
    when(breweries.findAll(any(Sort.class))).thenReturn(List.of());
    when(taprooms.findByBreweryId(5L)).thenReturn(List.of());

    mvc.perform(get("/admin/site/events").param("breweryId", "5"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("selectedBreweryId", 5L));

    verify(taprooms).findByBreweryId(5L);
    verify(taprooms, never()).findAll(any(Sort.class));
  }
}
