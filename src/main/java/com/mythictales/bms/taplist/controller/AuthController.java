package com.mythictales.bms.taplist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {
  @GetMapping("/login")
  public String login(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    return "auth/login";
  }
}
