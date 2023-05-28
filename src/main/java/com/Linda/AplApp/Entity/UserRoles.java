package com.Linda.AplApp.Entity;

import jakarta.persistence.ManyToMany;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.Linda.AplApp.Entity.UserPermissions.*;

public enum UserRoles {
    USER(Set.of(USER_READ)),
    ADMIN(Set.of(ADMIN_READ, ADMIN_WRITE));

    // Variable
    private final Set<UserPermissions> permissionsList;

    // Constructor
    UserRoles(Set<UserPermissions> permissions) {
        this.permissionsList = permissions;
    }

    // Getter
    public Set<UserPermissions> getPermissions() {
        return permissionsList;
    }

    // Create list: [ROLE & PERMISSIONS]
    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {

        // Loop
        Set<SimpleGrantedAuthority> permissionsSet = getPermissions().stream().map(
                index -> new SimpleGrantedAuthority(index.getUserPermission())
        ).collect(Collectors.toSet());

        // Add Role      (example ROLE_ADMIN)
        permissionsSet.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return permissionsSet;
    }

    @ManyToMany(mappedBy = "Authorities")
    private List<User> users;
}
