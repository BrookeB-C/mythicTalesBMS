package com.mythictales.bms.taplist.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mythictales.bms.taplist.domain.Bar;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.domain.Taproom;
import com.mythictales.bms.taplist.domain.UserAccount;
import com.mythictales.bms.taplist.repo.BarRepository;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.TaproomRepository;
import com.mythictales.bms.taplist.repo.UserAccountRepository;
import com.mythictales.bms.taplist.security.CurrentUser;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('SITE_ADMIN')")
public class AdminUserController {

  private final UserAccountRepository users;
  private final BreweryRepository breweries;
  private final TaproomRepository taprooms;
  private final BarRepository bars;
  private final PasswordEncoder passwordEncoder;

  public AdminUserController(
      UserAccountRepository users,
      BreweryRepository breweries,
      TaproomRepository taprooms,
      BarRepository bars,
      PasswordEncoder passwordEncoder) {
    this.users = users;
    this.breweries = breweries;
    this.taprooms = taprooms;
    this.bars = bars;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public String listUsers(@AuthenticationPrincipal CurrentUser currentUser, Model model) {
    model.addAttribute("users", users.findAll());
    model.addAttribute("roles", Role.values());
    model.addAttribute("breweries", breweries.findAll());
    model.addAttribute("taprooms", taprooms.findAll());
    model.addAttribute("bars", bars.findAll());
    return "admin/users";
  }

  @PostMapping
  public String createUser(
      @RequestParam String username,
      @RequestParam String password,
      @RequestParam Role role,
      @RequestParam(name = "breweryId", required = false) Long breweryId,
      @RequestParam(name = "taproomId", required = false) Long taproomId,
      @RequestParam(name = "barId", required = false) Long barId,
      RedirectAttributes redirectAttributes) {
    String trimmedUsername = username == null ? "" : username.trim();
    if (trimmedUsername.isEmpty() || password == null || password.isBlank()) {
      redirectAttributes.addFlashAttribute(
          "errorMessage", "Username and password are required to create a user.");
      return "redirect:/admin/users";
    }
    if (users.findByUsername(trimmedUsername).isPresent()) {
      redirectAttributes.addFlashAttribute(
          "errorMessage", "Username already exists. Choose another." );
      return "redirect:/admin/users";
    }

    UserAccount account = new UserAccount();
    account.setUsername(trimmedUsername);
    account.setPassword(passwordEncoder.encode(password));
    account.setRole(role);

    switch (role) {
      case SITE_ADMIN -> {
        account.setBrewery(null);
        account.setTaproom(null);
        account.setBar(null);
      }
      case BREWERY_ADMIN -> {
        if (breweryId == null) {
          redirectAttributes.addFlashAttribute(
              "errorMessage", "Select a brewery for the new brewery admin user.");
          return "redirect:/admin/users";
        }
        Brewery brewery =
            breweries
                .findById(breweryId)
                .orElse(null);
        if (brewery == null) {
          redirectAttributes.addFlashAttribute(
              "errorMessage", "Unable to locate the selected brewery.");
          return "redirect:/admin/users";
        }
        account.setBrewery(brewery);
        account.setTaproom(null);
        account.setBar(null);
      }
      case TAPROOM_ADMIN, TAPROOM_USER -> {
        if (taproomId == null) {
          redirectAttributes.addFlashAttribute(
              "errorMessage", "Select a taproom for the taproom user.");
          return "redirect:/admin/users";
        }
        Taproom taproom = taprooms.findById(taproomId).orElse(null);
        if (taproom == null) {
          redirectAttributes.addFlashAttribute(
              "errorMessage", "Unable to locate the selected taproom.");
          return "redirect:/admin/users";
        }
        account.setTaproom(taproom);
        account.setBrewery(taproom.getBrewery());
        account.setBar(null);
      }
      case BAR_ADMIN -> {
        if (barId == null) {
          redirectAttributes.addFlashAttribute(
              "errorMessage", "Select a bar for the bar admin user.");
          return "redirect:/admin/users";
        }
        Bar bar = bars.findById(barId).orElse(null);
        if (bar == null) {
          redirectAttributes.addFlashAttribute(
              "errorMessage", "Unable to locate the selected bar.");
          return "redirect:/admin/users";
        }
        account.setBar(bar);
        account.setBrewery(bar.getBrewery());
        account.setTaproom(null);
      }
      default -> {
        redirectAttributes.addFlashAttribute(
            "errorMessage", "Unsupported role selection.");
        return "redirect:/admin/users";
      }
    }

    users.save(account);
    redirectAttributes.addFlashAttribute(
        "successMessage", "User %s created.".formatted(trimmedUsername));
    return "redirect:/admin/users";
  }

  @PostMapping("/{userId}/delete")
  public String deleteUser(
      @PathVariable Long userId,
      @AuthenticationPrincipal CurrentUser currentUser,
      RedirectAttributes redirectAttributes) {
    if (currentUser != null && currentUser.getId().equals(userId)) {
      redirectAttributes.addFlashAttribute(
          "errorMessage", "You cannot delete your own account while signed in.");
      return "redirect:/admin/users";
    }
    users
        .findById(userId)
        .ifPresentOrElse(
            user -> {
              if (user.getRole() == Role.SITE_ADMIN && users.findAll().stream().filter(u -> u.getRole() == Role.SITE_ADMIN).count() <= 1) {
                redirectAttributes.addFlashAttribute(
                    "errorMessage", "At least one site admin account must remain.");
              } else {
                users.delete(user);
                redirectAttributes.addFlashAttribute(
                    "successMessage", "User %s removed.".formatted(user.getUsername()));
              }
            },
            () ->
                redirectAttributes.addFlashAttribute(
                    "errorMessage", "Could not find the requested user."));
    return "redirect:/admin/users";
  }
}
