package com.fantasyunlimited.data.service;

import com.fantasyunlimited.data.converter.PlayerCharacterConverter;
import com.fantasyunlimited.data.dao.PlayerCharacterRepository;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.bags.ConsumablesBag;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.items.util.DropableUtils;
import com.fantasyunlimited.rest.dto.EquipmentSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class PlayerCharacterService {
    private Logger log = LoggerFactory.getLogger(PlayerCharacterService.class);
    @Autowired
    private PlayerCharacterRepository repository;
    @Autowired
    private ConsumablesBag consumablesBag;
    @Autowired
    private DropableUtils dropableUtils;

    private Map<Integer, Integer> experienceTable;

    @PostConstruct
    public void initializeExperienceTable() {
        experienceTable = new HashMap<>();
        for (int i = 1; i < 100; i++) {
            double log = (Math.log(i) / Math.log(2));
            int experience = (int) Math.floor(Math.pow(i, 2) * 100 * log);
            experienceTable.put(i, experience);
        }
    }

    @Transactional
    public boolean addExperience(Long id, int experience) {
        return addExperience(repository.findById(id).get(), experience);
    }
    @Transactional
    public boolean addExperience(PlayerCharacter selectedCharacter, int experience) {
        if(selectedCharacter == null) return false;

        PlayerCharacter character = repository.findById(selectedCharacter.getId()).get();

        character.setCurrentXp(character.getCurrentXp() + experience);
        int oldLevel = character.getCurrentLevel();

        if (character.getCurrentXp() >= getExperienceForNextLevel(oldLevel)
                && oldLevel < 100) {
            character.setCurrentLevel(oldLevel + 1);
            character.getAttributes().raiseUnspent(5);

            character.getAttributes().raiseEndurance(character.getClassId().getAttributes().getEnduranceGrowth());
            character.getAttributes().raiseStrength(character.getClassId().getAttributes().getStrengthGrowth());
            character.getAttributes().raiseDexterity(character.getClassId().getAttributes().getDexterityGrowth());
            character.getAttributes().raiseWisdom(character.getClassId().getAttributes().getWisdomGrowth());
            character.getAttributes().raiseIntelligence(character.getClassId().getAttributes().getIntelligenceGrowth());
            character.getAttributes().raiseDefense(character.getClassId().getAttributes().getDefenseGrowth());
            character.getAttributes().raiseLuck(character.getClassId().getAttributes().getLuckGrowth());

            character.setCurrentHealth(character.getMaxHealth());
        }

        character = repository.save(character);

        return oldLevel != character.getCurrentLevel();
    }

    public Integer getExperienceForNextLevel(int currentLevel) {
        if (currentLevel == 100) {
            return 0;
        }
        int nextLevel = currentLevel + 1;
        return experienceTable.get(nextLevel);
    }

    @Transactional(readOnly = true)
    public PlayerCharacter findCharacter(Long id) {
        return repository.findById(id).orElse(null);
    }

    public PlayerCharacter addGold(Long characterId, int gold) {
        PlayerCharacter character = repository.findById(characterId).get();
        character.addGold(gold);
        return repository.save(character);
    }

    @Transactional
    public PlayerCharacter updateHealthAndAtk(long characterId, int newHealth, int newAtk) {
        PlayerCharacter character = repository.findById(characterId).get();
        character.setCurrentHealth(newHealth);
        if(character.getCurrentHealth() < 0)
            character.setCurrentHealth(0);
        else if(character.getCurrentHealth() > character.getMaxHealth())
            character.setCurrentHealth(character.getMaxHealth());

        character.setCurrentAtkResource(newAtk);
        if(character.getCurrentAtkResource() < 0)
            character.setCurrentAtkResource(0);
        else if(character.getCurrentAtkResource() > character.getMaxAtkResource())
            character.setCurrentAtkResource(character.getMaxAtkResource());

        return repository.save(character);
    }

    @Transactional
    public PlayerCharacter equipItem(PlayerCharacter selectedCharacter, String itemId, EquipmentSlot slot) throws IllegalArgumentException {
        PlayerCharacter character = repository.findById(selectedCharacter.getId()).get();

        if(character.getInventory().containsKey(itemId) == false ||
           character.getInventory().get(itemId) == 0) {
            throw new IllegalArgumentException("Item '" + itemId + "' not in inventory.");
        }

        Dropable dropable = dropableUtils.getDropableItem(itemId);
        if(dropable == null) {
            throw new IllegalArgumentException("Item '" + itemId + "' not found.");
        }

        if(dropable instanceof Gear gear) {
            if(isRaceAllowed(character,gear) == false)
                throw new IllegalArgumentException("Item not allowed for character because of Race Restriction.");
            if(isClassAllowed(character,gear) == false)
                throw new IllegalArgumentException("Item not allowed for character because of Class Restriction.");
        }
        else {
            throw new IllegalArgumentException("Item is not assignable to class 'Gear': " + dropable);
        }

        if(dropable instanceof Weapon weapon) {
            checkWeaponSlot(weapon, slot);

            // Get current weapons
            Weapon currentMain = character.getEquipment().getMainhand();
            Weapon currentOff = character.getEquipment().getOffhand();

            // Handle unequipment of Weapon
            if(weapon.getHand() == Weapon.Hand.TWOHANDED) {
                // New weapon is twohanded, so we need to unequip both
                if (currentMain != null)
                    character = unequipItemFromSlot(character, EquipmentSlot.MAINHAND);
                if (currentOff != null)
                    character = unequipItemFromSlot(character, EquipmentSlot.OFFHAND);
            }
            else if(weapon.getHand() == Weapon.Hand.RIGHT) {
                if (currentMain != null)
                    character = unequipItemFromSlot(character, EquipmentSlot.MAINHAND);
            }
            else if(weapon.getHand() == Weapon.Hand.LEFT) {
                // unequip a current offhand, and also unequip a two handed weapon in the mainhand
                if (currentOff != null)
                    character = unequipItemFromSlot(character, EquipmentSlot.OFFHAND);
                else if(currentMain != null && currentMain.getHand() == Weapon.Hand.TWOHANDED)
                    character = unequipItemFromSlot(character, EquipmentSlot.MAINHAND);
            }
            else {
                // unequip the requested hand
                // Also unequip a two handed weapon if requested slot is the offhand
                if(slot == EquipmentSlot.MAINHAND && currentMain != null)
                    character = unequipItemFromSlot(character, EquipmentSlot.MAINHAND);
                else if(slot == EquipmentSlot.OFFHAND && currentOff != null)
                    character = unequipItemFromSlot(character, EquipmentSlot.OFFHAND);
                else if(slot == EquipmentSlot.OFFHAND && currentMain != null && currentMain.getHand() == Weapon.Hand.TWOHANDED)
                    character = unequipItemFromSlot(character, EquipmentSlot.MAINHAND);
            }

            // Equip the actual weapon
            switch (slot) {
                case MAINHAND -> character.getEquipment().setMainhand(weapon);
                case OFFHAND -> character.getEquipment().setOffhand(weapon);
                default -> throw new IllegalArgumentException("Wrong Slot AND somehow passed the checks");
            }

        }
        else if(dropable instanceof Equipment equipment) {
            Equipment current = checkEquipmentSlot(character, equipment, slot);
            if(current != null) character = unequipItemFromSlot(character, slot);

            // Equip the body equipment to its correct slot
            switch(slot) {
                case HELMET -> character.getEquipment().setHelmet(equipment);
                case CHEST -> character.getEquipment().setChest(equipment);
                case GLOVES -> character.getEquipment().setGloves(equipment);
                case PANTS -> character.getEquipment().setPants(equipment);
                case BOOTS -> character.getEquipment().setBoots(equipment);
                case RING1 -> character.getEquipment().setRing1(equipment);
                case RING2 -> character.getEquipment().setRing2(equipment);
                case NECK -> character.getEquipment().setNeck(equipment);
                default -> throw new IllegalArgumentException("Wrong Slot AND somehow passed the checks");
            }
        }
        else {
            throw new IllegalArgumentException("Item '" + itemId + "' cannot be equipped.");
        }

        int inventoryCount = character.getInventory().get(itemId);
        int newCount = inventoryCount - 1;
        character.getInventory().put(itemId, newCount);

        alignHealthAndAtkResource(character);
        return repository.save(character);
    }

    private boolean isClassAllowed(PlayerCharacter character, Gear gear) {
        // no restriction check => is allowed
        if(gear.getClassExclusive() == null ||gear.getClassExclusive().isEmpty())
            return true;

        // restriction on same class => is allowed
        if(gear.getClassExclusive().equals(character.getClassId()))
            return true;

        // not allowed
        return false;
    }
    private boolean isRaceAllowed(PlayerCharacter character, Gear gear) {
        // no restriction check => is allowed
        if(gear.getRaceExclusive() == null ||gear.getRaceExclusive().isEmpty())
            return true;

        // restriction on same race => allowed
        if(gear.getRaceExclusive().equals(character.getRaceId()))
            return true;

        // not allowed
        return false;
    }

    private Equipment checkEquipmentSlot(PlayerCharacter character, Equipment equipment, EquipmentSlot slot) throws IllegalArgumentException {
        switch(equipment.getType()) {
            case HELMET -> {
                if(slot != EquipmentSlot.HELMET)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + equipment.getId());
                return character.getEquipment().getHelmet();
            }
            case CHEST -> {
                if(slot != EquipmentSlot.CHEST)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + equipment.getId());
                return character.getEquipment().getChest();
            }
            case GLOVES -> {
                if(slot != EquipmentSlot.GLOVES)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + equipment.getId());
                return character.getEquipment().getGloves();
            }
            case PANTS -> {
                if(slot != EquipmentSlot.PANTS)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + equipment.getId());
                return character.getEquipment().getPants();
            }
            case BOOTS -> {
                if(slot != EquipmentSlot.BOOTS)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + equipment.getId());
                return character.getEquipment().getBoots();
            }
            case RING -> {
                if(slot != EquipmentSlot.RING1 && slot != EquipmentSlot.RING2)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + equipment.getId());

                if(slot == EquipmentSlot.RING1) return character.getEquipment().getRing1();
                else return character.getEquipment().getRing2();
            }
            case NECK -> {
                if(slot != EquipmentSlot.NECK)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + equipment.getId());
                return character.getEquipment().getNeck();
            }
        }
        return null;
    }
    private void checkWeaponSlot(Weapon weapon, EquipmentSlot slot) throws IllegalArgumentException{
        switch(weapon.getHand()) {
            case RIGHT, TWOHANDED -> {
                if (slot != EquipmentSlot.MAINHAND)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + weapon.getId());
            }
            case LEFT -> {
                if (slot != EquipmentSlot.OFFHAND)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + weapon.getId());
            }
            case BOTH -> {
                if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND)
                    throw new IllegalArgumentException("Wrong slot " + slot + " for item " + weapon.getId());
            }
        }
    }

    @Transactional
    public PlayerCharacter unequipItemFromSlot(PlayerCharacter selectedCharacter, EquipmentSlot slot) throws IllegalArgumentException{
        PlayerCharacter character = repository.findById(selectedCharacter.getId()).get();

        Gear item;
        switch (slot) {
            case HELMET -> {
                item = character.getEquipment().getHelmet();
                character.getEquipment().setHelmet(null);
            }
            case CHEST -> {
                item = character.getEquipment().getChest();
                character.getEquipment().setChest(null);
            }
            case GLOVES -> {
                item = character.getEquipment().getGloves();
                character.getEquipment().setGloves(null);
            }
            case PANTS -> {
                item = character.getEquipment().getPants();
                character.getEquipment().setPants(null);
            }
            case BOOTS -> {
                item = character.getEquipment().getBoots();
                character.getEquipment().setBoots(null);
            }
            case RING1 -> {
                item = character.getEquipment().getRing1();
                character.getEquipment().setRing1(null);
            }
            case RING2 -> {
                item = character.getEquipment().getRing2();
                character.getEquipment().setRing2(null);
            }
            case NECK -> {
                item = character.getEquipment().getNeck();
                character.getEquipment().setNeck(null);
            }
            case MAINHAND -> {
                item = character.getEquipment().getMainhand();
                character.getEquipment().setMainhand(null);
            }
            case OFFHAND -> {
                item = character.getEquipment().getOffhand();
                character.getEquipment().setOffhand(null);
            }
            default -> item = null;
        }

        if(item == null) {
            throw new IllegalArgumentException("Selected slot " + slot + " is empty.");
        }

        if(character.getInventory().containsKey(item.getId()) == false) {
            character.getInventory().put(item.getId(), 1);
        }
        else {
            int currentCount = character.getInventory().get(item.getId());
            character.getInventory().put(item.getId(), currentCount + 1);
        }

        alignHealthAndAtkResource(character);
        return repository.save(character);
    }

    private void alignHealthAndAtkResource(PlayerCharacter character) {
        int maxHealth = character.getMaxHealth();
        int currentHealth = character.getCurrentHealth();
        if (currentHealth > maxHealth)
            character.setCurrentHealth(maxHealth);

        int maxAtkRes = character.getMaxAtkResource();
        int currentAtkRes = character.getCurrentAtkResource();
        if (currentAtkRes > maxAtkRes)
            character.setCurrentAtkResource(maxAtkRes);
    }

    @Transactional
    public PlayerCharacter dropItems(PlayerCharacter selectedCharacter, String itemId, int count) {
        // read into transaction
        PlayerCharacter character = repository.findById(selectedCharacter.getId()).get();
        // Remove item from inventory
        character = removeItemFromInventory(character, itemId, count, "Dropping");

        return repository.save(character);
    }

    @Transactional
    public PlayerCharacter addItemToInventory(Long id, Dropable item, int count) {
        return addItemToInventory(repository.findById(id).get(), item, count);
    }

    @Transactional
    public PlayerCharacter addItemToInventory(PlayerCharacter selectedCharacter, Dropable item, int count) {
        if(selectedCharacter == null) return null;

        // read into transaction
        PlayerCharacter character = repository.findById(selectedCharacter.getId()).get();

        if(character.getInventory().containsKey(item.getId())) {
            int currentCount = character.getInventory().get(item.getId());
            character.getInventory().put(item.getId(), currentCount + count);
        }
        else {
            character.getInventory().put(item.getId(), count);
        }
        return repository.save(character);
    }

    @Transactional
    public PlayerCharacter useItemsFromInventory(PlayerCharacter selectedCharacter, String itemId) {
        // read into transaction
        PlayerCharacter character = repository.findById(selectedCharacter.getId()).get();

        if(character.getInventory().get(itemId) <= 0 ) {
            throw new IllegalStateException("Item count for item " + itemId + " is below or equal 0.");
        }

        Consumable consumable = consumablesBag.getItem(itemId);
        if(consumable == null) {
            throw new IllegalStateException("Item " + itemId + " not found in configured consumables.");
        }
        if(consumable.isFromInventory() == false) {
            throw new IllegalStateException("Item " + itemId + " not usable from inventory.");
        }

        // Restore health
        character = performHealthChange(character, consumable.getHealthRestored());
        // Restore attack resource
        character = performAtkResourceChange(character, consumable.getAtkResourceRestored());
        // Remove item used from inventory
        character = removeItemFromInventory(character, itemId, 1, "Using");

        return repository.save(character);
    }

    private PlayerCharacter performHealthChange(PlayerCharacter character, int healthChanged) {
        character.setCurrentHealth(character.getCurrentHealth() + healthChanged);
        if(character.getCurrentHealth() > character.getMaxHealth()) {
            character.setCurrentHealth(character.getMaxHealth());
        }
        return character;
    }

    private PlayerCharacter performAtkResourceChange(PlayerCharacter character, int atkResourceChanged) {
        character.setCurrentAtkResource(character.getCurrentAtkResource() + atkResourceChanged);
        if(character.getCurrentAtkResource() > character.getMaxAtkResource()) {
            character.setCurrentAtkResource(character.getMaxAtkResource());
        }
        return character;
    }

    private PlayerCharacter removeItemFromInventory(PlayerCharacter character, String itemId, int count, String actionType) {
        int currentItemCount = character.getInventory().get(itemId);
        int newItemCount = currentItemCount - 1;
        log.debug("{} {} of item {}; old count: {}, new count: {}", actionType,  1, itemId, currentItemCount,newItemCount);
        character.getInventory().put(itemId, newItemCount);
        log.debug("Inventory: {}", character.getInventory());
        if(character.getInventory().get(itemId) < 0 ) {
            throw new IllegalStateException("Item count for item " + itemId + " is below 0.");
        }

        return character;
    }
}
