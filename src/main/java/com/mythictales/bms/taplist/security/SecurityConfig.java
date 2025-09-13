package com.mythictales.bms.taplist.security;
import org.springframework.context.annotation.*; import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*; import org.springframework.security.web.authentication.*; import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache; import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    public SecurityConfig(DbUserDetailsService uds){ this.userDetailsService = uds; }
    @Bean public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean public DaoAuthenticationProvider authenticationProvider(){ var p=new DaoAuthenticationProvider(); p.setUserDetailsService(userDetailsService); p.setPasswordEncoder(passwordEncoder()); return p; }
    @Bean public AuthenticationSuccessHandler roleBasedSuccessHandler(){ return (req,res,auth)->{
        var roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        if (roles.contains("ROLE_SITE_ADMIN"))    { res.sendRedirect("/admin/site");    return; }
        if (roles.contains("ROLE_BREWERY_ADMIN")) { res.sendRedirect("/admin/brewery"); return; }
        if (roles.contains("ROLE_BAR_ADMIN"))     { res.sendRedirect("/admin/bar");     return; }
        if (roles.contains("ROLE_TAPROOM_ADMIN")) { res.sendRedirect("/admin/taproom"); return; }
        SavedRequest saved = new HttpSessionRequestCache().getRequest(req,res);
        if (saved != null) { new SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(req,res,auth); return; }
        res.sendRedirect("/taplist");
    };}
    @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf->csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                         .ignoringRequestMatchers("/login","/logout","/h2-console/**"))
          .headers(h->h.frameOptions(f->f.sameOrigin()))
          .authorizeHttpRequests(auth->auth
              .requestMatchers("/css/**","/js/**","/images/**","/h2-console/**","/error/**").permitAll()
              .requestMatchers("/login").permitAll()
              .requestMatchers("/admin/site/**").hasRole("SITE_ADMIN")
              .requestMatchers("/admin/brewery/**").hasRole("BREWERY_ADMIN")
              .requestMatchers("/admin/bar/**").hasRole("BAR_ADMIN")
              .requestMatchers("/admin/taproom/**").hasAnyRole("TAPROOM_ADMIN","BREWERY_ADMIN","BAR_ADMIN","SITE_ADMIN")
              .requestMatchers("/taplist","/taplist/**").authenticated()
              .anyRequest().permitAll())
          .formLogin(l->l.loginPage("/login").permitAll().failureUrl("/login?error").successHandler(roleBasedSuccessHandler()))
          .logout(l->l.logoutUrl("/logout").logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))
                      .logoutSuccessUrl("/login?logout").permitAll());
        return http.build();
    }
}
