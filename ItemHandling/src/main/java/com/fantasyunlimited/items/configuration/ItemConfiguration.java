package com.fantasyunlimited.items.configuration;

import com.fantasyunlimited.items.entity.*;
import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.fantasyunlimited.items")
public class ItemConfiguration {

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
        return xstream;
    }
}
