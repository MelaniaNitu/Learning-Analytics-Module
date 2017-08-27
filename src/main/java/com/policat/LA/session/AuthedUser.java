package com.policat.LA.session;

import com.policat.LA.entities.User;
import com.policat.LA.enums.Role;
import org.springframework.security.core.authority.AuthorityUtils;

public class AuthedUser extends org.springframework.security.core.userdetails.User {
    private User user;

    public AuthedUser(User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return user.getRole();
    }
}