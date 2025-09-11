package com.mythictales.bms.taplist.security;
import com.mythictales.bms.taplist.repo.UserAccountRepository;
import org.springframework.security.core.userdetails.*; import org.springframework.stereotype.Service;
@Service
public class DbUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;
    public DbUserDetailsService(UserAccountRepository users){ this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var ua = users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CurrentUser(ua);
    }
}
