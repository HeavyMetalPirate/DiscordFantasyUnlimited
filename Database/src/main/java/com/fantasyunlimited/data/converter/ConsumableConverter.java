package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.ConsumablesBag;
import com.fantasyunlimited.items.entity.Consumable;
import org.springframework.beans.factory.annotation.Autowired;

public class ConsumableConverter extends GenericItemConverter<Consumable> {
    @Autowired
    private ConsumablesBag consumablesBag;

    @Override
    public Consumable convertToEntityAttribute(String dbData) {
        return consumablesBag.getItem(dbData);
    }
}
