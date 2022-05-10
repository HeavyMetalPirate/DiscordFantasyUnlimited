package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;

public class RaceConverter extends GenericItemConverter<Race> {
    @Autowired
    private RaceBag raceBag;

    @Override
    public Race convertToEntityAttribute(String dbData) {
        return raceBag.getItem(dbData);
    }
}
