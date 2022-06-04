/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.36.1070 on 2022-06-03 17:35:37.

export namespace Items {

    export interface AttackResourceBonus extends Serializable {
        bonus: number;
        skill: EnergyType;
    }

    export interface AttributeBonus extends Serializable {
        attribute: Attribute;
        bonus: number;
    }

    export interface Attributes extends Serializable {
        endurance: number;
        strength: number;
        dexterity: number;
        wisdom: number;
        intelligence: number;
        defense: number;
        luck: number;
        enduranceGrowth: number;
        strengthGrowth: number;
        dexterityGrowth: number;
        wisdomGrowth: number;
        intelligenceGrowth: number;
        defenseGrowth: number;
        luckGrowth: number;
    }

    export interface CharacterClass extends GenericItem {
        lore: string;
        humanPlayable: boolean;
        energyType: EnergyType;
        attributes: Attributes;
        startingMainhand: string;
        startingOffhand: string;
        startingHelmet: string;
        startingChest: string;
        startingGloves: string;
        startingPants: string;
        startingBoots: string;
        startingRing1: string;
        startingRing2: string;
        startingNeck: string;
        bonuses: ClassBonus[];
        skills: string[];
        skillInstances: Skill[];
        startingMainhandInstance: Weapon;
        startingOffhandInstance: Weapon;
        startingHelmetInstance: Equipment;
        startingChestInstance: Equipment;
        startingGlovesInstance: Equipment;
        startingPantsInstance: Equipment;
        startingBootsInstance: Equipment;
        startingRing1Instance: Equipment;
        startingRing2Instance: Equipment;
        startingNeckInstance: Equipment;
    }

    export interface ClassBonus extends GenericItem {
        attribute: Attribute;
        weaponType: WeaponType;
        combatSkill: CombatSkill;
        modifier: number;
    }

    export interface CombatSkillBonus extends Serializable {
        skill: CombatSkill;
        bonus: number;
    }

    export interface Consumable extends RarityClassifiedItem {
        duringBattle: boolean;
        fromInventory: boolean;
        combatSkillModifiers: CombatSkillBonus[];
        attributeModifiers: AttributeBonus[];
        durationRounds: number;
        healthRestored: number;
        atkResourceRestored: number;
        resourceType: EnergyType;
    }

    export interface Dropable extends GenericItem {
        value: number;
    }

    export interface Equipment extends Gear {
        type: EquipmentType;
        armor: number;
    }

    export interface Gear extends RarityClassifiedItem {
        skillBonuses: CombatSkillBonus[];
        attributeBonuses: AttributeBonus[];
        secondarySkillBonuses: SecondarySkillBonus[];
        atkResourceBonuses: AttackResourceBonus[];
        classExclusive: string;
        raceExclusive: string;
    }

    export interface GenericItem extends Serializable {
        id: string;
        name: string;
        description: string;
        iconName: string;
    }

    export interface HostileNPC extends GenericItem {
        raceId: string;
        classId: string;
        unique: boolean;
        level: number;
        mainhand: string;
        offhand: string;
        helmet: string;
        chest: string;
        gloves: string;
        pants: string;
        boots: string;
        ring1: string;
        ring2: string;
        neck: string;
        loottable: { [index: string]: number };
        minimumGold: number;
        maximumGold: number;
        characterClass: CharacterClass;
        race: Race;
        mainhandInstance: Weapon;
        offhandInstance: Weapon;
        helmetInstance: Equipment;
        gloveInstance: Equipment;
        chestInstance: Equipment;
        pantsInstance: Equipment;
        bootsInstance: Equipment;
        ring1Instance: Equipment;
        ring2Instance: Equipment;
        neckInstance: Equipment;
    }

    export interface Location extends GenericItem {
        marketAccess: boolean;
        globalMarketAccess: boolean;
        bannerImage: string;
        allowedSecondarySkills: { [P in SecondarySkill]?: number };
        connections: TravelConnection[];
        npcIds: string[];
        hostileNPCIds: string[];
        minimumLevel: number;
        maximumLevel: number;
        npcs: NPC[];
        hostileNPCs: HostileNPC[];
    }

    export interface NPC extends GenericItem {
        raceId: string;
        classId: string;
        level: number;
        title: string;
        vending: boolean;
        selling: { [index: string]: number };
        genericDialogue: string[];
        questIds: string[];
        sellingItems: { [index: string]: number };
        quests: Quest[];
    }

    export interface Quest extends GenericItem {
    }

    export interface Race extends GenericItem {
        lore: string;
        startingLocationId: string;
        humanPlayable: boolean;
        bonuses: RacialBonus[];
        startingLocation: Location;
    }

    export interface RacialBonus extends GenericItem {
        attribute: Attribute;
        weaponType: WeaponType;
        combatSkill: CombatSkill;
        secondarySkill: SecondarySkill;
        modifier: number;
    }

    export interface RarityClassifiedItem extends Dropable {
        rarity: ItemRarity;
    }

    export interface SecondarySkillBonus extends Serializable {
        skill: SecondarySkill;
        bonus: number;
    }

    export interface Skill extends GenericItem {
        iconId: string;
        attribute: Attribute;
        type: SkillType;
        targetType: TargetType;
        weaponModifier: SkillWeaponModifier;
        minDamage: number;
        maxDamage: number;
        costOfExecution: number;
        preparationRounds: number;
        ranks: SkillRank[];
        requirements: SkillRequirement[];
        buffModifiesAttribute: Attribute;
        buffModifiesCombatSkill: CombatSkill;
        buffModifier: number;
        skillIncapacitates: boolean;
        durationInTurns: number;
    }

    export interface SkillRank extends Serializable {
        rank: number;
        damageModifier: number;
        costModifier: number;
        requiredAttributeValue: number;
        requiredPlayerLevel: number;
    }

    export interface SkillRequirement extends Serializable {
        skillIdOnSelf: string;
        skillIdOnTarget: string;
    }

    export interface TravelConnection extends Serializable {
        targetLocationId: string;
        duration: number;
        toll: number;
    }

    export interface Weapon extends Gear {
        type: WeaponType;
        hand: Hand;
        minDamage: number;
        maxDamage: number;
    }

    export interface Serializable {
    }

    export type CombatSkill = "DODGE" | "CRITICAL" | "BLOCK" | "PARRY" | "SPELLPOWER" | "HEALPOWER";

    export type EquipmentType = "HELMET" | "CHEST" | "GLOVES" | "PANTS" | "BOOTS" | "RING" | "NECK";

    export type ItemRarity = "COMMON" | "UNCOMMON" | "RARE" | "EPIC" | "LEGENDARY" | "ARTIFACT";

    export type SecondarySkill = "WOODCUTTING" | "FISHING" | "MINING" | "ALCHEMY" | "ENCHANTING";

    export type EnergyType = "RAGE" | "FOCUS" | "MANA";

    export type Attribute = "STRENGTH" | "ENDURANCE" | "DEXTERITY" | "WISDOM" | "INTELLIGENCE" | "DEFENSE" | "LUCK" | "ALL";

    export type WeaponType = "NONE" | "SWORD" | "AXE" | "DAGGER" | "POLEARM" | "GREATSWORD" | "GREATAXE" | "BOW" | "CROSSBOW" | "STAFF" | "WAND" | "SHIELD";

    export type SkillType = "OFFENSIVE" | "DEFENSIVE" | "BUFF" | "DEBUFF";

    export type TargetType = "ENEMY" | "FRIEND" | "OWN" | "AREA";

    export type SkillWeaponModifier = "WEAPON_MAINHAND" | "WEAPON_OFFHAND" | "NONE";

    export type Hand = "LEFT" | "RIGHT" | "BOTH" | "TWOHANDED";

}
