package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.SkillBag;
import com.fantasyunlimited.items.entity.Skill;
import org.springframework.beans.factory.annotation.Autowired;

public class SkillConverter extends GenericItemConverter<Skill> {
    @Autowired
    private SkillBag skillBag;

    @Override
    public Skill convertToEntityAttribute(String dbData) {
        return skillBag.getItem(dbData);
    }
}
