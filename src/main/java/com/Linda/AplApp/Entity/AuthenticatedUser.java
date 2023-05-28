package com.Linda.AplApp.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthenticatedUser extends org.springframework.security.core.userdetails.User
{

    private static final long serialVersionUID = 1L;
    private User user;

    public AuthenticatedUser(User user)
    {
        super(user.getEmail(), user.getPassword(), getAuthorities(user));
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(User user)
    {
        Set<String> roleAndPermissions = new HashSet<>();
        List<SimpleGrantedAuthority> roles = user.getAuthorities();

        for (SimpleGrantedAuthority role : roles)
        {
            roleAndPermissions.add(role.getAuthority());
        }
        String[] roleNames = new String[roleAndPermissions.size()];
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleAndPermissions.toArray(roleNames));
        return authorities;
    }
}
