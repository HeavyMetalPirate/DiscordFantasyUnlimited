package com.fantasyunlimited.data.service;

import com.fantasyunlimited.data.dao.FantasyUnlimitedUserRepository;
import com.fantasyunlimited.data.entity.FantasyUnlimitedUser;
import com.fantasyunlimited.data.enums.UserFoundStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FantasyUnlimitedUserService {
    @Autowired
    private FantasyUnlimitedUserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public FantasyUnlimitedUser getUser(String alias) {
        FantasyUnlimitedUser user;

        //Find by username first
        user = repository.findByUserName(alias);
        if( user == null ) {
            // Then find by email
            user = repository.findByUserEmail(alias);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserFoundStatus userExists(String username, String email) {
        if(repository.findByUserName(username) != null) return UserFoundStatus.BY_NAME;
        if(repository.findByUserEmail(email) != null) return UserFoundStatus.BY_EMAIL;

        return UserFoundStatus.NOT_FOUND;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public FantasyUnlimitedUser createUser(FantasyUnlimitedUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }
}
