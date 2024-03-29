package com.fantasyunlimited.security;

import com.fantasyunlimited.data.entity.FantasyUnlimitedUser;
import com.fantasyunlimited.data.service.FantasyUnlimitedUserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsService.class);
    @Autowired
    private FantasyUnlimitedUserService service;

    @Override
    public UserDetails loadUserByUsername(String alias) {
        FantasyUnlimitedUser user = service.getUser(alias);
        if (user == null) {
            throw new RuntimeException("Bad credentials");
        }
        log.info("Found user {}", user);
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(), user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole()));
    }
}