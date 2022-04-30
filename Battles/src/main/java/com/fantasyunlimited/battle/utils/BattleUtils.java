package com.fantasyunlimited.battle.utils;

import com.fantasyunlimited.items.bags.HostileNPCBag;
import com.fantasyunlimited.items.entity.HostileNPC;
import com.fantasyunlimited.items.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class BattleUtils {

    @Autowired
    private HostileNPCBag hostileNPCBag;

    private Random random = new Random();

    public List<HostileNPC> findOpponents(Location location) {
        if (location.getHostileNPCIds().isEmpty()) {
            return new ArrayList<>();
        }

        float chance = random.nextFloat();

        if (chance < 0.6f) {
            // only one
            return createOpponentList(1, location);
        } else if (chance < 0.75f) {
            // two
            return createOpponentList(2, location);
        } else if (chance < 0.85f) {
            // three
            return createOpponentList(3, location);
        } else if (chance < 0.92f) {
            // four
            return createOpponentList(4, location);
        } else {
            // five, holy crêpe
            return createOpponentList(5, location);
        }
    }

    private List<HostileNPC> createOpponentList(int size, Location location) {
        List<String> available = new ArrayList<>(location.getHostileNPCIds());
        List<HostileNPC> picks = new ArrayList<>();
        while (picks.size() < size) {
            if (available.isEmpty()) {
                break;
            }
            int pick = random.nextInt(available.size());
            HostileNPC npc = hostileNPCBag.getItem(available.get(pick));
            picks.add(npc);

            if (npc.isUnique()) {
                available.remove(npc.getId());
            }
        }
        return picks;
    }
}
