package com.mythictales.bms.taplist.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mythictales.bms.taplist.repo.UserAccountRepository;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('SITE_ADMIN')")
public class AdminUserController {

  private final UserAccountRepository users;

  public AdminUserController(UserAccountRepository users) {
    this.users = users;
  }

  @GetMapping
  public String listUsers(Model model) {
    model.addAttribute("users", users.findAll());
    return "admin/users";
  }
}
