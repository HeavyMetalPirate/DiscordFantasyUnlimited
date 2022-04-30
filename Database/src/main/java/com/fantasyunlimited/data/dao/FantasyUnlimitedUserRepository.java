package com.fantasyunlimited.data.dao;

import com.fantasyunlimited.data.entity.FantasyUnlimitedUser;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FantasyUnlimitedUserRepository extends CrudRepository<FantasyUnlimitedUser, String> {
    public FantasyUnlimitedUser findByUserName(String userName);
    public FantasyUnlimitedUser findByUserEmail(String userEmail);

    @Query("select p from PlayerCharacter p where p.user = ?1")
    public List<PlayerCharacter> findPlayerCharacters(FantasyUnlimitedUser user);
}
