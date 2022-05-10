package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.ClassBag;
import com.fantasyunlimited.items.entity.CharacterClass;
import org.springframework.beans.factory.annotation.Autowired;

public class CharacterClassConverter extends GenericItemConverter<CharacterClass> {
    @Autowired
    private ClassBag classBag;

    @Override
    public CharacterClass convertToEntityAttribute(String dbData) {
        return classBag.getItem(dbData);
    }
}
