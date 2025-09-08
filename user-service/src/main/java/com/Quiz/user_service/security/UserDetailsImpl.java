package com.Quiz.user_service.security;

import com.Quiz.user_service.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final User user;
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Retourne les autorités (rôles) de l'utilisateur authentifié.
     * Cette méthode génère une liste d'autorités sous forme d'un {@link SimpleGrantedAuthority}
     * en fonction du rôle de l'utilisateur.
     *
     * @return Une collection contenant l'autorité de l'utilisateur, sous la forme de {@link GrantedAuthority}.
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
