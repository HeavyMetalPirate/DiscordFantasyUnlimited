package com.fantasyunlimited.battle.utils;

import com.fantasyunlimited.battle.entity.BattleEquipment;
import com.fantasyunlimited.battle.entity.BattleNPC;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.bags.HostileNPCBag;
import com.fantasyunlimited.items.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BattleUtils {

    @Autowired
    private HostileNPCBag hostileNPCBag;

    private Random random = new Random();

    public BattlePlayer initializeBattlePlayer(PlayerCharacter base) {
        BattlePlayer battlePlayer = new BattlePlayer();

        battlePlayer.setCharacterId(base.getId());

        battlePlayer.setRaceId(base.getRaceId());
        battlePlayer.setCharClassId(base.getClassId());

        battlePlayer.setName(base.getName());
        battlePlayer.setLevel(base.getCurrentLevel());
        battlePlayer.setAttributes(base.getAttributes());

        BattleEquipment equipment = new BattleEquipment(base.getEquipment());
        battlePlayer.setEquipment(equipment);

        battlePlayer.setMaxHealth(base.getMaxHealth());
        battlePlayer.setMaxAtkResource(base.getMaxAtkResource());
        battlePlayer.setCurrentHealth(base.getCurrentHealth());

        if(battlePlayer.getCharClassId().getEnergyType() == CharacterClass.EnergyType.RAGE) {
            battlePlayer.setCurrentAtkResource(0);
        }
        else {
            battlePlayer.setCurrentAtkResource(base.getCurrentAtkResource());
        }

        battlePlayer.calculateRegeneration();

        return battlePlayer;
    }

    public BattleNPC initializeHostileNPC(HostileNPC base) {
        BattleNPC battleNPC = new BattleNPC();

        battleNPC.setBase(base);
        battleNPC.setBaseId(base.getId());
        battleNPC.setRaceId(base.getRace());

        CharacterClass charClass = base.getCharacterClass();
        battleNPC.setCharClassId(charClass);

        int level = base.getLevel();
        battleNPC.setLevel(level);


        com.fantasyunlimited.data.entity.Attributes attributes = new com.fantasyunlimited.data.entity.Attributes();

        int defense = charClass.getAttributes().getDefense() + (charClass.getAttributes().getDefenseGrowth() * level);
        int dexterity = charClass.getAttributes().getDexterity()
                + (charClass.getAttributes().getDexterityGrowth() * level);
        int endurance = charClass.getAttributes().getEndurance()
                + (charClass.getAttributes().getEnduranceGrowth() * level);
        int luck = charClass.getAttributes().getLuck() + (charClass.getAttributes().getLuckGrowth() * level);
        int strength = charClass.getAttributes().getStrength()
                + (charClass.getAttributes().getStrengthGrowth() * level);
        int intelligence = charClass.getAttributes().getIntelligence()
                + (charClass.getAttributes().getIntelligenceGrowth() * level);
        int wisdom = charClass.getAttributes().getWisdom() + (charClass.getAttributes().getWisdomGrowth() * level);

        attributes.setDefense(defense);
        attributes.setDexterity(dexterity);
        attributes.setIntelligence(intelligence);
        attributes.setLuck(luck);
        attributes.setStrength(strength);
        attributes.setWisdom(wisdom);
        attributes.setEndurance(endurance);
        battleNPC.setAttributes(attributes);

        BattleEquipment equipment = new BattleEquipment();
        equipment.setMainhand(base.getMainhandInstance());
        equipment.setOffhand(base.getOffhandInstance());
        equipment.setHelmet(base.getHelmetInstance());
        equipment.setChest(base.getChestInstance());
        equipment.setGloves(base.getGloveInstance());
        equipment.setPants(base.getPantsInstance());
        equipment.setBoots(base.getBootsInstance());
        equipment.setRing1(base.getRing1Instance());
        equipment.setRing2(base.getRing2Instance());
        equipment.setNeck(base.getNeckInstance());

        battleNPC.setEquipment(equipment);

        int enduraceBase = endurance + battleNPC.getAttributeBonus(Attributes.Attribute.ENDURANCE);
        int maxHealth = enduraceBase * 10 + level * 15;
        battleNPC.setMaxHealth(maxHealth);

        final AtomicInteger resourceBonus = new AtomicInteger(0);
        for (Gear eq : battleNPC.getCurrentGear()) {
            if (eq.getAtkResourceBonuses() == null) {
                continue;
            }
            eq.getAtkResourceBonuses().stream().filter(bonus -> bonus.getSkill() == charClass.getEnergyType())
                    .forEach(bonus -> resourceBonus.getAndAdd(bonus.getBonus()));
        }

        int maxAtkResource;
        if (charClass.getEnergyType() == CharacterClass.EnergyType.MANA) {
            int intelligenceBase = intelligence + battleNPC.getAttributeBonus(Attributes.Attribute.INTELLIGENCE);
            maxAtkResource = intelligenceBase * 15 + level * 20;
        } else {
            maxAtkResource = 100;
        }

        maxAtkResource += resourceBonus.get();
        battleNPC.setMaxAtkResource(maxAtkResource);

        battleNPC.setCurrentHealth(maxHealth);
        if (charClass.getEnergyType() == CharacterClass.EnergyType.RAGE) {
            battleNPC.setCurrentAtkResource(0);
        } else {
            battleNPC.setCurrentAtkResource(maxAtkResource);
        }

        battleNPC.calculateRegeneration();

        return battleNPC;
    }

    public List<HostileNPC> findOpponents(Location location) {
        if (location.getHostileNPCIds().isEmpty()) {
            return new ArrayList<>();
        }

        float chance = random.nextFloat();

        if (chance < 0.6f) {
            // only one
            return createOpponentList(1, location);
        } else if (chance < 0.75f) {
            // two
            return createOpponentList(2, location);
        } else if (chance < 0.85f) {
            // three
            return createOpponentList(3, location);
        } else if (chance < 0.92f) {
            // four
            return createOpponentList(4, location);
        } else {
            // five, holy crêpe
            return createOpponentList(5, location);
        }
    }

    private List<HostileNPC> createOpponentList(int size, Location location) {
        List<String> available = new ArrayList<>(location.getHostileNPCIds());
        List<HostileNPC> picks = new ArrayList<>();
        while (picks.size() < size) {
            if (available.isEmpty()) {
                break;
            }
            int pick = random.nextInt(available.size());
            HostileNPC npc = hostileNPCBag.getItem(available.get(pick));
            picks.add(npc);

            if (npc.isUnique()) {
                available.remove(npc.getId());
            }
        }
        return picks;
    }
}
