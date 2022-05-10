package com.fantasyunlimited.data.service;

import com.fantasyunlimited.data.dao.FantasyUnlimitedUserRepository;
import com.fantasyunlimited.data.dao.PlayerCharacterRepository;
import com.fantasyunlimited.data.entity.FantasyUnlimitedUser;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.enums.UserFoundStatus;
import com.fantasyunlimited.items.bags.*;
import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FantasyUnlimitedUserService {
    @Autowired
    private FantasyUnlimitedUserRepository repository;
    @Autowired
    private PlayerCharacterRepository characterRepository;
    @Autowired
    private ClassBag classBag;
    @Autowired
    private RaceBag raceBag;
    @Autowired
    private WeaponBag weaponBag;
    @Autowired
    private EquipmentBag equipmentBag;
    @Autowired
    private LocationBag locationBag;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean characterExistsByName(String name) {
        return characterRepository.findByNameIgnoreCase(name).isPresent();
    }

    @Transactional(readOnly = true)
    public PlayerCharacter getPlayerCharacterById(Long id) {
        return characterRepository.findById(id).get();
    }

    @Transactional
    public void createCharacter(FantasyUnlimitedUser user, String name, String classId, String raceId) throws IllegalArgumentException {
        PlayerCharacter character = new PlayerCharacter();
        character.setName(name);

        CharacterClass selectedClass = classBag.getItem(classId);
        Race selectedRace = raceBag.getItem(raceId);

        if(selectedClass.isHumanPlayable() == false || selectedRace.isHumanPlayable() == false) {
            throw new IllegalArgumentException("Selected a non-playable option.");
        }

        character.setRaceId(selectedRace);
        character.setClassId(selectedClass);

        // Set up Attributes
        character.getAttributes().setEndurance(selectedClass.getAttributes().getEndurance());
        character.getAttributes().setStrength(selectedClass.getAttributes().getStrength());
        character.getAttributes().setDexterity(selectedClass.getAttributes().getDexterity());
        character.getAttributes().setWisdom(selectedClass.getAttributes().getWisdom());
        character.getAttributes().setIntelligence(selectedClass.getAttributes().getIntelligence());
        character.getAttributes().setDefense(selectedClass.getAttributes().getDefense());
        character.getAttributes().setLuck(selectedClass.getAttributes().getLuck());
        character.getAttributes().setUnspent(0);

        // Set up starting equipment
        character.getEquipment().setCharacter(character);
        character.getEquipment().setMainhand(weaponBag.getItem(selectedClass.getStartingMainhand()));
        character.getEquipment().setOffhand(weaponBag.getItem(selectedClass.getStartingOffhand()));
        character.getEquipment().setHelmet(equipmentBag.getItem(selectedClass.getStartingHelmet()));
        character.getEquipment().setChest(equipmentBag.getItem(selectedClass.getStartingChest()));
        character.getEquipment().setGloves(equipmentBag.getItem(selectedClass.getStartingGloves()));
        character.getEquipment().setPants(equipmentBag.getItem(selectedClass.getStartingPants()));
        character.getEquipment().setBoots(equipmentBag.getItem(selectedClass.getStartingBoots()));
        character.getEquipment().setRing1(equipmentBag.getItem(selectedClass.getStartingRing1()));
        character.getEquipment().setRing2(equipmentBag.getItem(selectedClass.getStartingRing2()));
        character.getEquipment().setNeck(equipmentBag.getItem(selectedClass.getStartingNeck()));

        // Set up starting location
        character.setLocationId(locationBag.getItem(selectedRace.getStartingLocationId()));

        // Set up additional initial values
        character.setCurrentLevel(1);
        character.setCurrentXp(0);
        character.setCurrentHealth(character.getMaxHealth());
        if(selectedClass.getEnergyType() == CharacterClass.EnergyType.RAGE) {
            character.setCurrentAtkResource(0);
        }
        else {
            character.setCurrentAtkResource(character.getMaxAtkResource());
        }
        // Fetch user into transaction session and add character
        user = repository.findByUserName(user.getUserName());
        user.getCharacters().add(character);
        character.setUser(user);
        repository.save(user);
    }

    @Transactional(readOnly = true)
    public List<PlayerCharacter> getPlayerCharacters(FantasyUnlimitedUser user) {
        return repository.findPlayerCharacters(user);
    }

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
