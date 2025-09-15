package com.mythictales.bms.taplist.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mythictales.bms.taplist.api.dto.UserDto;
import com.mythictales.bms.taplist.repo.UserAccountRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
@PreAuthorize("hasRole('SITE_ADMIN')")
public class UserApiController {
  private final UserAccountRepository users;

  public UserApiController(UserAccountRepository users) {
    this.users = users;
  }

  @GetMapping
  public Page<UserDto> list(
      @ParameterObject @PageableDefault(sort = "username") Pageable pageable) {
    return users.findAll(pageable).map(ApiMappers::toDto);
  }
}
