package com.fantasyunlimited.data.service;

import com.fantasyunlimited.data.dao.PlayerCharacterRepository;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.bags.ConsumablesBag;
import com.fantasyunlimited.items.entity.Consumable;
import com.fantasyunlimited.items.entity.Dropable;
import com.fantasyunlimited.items.util.DropableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerCharacterService {
    private Logger log = LoggerFactory.getLogger(PlayerCharacterService.class);
    @Autowired
    private PlayerCharacterRepository repository;
    @Autowired
    private ConsumablesBag consumablesBag;

    @Transactional
    public PlayerCharacter dropItems(PlayerCharacter selectedCharacter, String itemId, int count) {
        // read into transaction
        PlayerCharacter character = repository.findById(selectedCharacter.getId()).get();

        int currentItemCount = character.getInventory().get(itemId);
        int newItemCount = currentItemCount - count;
        log.debug("Dropping {} of item {}; old count: {}, new count: {}", count, itemId, currentItemCount,newItemCount);
        character.getInventory().put(itemId, newItemCount);
        log.debug("Inventory: {}", character.getInventory());
        if(character.getInventory().get(itemId) < 0 ) {
            throw new IllegalStateException("Item count for item " + itemId + " is below 0.");
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
        character.setCurrentHealth(character.getCurrentHealth() + consumable.getHealthRestored());
        if(character.getCurrentHealth() > character.getMaxHealth()) {
            character.setCurrentHealth(character.getMaxHealth());
        }

        // Restore attack resource
        character.setCurrentAtkResource(character.getCurrentAtkResource() + consumable.getAtkResourceRestored());
        if(character.getCurrentAtkResource() > character.getMaxAtkResource()) {
            character.setCurrentAtkResource(character.getMaxAtkResource());
        }

        return repository.save(character);
    }
}
