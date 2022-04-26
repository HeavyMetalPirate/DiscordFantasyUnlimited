package com.fantasyunlimited.items.util;

import com.fantasyunlimited.items.entity.Dropable;
import com.fantasyunlimited.items.bags.ConsumablesBag;
import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DropableUtils {
    @Autowired
    private EquipmentBag equipmentBag;
    @Autowired
    private ConsumablesBag consumablesBag;
    @Autowired
    private WeaponBag weaponBag;

    public Dropable getDropableItem(String id) {
        Dropable dropable = null;

        dropable = equipmentBag.getItem(id);
        if (dropable != null) {
            return dropable;
        }
        dropable = weaponBag.getItem(id);
        if (dropable != null) {
            return dropable;
        }
        dropable = consumablesBag.getItem(id);
        if (dropable != null) {
            return dropable;
        }
        // TODO others

        return dropable;
    }
}
