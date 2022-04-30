package com.fantasyunlimited.data.dao;

import com.fantasyunlimited.data.entity.FantasyUnlimitedUser;
import org.springframework.data.repository.CrudRepository;

public interface FantasyUnlimitedUserRepository extends CrudRepository<FantasyUnlimitedUser, String> {
    public FantasyUnlimitedUser findByUserName(String userName);
    public FantasyUnlimitedUser findByUserEmail(String userEmail);

}
