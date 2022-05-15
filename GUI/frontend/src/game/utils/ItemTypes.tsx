interface SimpleDialogProps extends TranslationAsProperty {
    selectedValue: InventoryItem | null;
    open: boolean;
    onClose(value: string): void;
}

interface InventoryListProperties extends TranslationAsProperty {
    inventory: InventoryItem[] | null;
    onReload(): void;
}

interface ContextMenuItemSelection {
    item: InventoryItem | null;
    name: string | null;
}

interface ItemDetailView extends TranslationAsProperty {
    item: InventoryItem | null;
    visible: string;
    x: number;
    y: number;
}

type InventoryType = {
    items: InventoryItem[];
    gold: number;
}

type InventoryItem = {
    type: string;
    count: number;
    item: DropableItem;
}

type DropableItem = {
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

