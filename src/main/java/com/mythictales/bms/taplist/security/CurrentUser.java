package com.mythictales.bms.taplist.security;
import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.domain.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection; import java.util.List;
public class CurrentUser implements UserDetails {
    private final Long id; private final String username; private final String password; private final Role role;
    private final Long breweryId; private final Long barId; private final Long taproomId;
    public CurrentUser(UserAccount ua){ id=ua.getId(); username=ua.getUsername(); password=ua.getPassword(); role=ua.getRole();
        breweryId = ua.getBrewery()!=null?ua.getBrewery().getId():null;
        barId = ua.getBar()!=null?ua.getBar().getId():null;
        taproomId = ua.getTaproom()!=null?ua.getTaproom().getId():null; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities(){ return List.of(new SimpleGrantedAuthority("ROLE_"+role.name())); }
    @Override public String getPassword(){ return password; }
    @Override public String getUsername(){ return username; }
    @Override public boolean isAccountNonExpired(){ return true; }
    @Override public boolean isAccountNonLocked(){ return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled(){ return true; }
    public Role getRole(){ return role; } public Long getBreweryId(){ return breweryId; } public Long getBarId(){ return barId; } public Long getTaproomId(){ return taproomId; } public Long getId(){ return id; }
}
