package com.fantasyunlimited.items.configuration;

import com.fantasyunlimited.items.bags.HostileNPCBag;
import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.entity.*;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ComponentScan(basePackages = "com.fantasyunlimited.items")
public class ItemConfiguration {

    @Bean
    public CommandLineRunner runAdditionalRaceBagConfiguration(@Autowired RaceBag raceBag, @Autowired LocationBag locationBag, @Autowired HostileNPCBag hostileNPCBag) {
        return (args -> {
            raceBag.getItems().forEach(item -> item.setStartingLocation(locationBag.getItem(item.getStartingLocationId())));

            locationBag.getItems().forEach(item -> {
                for (String hostile : item.getHostileNPCIds()) {
                    HostileNPC hostileNPC = hostileNPCBag.getItem(hostile);

                    if (item.getMinimumLevel() > hostileNPC.getLevel() || item.getMinimumLevel() == 0) {
                        item.setMinimumLevel(hostileNPC.getLevel());
                    }
                    if (item.getMaximumLevel() < hostileNPC.getLevel() || item.getMaximumLevel() == 0) {
                        item.setMaximumLevel(hostileNPC.getLevel());
                    }

                    if(item.getHostileNPCs() == null) item.setHostileNPCs(new ArrayList<>());

                    item.getHostileNPCs().add(hostileNPC);
                }
            });
        });
    }

    @Bean
    public XStream getXStream() {
        XStream xstream = new XStream();
        xstream.allowTypesByWildcard(new String[] {
                "com.fantasyunlimited.items.entity.**"
        });
        xstream.alias("Class", CharacterClass.class);
        xstream.alias("Race", Race.class);
        xstream.alias("ClassBonus", ClassBonus.class);
        xstream.alias("Skill", Skill.class);
        xstream.alias("SkillRank", SkillRank.class);
        xstream.alias("StatusEffect", StatusEffect.class);
        xstream.alias("RacialBonus", RacialBonus.class);
        xstream.alias("Weapon", Weapon.class);
        xstream.alias("Equipment", Equipment.class);
        xstream.alias("AttributeBonus", AttributeBonus.class);
        xstream.alias("CombatSkillBonus", CombatSkillBonus.class);
        xstream.alias("SecondarySkill", SecondarySkill.class);
        xstream.alias("SecondarySkillBonus", SecondarySkillBonus.class);
        xstream.alias("AttackResourceBonus", AttackResourceBonus.class);
        xstream.alias("Location", Location.class);
        xstream.alias("TravelConnection", TravelConnection.class);
        xstream.alias("NPC", NPC.class);
        xstream.alias("HostileNPC", HostileNPC.class);
        xstream.alias("Consumable", Consumable.class);
        xstream.alias("Integer", Integer.class);
        return xstream;
    }
}
