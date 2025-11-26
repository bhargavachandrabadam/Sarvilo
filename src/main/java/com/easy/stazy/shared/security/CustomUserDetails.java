package com.easy.stazy.shared.security;

import com.easy.stazy.pgmanagement.owner.entities.PgOwnerEntity;
import com.easy.stazy.authentication.dto.RoleType;
import com.easy.stazy.authentication.entities.UsersEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 4268114951063693694L;

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isAccountEnabled;
    private final String userType;
    private final PgOwnerEntity pgOwnerEntity;
    private final UsersEntity usersEntity;

    // Constructor for PgOwnerEntity
    public CustomUserDetails(PgOwnerEntity pgOwner) {
        this.username = pgOwner.getOwnerContactNumber();
        this.password = pgOwner.getPasswordHash();
        this.authorities = List.of(new SimpleGrantedAuthority("OWNER"));
        this.isAccountEnabled = pgOwner.getIsActive() != null ? pgOwner.getIsActive() : false;
        this.userType = RoleType.OWNER.toString();
        this.pgOwnerEntity = pgOwner;
        this.usersEntity = null;
    }

    // Constructor for UserEntity
    public CustomUserDetails(UsersEntity user) {
        this.username = user.getPhoneNumber();
        this.password = user.getPasswordHash();
        this.authorities = List.of(new SimpleGrantedAuthority("USER"));
        this.isAccountEnabled = user.getIsActive() != null ? user.getIsActive() : false;
        this.userType = RoleType.USER.toString();
        this.pgOwnerEntity = null;
        this.usersEntity = user;
    }

    // Standard UserDetails interface methods...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return isAccountEnabled;
    }

    public PgOwnerEntity pgOwnerEntity() {
        return pgOwnerEntity;
    }

    public UsersEntity usersEntity() {
        return usersEntity;
    }
}
