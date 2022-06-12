package com.fantasyunlimited.items.entity;

public enum EnergyType {
    RAGE("Rage"), FOCUS("Focus"), MANA("Mana");

    private String label;

    private EnergyType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}