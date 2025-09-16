package com.mythictales.bms.taplist.api;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Shared test wiring for API controller tests to avoid brittle per-test setup. */
public abstract class BaseApiControllerTest {

  protected MockMvc buildMvc(Object... controllers) {
    return MockMvcBuilders.standaloneSetup(controllers)
        // Register pageable resolver so controllers/tests can use page/size/sort reliably
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        // Use the API exception handler to provide consistent Problem JSON
        .setControllerAdvice(new com.mythictales.bms.taplist.config.RestExceptionHandler())
        // Print exchanges to aid debugging during failures
        .alwaysDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        .build();
  }
}
