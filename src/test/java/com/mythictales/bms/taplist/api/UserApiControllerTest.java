package com.mythictales.bms.taplist.api;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

class UserApiControllerTest extends BaseApiControllerTest {

  private final UserAccountRepository users = mock(UserAccountRepository.class);
  private final BreweryRepository breweries = mock(BreweryRepository.class);
  private final TaproomRepository taprooms = mock(TaproomRepository.class);
  private final BarRepository bars = mock(BarRepository.class);
  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

  private UserApiController controller;
  private MockMvc mvc;
  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    controller = new UserApiController(users, breweries, taprooms, bars, passwordEncoder);
    mvc = buildMvc(controller);
  }

  @Test
  void listReturnsScopedUsersForBreweryAdmin() throws Exception {
    Brewery brewery = new Brewery("Mythic");
    brewery.setId(42L);

    UserAccount actor = new UserAccount("brewadmin", "pw", Role.BREWERY_ADMIN);
    actor.setId(100L);
    actor.setBrewery(brewery);

    UserAccount taproomUser = new UserAccount("tap-user", "x", Role.TAPROOM_ADMIN);
    taproomUser.setId(200L);
    taproomUser.setBrewery(null);
    Taproom taproom = new Taproom("Downtown", brewery);
    taproom.setId(77L);
    taproomUser.setTaproom(taproom);

    when(users.findByBreweryId(42L)).thenReturn(List.of());
    when(users.findByTaproom_Brewery_Id(42L)).thenReturn(List.of(taproomUser));
    when(users.findByBar_Brewery_Id(42L)).thenReturn(List.of());

    Page<UserDto> result =
        controller.list(new CurrentUser(actor), null, Pageable.unpaged());

    org.junit.jupiter.api.Assertions.assertEquals(1, result.getTotalElements());
    org.junit.jupiter.api.Assertions.assertEquals("tap-user", result.getContent().get(0).username());
  }

  @Test
  void listReturnsAllForSiteAdmin() throws Exception {
    UserAccount actor = new UserAccount("siteadmin", "pw", Role.SITE_ADMIN);
    actor.setId(1L);

    UserAccount breweryAdmin = new UserAccount("brewadmin", "pw", Role.BREWERY_ADMIN);
    breweryAdmin.setId(2L);
    Page<UserAccount> page = new PageImpl<>(new java.util.ArrayList<>(List.of(breweryAdmin)));
    when(users.findAll(any(Pageable.class))).thenReturn(page);

    Page<UserDto> result = controller.list(new CurrentUser(actor), null, Pageable.unpaged());

    org.junit.jupiter.api.Assertions.assertEquals(1, result.getTotalElements());
    org.junit.jupiter.api.Assertions.assertEquals("brewadmin", result.getContent().get(0).username());
  }

  @Test
  void siteAdminCanFilterByBrewery() {
    Brewery brewery = new Brewery("Mythic");
    brewery.setId(42L);

    UserAccount actor = new UserAccount("siteadmin", "pw", Role.SITE_ADMIN);
    actor.setId(1L);

    UserAccount barUser = new UserAccount("bar-user", "pw", Role.BAR_ADMIN);
    barUser.setId(5L);
    Bar bar = new Bar("Main Bar", brewery);
    bar.setId(90L);
    barUser.setBar(bar);
    barUser.setBrewery(null);

    when(users.findByBreweryId(42L)).thenReturn(List.of());
    when(users.findByTaproom_Brewery_Id(42L)).thenReturn(List.of());
    when(users.findByBar_Brewery_Id(42L)).thenReturn(List.of(barUser));

    Page<UserDto> result = controller.list(new CurrentUser(actor), 42L, Pageable.unpaged());

    org.junit.jupiter.api.Assertions.assertEquals(1, result.getTotalElements());
    org.junit.jupiter.api.Assertions.assertEquals("bar-user", result.getContent().get(0).username());
  }

  @Test
  void createUserForBreweryAdmin() throws Exception {
    Brewery brewery = new Brewery("Mythic");
    brewery.setId(42L);

    UserAccount actor = new UserAccount("brewadmin", "pw", Role.BREWERY_ADMIN);
    actor.setId(100L);
    actor.setBrewery(brewery);

    when(users.findByUsername("newadmin")).thenReturn(Optional.empty());
    when(breweries.findById(42L)).thenReturn(Optional.of(brewery));
    when(passwordEncoder.encode("secret")).thenReturn("ENCODED");
    when(users.save(any(UserAccount.class)))
        .thenAnswer(
            invocation -> {
              UserAccount saved = invocation.getArgument(0);
              saved.setId(501L);
              return saved;
            });

    CreateUserRequestDto payload =
        new CreateUserRequestDto("newadmin", "secret", "BREWERY_ADMIN", null, null, null);

    mvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", new CurrentUser(actor))
                .content(mapper.writeValueAsString(payload)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username", equalTo("newadmin")))
        .andExpect(jsonPath("$.breweryId", equalTo(42)));

    verify(users).save(any(UserAccount.class));
    verify(passwordEncoder).encode("secret");
  }

  @Test
  void createUserRejectsDuplicateUsername() throws Exception {
    when(users.findByUsername("dup")).thenReturn(Optional.of(new UserAccount()));

    CreateUserRequestDto payload =
        new CreateUserRequestDto("dup", "pw", "BREWERY_ADMIN", 1L, null, null);

    mvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", siteAdmin())
                .content(mapper.writeValueAsString(payload)))
        .andExpect(status().isUnprocessableEntity());

    verify(users, never()).save(any());
  }

  @Test
  void createUserBlocksDisallowedRoleForBreweryAdmin() throws Exception {
    Brewery brewery = new Brewery("Mythic");
    brewery.setId(42L);
    UserAccount actor = new UserAccount("brewadmin", "pw", Role.BREWERY_ADMIN);
    actor.setId(100L);
    actor.setBrewery(brewery);

    CreateUserRequestDto payload =
        new CreateUserRequestDto("another", "secret", "SITE_ADMIN", null, null, null);

    mvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", new CurrentUser(actor))
                .content(mapper.writeValueAsString(payload)))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateAssignsTaproom() throws Exception {
    Brewery brewery = new Brewery("Mythic");
    brewery.setId(42L);

    UserAccount actor = new UserAccount("brewadmin", "pw", Role.BREWERY_ADMIN);
    actor.setId(100L);
    actor.setBrewery(brewery);

    UserAccount target = new UserAccount("tap-user", "pw", Role.TAPROOM_ADMIN);
    target.setId(501L);
    target.setBrewery(brewery);

    Taproom taproom = new Taproom("Main", brewery);
    taproom.setId(77L);

    when(users.findById(501L)).thenReturn(Optional.of(target));
    when(taprooms.findById(77L)).thenReturn(Optional.of(taproom));
    when(users.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UpdateUserRequestDto payload = new UpdateUserRequestDto(null, null, null, 77L, null, null);

    mvc.perform(
            patch("/api/v1/users/{id}", 501L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", new CurrentUser(actor))
                .content(mapper.writeValueAsString(payload)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.taproomId", equalTo(77)));

    verify(users).save(any(UserAccount.class));
  }

  @Test
  void deleteUserRemovesRecord() throws Exception {
    Brewery brewery = new Brewery("Mythic");
    brewery.setId(42L);

    UserAccount actor = new UserAccount("brewadmin", "pw", Role.BREWERY_ADMIN);
    actor.setId(100L);
    actor.setBrewery(brewery);

    UserAccount target = new UserAccount("tap-user", "pw", Role.TAPROOM_ADMIN);
    target.setId(501L);
    target.setBrewery(brewery);

    when(users.findById(501L)).thenReturn(Optional.of(target));
    doNothing().when(users).delete(target);

    mvc.perform(
            delete("/api/v1/users/{id}", 501L)
                .requestAttr("currentUser", new CurrentUser(actor)))
        .andExpect(status().isNoContent());

    verify(users).delete(target);
  }

  private CurrentUser siteAdmin() {
    UserAccount actor = new UserAccount("siteadmin", "pw", Role.SITE_ADMIN);
    actor.setId(1L);
    return new CurrentUser(actor);
  }
}
