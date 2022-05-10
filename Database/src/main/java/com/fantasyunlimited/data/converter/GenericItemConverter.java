package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.items.entity.GenericItem;

import javax.persistence.AttributeConverter;

public abstract class GenericItemConverter<T extends GenericItem> implements AttributeConverter<T, String> {

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if(attribute == null) return null;

        return attribute.getId();
    }

    @Override
    public abstract T convertToEntityAttribute(String dbData);
}
