package com.mythictales.bms.taplist.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import com.mythictales.bms.taplist.logging.CorrelationIdFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  private final UserDetailsService userDetailsService;
  private final Environment env;
  private final CorsConfigurationSource corsConfigurationSource;
  private final CorrelationIdFilter correlationIdFilter;

  public SecurityConfig(
      DbUserDetailsService uds,
      Environment env,
      CorsConfigurationSource corsConfigurationSource,
      CorrelationIdFilter correlationIdFilter) {
    this.userDetailsService = uds;
    this.env = env;
    this.corsConfigurationSource = corsConfigurationSource;
    this.correlationIdFilter = correlationIdFilter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    var p = new DaoAuthenticationProvider();
    p.setUserDetailsService(userDetailsService);
    p.setPasswordEncoder(passwordEncoder());
    return p;
  }

  @Bean
  public AuthenticationSuccessHandler roleBasedSuccessHandler() {
    return (req, res, auth) -> {
      var roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
      if (roles.contains("ROLE_SITE_ADMIN")) {
        res.sendRedirect("/admin/site");
        return;
      }
      if (roles.contains("ROLE_BREWERY_ADMIN")) {
        res.sendRedirect("/admin/brewery");
        return;
      }
      if (roles.contains("ROLE_BAR_ADMIN")) {
        res.sendRedirect("/admin/bar");
        return;
      }
      if (roles.contains("ROLE_TAPROOM_ADMIN")) {
        res.sendRedirect("/admin/taproom");
        return;
      }
      SavedRequest saved = new HttpSessionRequestCache().getRequest(req, res);
      if (saved != null) {
        new SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(req, res, auth);
        return;
      }
      res.sendRedirect("/taplist");
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    boolean h2Enabled = env.getProperty("spring.h2.console.enabled", Boolean.class, false);
    boolean docsEnabled = env.getProperty("springdoc.enabled", Boolean.class, false);

    List<String> permitAllPatterns = new ArrayList<>();
    permitAllPatterns.addAll(List.of("/css/**", "/js/**", "/images/**", "/error/**", "/login"));
    if (h2Enabled) {
      permitAllPatterns.add("/h2-console/**");
    }
    if (docsEnabled) {
      permitAllPatterns.addAll(List.of("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"));
    }

    http.csrf(
            csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                        h2Enabled
                            ? new String[] {"/login", "/logout", "/h2-console/**"}
                            : new String[] {"/login", "/logout"}))
        .headers(h -> h.frameOptions(f -> f.sameOrigin()))
        .cors(c -> c.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(permitAllPatterns.toArray(new String[0]))
                    .permitAll()
                    .requestMatchers("/admin/site/**")
                    .hasRole("SITE_ADMIN")
                    .requestMatchers("/admin/brewery/**")
                    .hasRole("BREWERY_ADMIN")
                    .requestMatchers("/admin/bar/**")
                    .hasRole("BAR_ADMIN")
                    .requestMatchers("/admin/taproom/**")
                    .hasAnyRole("TAPROOM_ADMIN", "BREWERY_ADMIN", "BAR_ADMIN", "SITE_ADMIN")
                    .requestMatchers("/taplist", "/taplist/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .formLogin(
            l ->
                l.loginPage("/login")
                    .permitAll()
                    .failureUrl("/login?error")
                    .successHandler(roleBasedSuccessHandler()))
        .logout(
            l ->
                l.logoutUrl("/logout")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll())
        .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
