package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillBag extends GenericsBag<Skill> {

    public SkillBag() {
        super("skills");
    }

    @Override
    public boolean passSanityChecks(Skill item) throws SanityException {
        // Has name, description and id
        if (item.valuesFilled() == false) {
            throw new SanityException("At least one basic data (id, name, description) is missing!");
        }

        return true;
    }

    @Override
    public void initializeItemFields(Skill item) {
        // NO OP
    }
}
