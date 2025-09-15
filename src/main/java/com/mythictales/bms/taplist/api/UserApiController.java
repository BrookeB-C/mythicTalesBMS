package com.mythictales.bms.taplist.api;

import java.util.List;
import java.util.stream.Collectors;

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
  public List<UserDto> list() {
    return users.findAll().stream().map(ApiMappers::toDto).collect(Collectors.toList());
  }
}
