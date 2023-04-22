package com.fantasyunlimited.data.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SecondarySkills implements Serializable {
    private int woodcutting;
    private int fishing;
    private int mining;
    private int alchemy;
    private int enchanting;

    public void inreaseWoodcutting(int amount) {
        woodcutting += amount;
    }
    public void increaseFishing(int amount) {
        fishing += amount;
    }
    public void increaseMining(int amount) {
        mining += amount;
    }
    public void increaseAlchemy(int amount) {
        alchemy += amount;
    }
    public void increaseEnchanting(int amount) {
        enchanting += amount;
    }

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
