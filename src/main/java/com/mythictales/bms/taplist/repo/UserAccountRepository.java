package com.mythictales.bms.taplist.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByUsername(String username);

  java.util.List<UserAccount> findByBreweryId(Long breweryId);

  Page<UserAccount> findByBreweryId(Long breweryId, Pageable pageable);

  java.util.List<UserAccount> findByTaproom_Brewery_Id(Long breweryId);

  java.util.List<UserAccount> findByBar_Brewery_Id(Long breweryId);

  java.util.List<UserAccount> findByTaproomId(Long taproomId);
}
