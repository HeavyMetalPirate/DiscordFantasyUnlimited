package com.fantasyunlimited.util;

import com.fantasyunlimited.data.entity.PlayerCharacter;

@FunctionalInterface
public interface InventoryAction {

    public PlayerCharacter performAction();

}
