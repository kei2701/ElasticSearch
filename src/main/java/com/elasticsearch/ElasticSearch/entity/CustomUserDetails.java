package com.elasticsearch.ElasticSearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static com.elasticsearch.ElasticSearch.constant.Role.STAFF;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Account account;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role;

        if (STAFF.equals(account.getRole())) {
            role = "STAFF";
        } else {
            role = "CUSTOMER";
        }

        return List.of((GrantedAuthority) () -> "ROLE_" + role);
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getAccountId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !account.isDeleted();
    }
}
