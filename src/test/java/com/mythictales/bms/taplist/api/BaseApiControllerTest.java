package com.mythictales.bms.taplist.api;

import org.springframework.core.MethodParameter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Shared test wiring for API controller tests to avoid brittle per-test setup. */
public abstract class BaseApiControllerTest {

  protected MockMvc buildMvc(Object... controllers) {
    return MockMvcBuilders.standaloneSetup(controllers)
        // Register pageable resolver so controllers/tests can use page/size/sort reliably
        .setCustomArgumentResolvers(
            new PageableHandlerMethodArgumentResolver(),
            // Resolve @AuthenticationPrincipal CurrentUser as null for standalone tests
            new HandlerMethodArgumentResolver() {
              @Override
              public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
              }

              @Override
              public Object resolveArgument(
                  MethodParameter parameter,
                  ModelAndViewContainer mavContainer,
                  NativeWebRequest webRequest,
                  WebDataBinderFactory binderFactory)
                  throws Exception {
                Object principal =
                    webRequest.getAttribute("currentUser", NativeWebRequest.SCOPE_REQUEST);
                return principal;
              }
            })
        .setMessageConverters(new HttpMessageConverter[] {mappingJacksonConverter()})
        // Use the API exception handler to provide consistent Problem JSON
        .setControllerAdvice(new com.mythictales.bms.taplist.config.RestExceptionHandler())
        // Print exchanges to aid debugging during failures
        .alwaysDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        .build();
  }

  private MappingJackson2HttpMessageConverter mappingJacksonConverter() {
    var objectMapper = Jackson2ObjectMapperBuilder.json().build();
    objectMapper.findAndRegisterModules();
    return new MappingJackson2HttpMessageConverter(objectMapper);
  }
}
