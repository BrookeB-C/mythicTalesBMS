package com.mythictales.bms.taplist.repo;
import com.mythictales.bms.taplist.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    java.util.List<UserAccount> findByBreweryId(Long breweryId);
    java.util.List<UserAccount> findByTaproom_Brewery_Id(Long breweryId);
    java.util.List<UserAccount> findByBar_Brewery_Id(Long breweryId);
    java.util.List<UserAccount> findByTaproomId(Long taproomId);
}
