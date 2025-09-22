package com.mythictales.bms.taplist.api;

import com.mythictales.bms.taplist.api.dto.CreateUserRequestDto;
import com.mythictales.bms.taplist.api.dto.UpdateUserRequestDto;
import com.mythictales.bms.taplist.api.dto.UserDto;
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
import com.mythictales.bms.taplist.service.BusinessValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
@PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
@Validated
public class UserApiController {

  private static final Set<Role> BREWERY_MANAGEABLE_ROLES =
      Set.of(Role.BREWERY_ADMIN, Role.TAPROOM_ADMIN, Role.TAPROOM_USER, Role.BAR_ADMIN);

  private final UserAccountRepository users;
  private final BreweryRepository breweries;
  private final TaproomRepository taprooms;
  private final BarRepository bars;
  private final PasswordEncoder passwordEncoder;

  public UserApiController(
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

  @Operation(summary = "List users", description = "Returns users scoped to the requesting context")
  @GetMapping
  public Page<UserDto> list(
      @AuthenticationPrincipal CurrentUser currentUser,
      @RequestParam(value = "breweryId", required = false) Long breweryId,
      @ParameterObject @PageableDefault(sort = "username") Pageable pageable) {

    if (isBreweryAdmin(currentUser)) {
      Long scopedBreweryId = requireBreweryId(currentUser);
      return collectUsersForBrewery(scopedBreweryId, pageable);
    }

    if (breweryId != null) {
      return collectUsersForBrewery(breweryId, pageable);
    }

    return users.findAll(pageable).map(ApiMappers::toDto);
  }

  @Operation(summary = "Create a user for a brewery")
  @PostMapping
  public ResponseEntity<UserDto> create(
      @AuthenticationPrincipal CurrentUser currentUser,
      @Valid @RequestBody CreateUserRequestDto request) {

    String username = normalizeUsername(request.username());
    if (users.findByUsername(username).isPresent()) {
      throw new BusinessValidationException("Username already exists", username);
    }

    Role role = parseRole(request.role());
    boolean siteAdmin = isSiteAdmin(currentUser);

    Long breweryId =
        request.breweryId() != null
            ? request.breweryId()
            : (currentUser != null ? currentUser.getBreweryId() : null);
    if (breweryId == null && role != Role.SITE_ADMIN) {
      throw new BusinessValidationException("breweryId is required for non site-admin users");
    }

    if (!siteAdmin) {
      ensureBreweryScope(currentUser, breweryId);
      ensureBreweryRoleAllowed(role);
    }

    Brewery brewery =
        breweryId != null
            ? breweries
                .findById(breweryId)
                .orElseThrow(() -> new NoSuchElementException("Brewery not found"))
            : null;

    UserAccount account = new UserAccount();
    account.setUsername(username);
    account.setPassword(passwordEncoder.encode(request.password()));
    account.setRole(role);
    account.setBrewery(brewery);

    applyAssignmentsOnCreate(account, role, request.taproomId(), request.barId(), breweryId);

    UserAccount saved = users.save(account);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toDto(saved));
  }

  @Operation(summary = "Update a user's access or assignments")
  @PatchMapping("/{id}")
  public UserDto update(
      @AuthenticationPrincipal CurrentUser currentUser,
      @PathVariable @NotNull Long id,
      @Valid @RequestBody UpdateUserRequestDto request) {

    UserAccount account =
        users.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));

    boolean siteAdmin = isSiteAdmin(currentUser);
    ensureUserScope(currentUser, account);

    if (request.expectedVersion() != null) {
      throw new BusinessValidationException(
          "Optimistic concurrency not supported for users", request.expectedVersion());
    }

    if (request.password() != null && !request.password().isBlank()) {
      account.setPassword(passwordEncoder.encode(request.password()));
    }

    Role role = account.getRole();
    if (request.role() != null) {
      Role updatedRole = parseRole(request.role());
      if (!siteAdmin) {
        ensureBreweryRoleAllowed(updatedRole);
      }
      role = updatedRole;
      account.setRole(role);
    }

    Long targetBreweryId = account.getBrewery() != null ? account.getBrewery().getId() : null;
    if (request.breweryId() != null) {
      Long requestedBreweryId = request.breweryId();
      if (!siteAdmin) {
        ensureBreweryScope(currentUser, requestedBreweryId);
      }
      Brewery newBrewery =
          requestedBreweryId != null
              ? breweries
                  .findById(requestedBreweryId)
                  .orElseThrow(() -> new NoSuchElementException("Brewery not found"))
              : null;
      account.setBrewery(newBrewery);
      targetBreweryId = requestedBreweryId;
    }

    applyAssignmentsOnUpdate(account, role, request, targetBreweryId, siteAdmin);

    UserAccount saved = users.save(account);
    return ApiMappers.toDto(saved);
  }

  @Operation(summary = "Delete a user")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal CurrentUser currentUser, @PathVariable @NotNull Long id) {
    UserAccount account =
        users.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
    ensureUserScope(currentUser, account);
    if (!isSiteAdmin(currentUser) && account.getRole() == Role.SITE_ADMIN) {
      throw new AccessDeniedException("Cannot delete site admin accounts");
    }
    users.delete(account);
    return ResponseEntity.noContent().build();
  }

  private void applyAssignmentsOnCreate(
      UserAccount account, Role role, Long taproomId, Long barId, Long breweryId) {
    switch (role) {
      case SITE_ADMIN -> {
        if (taproomId != null || barId != null || breweryId != null) {
          throw new BusinessValidationException(
              "Site admins cannot be scoped to brewery, taproom, or bar");
        }
        account.setBrewery(null);
        account.setTaproom(null);
        account.setBar(null);
      }
      case BREWERY_ADMIN -> {
        account.setTaproom(null);
        account.setBar(null);
        ensureBreweryPresent(account);
      }
      case TAPROOM_ADMIN, TAPROOM_USER -> {
        if (taproomId == null) {
          throw new BusinessValidationException("taproomId is required for taproom roles");
        }
        Taproom taproom =
            taprooms
                .findById(taproomId)
                .orElseThrow(() -> new NoSuchElementException("Taproom not found"));
        ensureSameBrewery("Taproom", taproom.getBrewery(), breweryId);
        account.setTaproom(taproom);
        account.setBar(null);
        account.setBrewery(taproom.getBrewery());
      }
      case BAR_ADMIN -> {
        if (barId == null) {
          throw new BusinessValidationException("barId is required for BAR_ADMIN role");
        }
        Bar bar = bars.findById(barId).orElseThrow(() -> new NoSuchElementException("Bar not found"));
        ensureSameBrewery("Bar", bar.getBrewery(), breweryId);
        account.setBar(bar);
        account.setTaproom(null);
        account.setBrewery(bar.getBrewery());
      }
      default -> throw new BusinessValidationException("Unsupported role: " + role);
    }
  }

  private void applyAssignmentsOnUpdate(
      UserAccount account,
      Role role,
      UpdateUserRequestDto request,
      Long currentBreweryId,
      boolean siteAdmin) {

    if (role == Role.SITE_ADMIN) {
      account.setTaproom(null);
      account.setBar(null);
      account.setBrewery(null);
      if (request.taproomId() != null || request.barId() != null) {
        throw new BusinessValidationException(
            "Site admins cannot be scoped to brewery, taproom, or bar");
      }
      return;
    }

    Long breweryId =
        request.breweryId() != null ? request.breweryId() : currentBreweryId != null ? currentBreweryId : null;
    if (role == Role.BREWERY_ADMIN) {
      if (request.taproomId() != null || request.barId() != null) {
        throw new BusinessValidationException(
            "Taproom or bar assignments are not applicable to brewery admins");
      }
      if (breweryId == null) {
        throw new BusinessValidationException("breweryId is required for brewery admins");
      }
      ensureBreweryPresent(account);
      return;
    }

    if (role == Role.BAR_ADMIN) {
      if (request.taproomId() != null) {
        throw new BusinessValidationException("Taproom assignment not allowed for BAR_ADMIN");
      }
      if (request.barId() != null) {
        Bar bar = bars.findById(request.barId()).orElseThrow(() -> new NoSuchElementException("Bar not found"));
        ensureSameBrewery("Bar", bar.getBrewery(), breweryId);
        account.setBar(bar);
        account.setBrewery(bar.getBrewery());
        account.setTaproom(null);
      } else if (account.getBar() == null) {
        throw new BusinessValidationException("BAR_ADMIN users require a bar assignment");
      } else {
        ensureSameBrewery("Bar", account.getBar().getBrewery(), breweryId);
      }
      return;
    }

    if (role == Role.TAPROOM_ADMIN || role == Role.TAPROOM_USER) {
      if (request.barId() != null) {
        throw new BusinessValidationException("Bar assignment not allowed for taproom roles");
      }
      if (request.taproomId() != null) {
        Taproom taproom =
            taprooms
                .findById(request.taproomId())
                .orElseThrow(() -> new NoSuchElementException("Taproom not found"));
        ensureSameBrewery("Taproom", taproom.getBrewery(), breweryId);
        account.setTaproom(taproom);
        account.setBar(null);
        account.setBrewery(taproom.getBrewery());
      } else if (account.getTaproom() == null) {
        throw new BusinessValidationException("Taproom assignment required for taproom roles");
      } else {
        ensureSameBrewery("Taproom", account.getTaproom().getBrewery(), breweryId);
      }
      return;
    }

    if (!siteAdmin) {
      throw new BusinessValidationException("Unsupported role update");
    }
  }

  private void ensureBreweryScope(CurrentUser currentUser, Long breweryId) {
    if (breweryId == null) {
      throw new BusinessValidationException("Brewery scope is required");
    }
    if (!Objects.equals(requireBreweryId(currentUser), breweryId)) {
      throw new AccessDeniedException("Access denied for brewery scope");
    }
  }

  private void ensureUserScope(CurrentUser currentUser, UserAccount account) {
    if (isSiteAdmin(currentUser)) {
      return;
    }
    Long breweryId = requireBreweryId(currentUser);
    if (account.getBrewery() == null
        || account.getBrewery().getId() == null
        || !Objects.equals(account.getBrewery().getId(), breweryId)) {
      throw new AccessDeniedException("Access denied for user");
    }
    ensureBreweryRoleAllowed(account.getRole());
  }

  private void ensureBreweryRoleAllowed(Role role) {
    if (!BREWERY_MANAGEABLE_ROLES.contains(role)) {
      throw new AccessDeniedException("Role not manageable at brewery scope");
    }
  }

  private void ensureSameBrewery(String type, Brewery brewery, Long expectedBreweryId) {
    if (expectedBreweryId == null) {
      return;
    }
    if (brewery == null || brewery.getId() == null) {
      throw new BusinessValidationException(type + " is not linked to a brewery");
    }
    if (!Objects.equals(brewery.getId(), expectedBreweryId)) {
      throw new AccessDeniedException(type + " outside of brewery scope");
    }
  }

  private void ensureBreweryPresent(UserAccount account) {
    if (account.getBrewery() == null) {
      throw new BusinessValidationException("breweryId is required for this role");
    }
  }

  private Role parseRole(String raw) {
    try {
      return Role.valueOf(raw.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new BusinessValidationException("Unknown role: " + raw);
    }
  }

  private String normalizeUsername(String username) {
    String trimmed = Optional.ofNullable(username).map(String::trim).orElse("");
    if (trimmed.isEmpty()) {
      throw new BusinessValidationException("username is required");
    }
    return trimmed;
  }

  private boolean isSiteAdmin(CurrentUser currentUser) {
    return currentUser != null && currentUser.getRole() == Role.SITE_ADMIN;
  }

  private boolean isBreweryAdmin(CurrentUser currentUser) {
    return currentUser != null && currentUser.getRole() == Role.BREWERY_ADMIN;
  }

  private Long requireBreweryId(CurrentUser currentUser) {
    if (currentUser == null || currentUser.getBreweryId() == null) {
      throw new AccessDeniedException("Brewery context required");
    }
    return currentUser.getBreweryId();
  }

  private Page<UserDto> collectUsersForBrewery(Long breweryId, Pageable pageable) {
    if (breweryId == null) {
      return Page.empty(pageable);
    }

    LinkedHashMap<Long, UserAccount> deduped = new LinkedHashMap<>();
    addUsers(deduped, users.findByBreweryId(breweryId));
    addUsers(deduped, users.findByTaproom_Brewery_Id(breweryId));
    addUsers(deduped, users.findByBar_Brewery_Id(breweryId));

    List<UserAccount> combined = new ArrayList<>(deduped.values());
    sortUsers(combined, pageable);

    int total = combined.size();
    if (pageable.isUnpaged()) {
      List<UserDto> dtos =
          combined.stream().map(ApiMappers::toDto).collect(Collectors.toList());
      return new PageImpl<>(dtos, Pageable.unpaged(), total);
    }

    int pageSize = pageable.getPageSize();
    int offset = (int) pageable.getOffset();
    if (offset >= total) {
      return new PageImpl<>(List.of(), pageable, total);
    }

    int toIndex = Math.min(offset + pageSize, total);
    List<UserDto> slice =
        combined.subList(offset, toIndex).stream().map(ApiMappers::toDto).collect(Collectors.toList());

    return new PageImpl<>(slice, pageable, total);
  }

  private void addUsers(LinkedHashMap<Long, UserAccount> map, List<UserAccount> candidates) {
    if (candidates == null) {
      return;
    }
    for (UserAccount account : candidates) {
      if (account == null || account.getId() == null) {
        continue;
      }
      map.putIfAbsent(account.getId(), account);
    }
  }

  private void sortUsers(List<UserAccount> usersToSort, Pageable pageable) {
    Comparator<UserAccount> comparator = null;
    for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
      Comparator<UserAccount> next = comparatorFor(order.getProperty());
      if (next == null) {
        continue;
      }
      if (order.isDescending()) {
        next = next.reversed();
      }
      comparator = comparator == null ? next : comparator.thenComparing(next);
    }

    if (comparator == null) {
      comparator = comparatorFor("username");
    }

    if (comparator != null) {
      usersToSort.sort(comparator);
    }
  }

  private Comparator<UserAccount> comparatorFor(String property) {
    if (property == null) {
      return null;
    }
    return switch (property) {
      case "username" ->
          Comparator.comparing(
              UserAccount::getUsername,
              Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
      case "role" ->
          Comparator.comparing(
              user -> user.getRole() != null ? user.getRole().name() : "",
              Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
      case "id" -> Comparator.comparing(UserAccount::getId, Comparator.nullsLast(Long::compareTo));
      default -> null;
    };
  }
}
