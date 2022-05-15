package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationConverter extends GenericItemConverter<Location> {
    @Autowired
    private LocationBag locationBag;

    @Override
    public Location convertToEntityAttribute(String dbData) {
        return locationBag.getItem(dbData);
    }
}
