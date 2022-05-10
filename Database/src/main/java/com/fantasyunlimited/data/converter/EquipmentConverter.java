package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.entity.Equipment;
import org.springframework.beans.factory.annotation.Autowired;

public class EquipmentConverter extends GenericItemConverter<Equipment> {
    @Autowired
    private EquipmentBag equipmentBag;

    @Override
    public Equipment convertToEntityAttribute(String dbData) {
        return equipmentBag.getItem(dbData);
    }
}
