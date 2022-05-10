package com.fantasyunlimited.data.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SecondarySkills implements Serializable {
    private int woodcutting;
    private int fishing;
    private int mining;
    private int alchemy;
    private int enchanting;

    public int getWoodcutting() {
        return woodcutting;
    }

    public void setWoodcutting(int woodcutting) {
        this.woodcutting = woodcutting;
    }

    public int getFishing() {
        return fishing;
    }

    public void setFishing(int fishing) {
        this.fishing = fishing;
    }

    public int getMining() {
        return mining;
    }

    public void setMining(int mining) {
        this.mining = mining;
    }

    public int getAlchemy() {
        return alchemy;
    }

    public void setAlchemy(int alchemy) {
        this.alchemy = alchemy;
    }

    public int getEnchanting() {
        return enchanting;
    }

    public void setEnchanting(int enchanting) {
        this.enchanting = enchanting;
    }
}
