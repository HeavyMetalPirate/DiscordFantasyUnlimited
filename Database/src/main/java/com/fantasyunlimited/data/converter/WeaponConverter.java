package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.Weapon;
import org.springframework.beans.factory.annotation.Autowired;

public class WeaponConverter extends GenericItemConverter<Weapon> {
    @Autowired
    private WeaponBag weaponBag;

    @Override
    public Weapon convertToEntityAttribute(String dbData) {
        return weaponBag.getItem(dbData);
    }
}
