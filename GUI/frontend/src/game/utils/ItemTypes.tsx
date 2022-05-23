import * as EntityTypes from "../../types/itemhandling";

import CombatSkillBonus = EntityTypes.Items.CombatSkillBonus;
import AttributeBonus = EntityTypes.Items.AttributeBonus;
import SecondarySkillBonus = EntityTypes.Items.SecondarySkillBonus;
import AttackResourceBonus = EntityTypes.Items.AttackResourceBonus;

export interface SimpleDialogProps extends TranslationAsProperty {
    selectedValue: InventoryItem | null;
    open: boolean;
    onClose(value: string): void;
}

export interface InventoryListProperties extends TranslationAsProperty {
    inventory: InventoryItem[] | null;
    onReload(): void;
}

export interface ContextMenuItemSelection {
    item: InventoryItem | null;
    name: string | null;
}

export interface ItemDetailViewProperties extends TranslationAsProperty {
    item: InventoryItem | null;
    visible: string;
    x: number;
    y: number;
}

export type InventoryType = {
    items: InventoryItem[];
    gold: number;
}

export type InventoryItem = {
    type: 'weapon' | 'equipment' | 'consumable';
    count: number;
    item: DropableItem;
}

export type DropableItem = {
    // Generic Item
    id: string;
    name: string;
    description: string;
    iconName: string;

    // RarityClassifiedItem
    rarity: string;

    // Dropable
    value: number;

    // Gear
    skillBonuses?: CombatSkillBonus[];
    attributeBonuses?: AttributeBonus[];
    secondarySkillBonuses?: SecondarySkillBonus[];
    atkResourceBonuses?: AttackResourceBonus[];
    classExclusive?: string;
    raceExclusive?: string;

    // Weapon
    type?: string;
    hand?: string;
    minDamage?: number;
    maxDamage?: number;

    // Equipment
    armor?: number;

    // Consumable
    duringBattle?: boolean;
    fromInventory?: boolean;
    combatSkillModifiers?: CombatSkillBonus[];
    attributeModifiers?: AttributeBonus[];
    durationRounds?: number;
    healthRestored?: number;
    atkResourceRestored?: number;
    resourceType?: string;
}

