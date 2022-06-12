package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.HostileNPCBag;
import com.fantasyunlimited.items.entity.HostileNPC;
import com.fantasyunlimited.items.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;

public class HostileNPCConverter extends GenericItemConverter<HostileNPC> {
    @Autowired
    private HostileNPCBag hostileNPCBag;

    @Override
    public HostileNPC convertToEntityAttribute(String dbData) {
        return hostileNPCBag.getItem(dbData);
    }
}
