package com.mythictales.bms.taplist.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  public static final String TRACE_ID = "traceId";
  public static final String USER_ID = "userId";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = request.getHeader("X-Request-Id");
    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }
    MDC.put(TRACE_ID, traceId);
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String user = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
      MDC.put(USER_ID, user);
      response.setHeader("X-Request-Id", traceId);
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(TRACE_ID);
      MDC.remove(USER_ID);
    }
  }
}
