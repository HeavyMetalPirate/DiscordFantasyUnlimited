/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.36.1070 on 2022-05-22 15:41:05.

export namespace REST {

    export interface ActiveUserItem {
        name: string;
        role: string;
    }

    export interface BattleActionDetails {
        minimumLevel: number;
        maximumLevel: number;
    }

    export interface BattleBasicInfo {
        id: string;
    }

    export interface BattleResourceItem {
        currentHealth: number;
        maxHealth: number;
        currentResource: number;
        maxResource: number;
        energyType: EnergyType;
    }

    export interface CharacterCreationBody {
        name: string;
        classId: string;
        raceId: string;
    }

    export interface CharacterListItem {
        id: number;
        name: string;
        characterClass: ClassItem;
        race: RaceItem;
        location: LocationItem;
        level: number;
    }

    export interface ClassItem {
        id: string;
        name: string;
        icon: string;
    }

    export interface DropItemDetails {
        itemId: string;
        count: number;
    }

    export interface EquipRequest {
        itemId: string;
        slot: EquipmentSlot;
    }

    export interface InventoryItem {
        item: Dropable;
        type: string;
        count: number;
    }

    export interface InventoryManagementDetails {
        gold: number;
        items: InventoryItem[];
    }

    export interface LocationAction {
        type: LocationActionType;
        text: string;
        iconName: string;
        endpoint: string;
        details: any;
        requirementMet: boolean;
        reason: string;
    }

    export interface LocationItem {
        id: string;
        name: string;
        icon: string;
    }

    export interface PlayerCharacterItem {
        name: string;
        characterClass: ClassItem;
        race: RaceItem;
        location: LocationItem;
        level: number;
        exp: number;
        resources: BattleResourceItem;
    }

    export interface PlayerCombatSkills {
        dodge: number;
        block: number;
        parry: number;
        critical: number;
        spellpower: number;
        healpower: number;
    }

    export interface PlayerEquipment {
        mainhand: Weapon;
        offhand: Weapon;
        helmet: Equipment;
        chest: Equipment;
        gloves: Equipment;
        pants: Equipment;
        boots: Equipment;
        ring1: Equipment;
        ring2: Equipment;
        neck: Equipment;
    }

    export interface PlayerEquipmentDetails {
        stats: PlayerStats;
        secondaryStats: PlayerSecondaryStats;
        combatSkills: PlayerCombatSkills;
        equipment: PlayerEquipment;
    }

    export interface PlayerSecondaryStats {
        playerSkills: SecondarySkills;
        equipmentSkills: SecondarySkills;
    }

    export interface PlayerStats {
        characterAttributes: Attributes;
        equipmentAttributes: Attributes;
    }

    export interface RaceItem {
        id: string;
        name: string;
        icon: string;
    }

    export interface SecondarySkillDetails {
        minimumLevel: number;
    }

    export interface TravelDetails {
        duration: number;
        toll: number;
    }

    export interface UnequipRequest {
        slot: EquipmentSlot;
    }

    export interface UseItemDetails {
        itemId: string;
    }

    export interface UserRegistrationBody {
        username: string;
        password: string;
        email: string;
    }

    export interface UserRegistrationResponse {
        message: string;
        status: UserRegistrationStatus;
    }

    export interface UserSessionInformation {
        principal: Principal;
        csrf: CsrfToken;
        character: string;
    }

    export interface Dropable extends GenericItem {
        value: number;
    }

    export interface Weapon extends Gear {
        type: WeaponType;
        hand: Hand;
        minDamage: number;
        maxDamage: number;
    }

    export interface Equipment extends Gear {
        type: EquipmentType;
        armor: number;
    }

    export interface SecondarySkills extends Serializable {
        woodcutting: number;
        fishing: number;
        mining: number;
        alchemy: number;
        enchanting: number;
    }

    export interface Attributes extends Serializable {
        endurance: number;
        strength: number;
        dexterity: number;
        wisdom: number;
        intelligence: number;
        defense: number;
        luck: number;
        unspent: number;
    }

    export interface Principal {
        name: string;
    }

    export interface CsrfToken extends Serializable {
        token: string;
        parameterName: string;
        headerName: string;
    }

    export interface GenericItem extends Serializable {
        id: string;
        name: string;
        description: string;
        iconName: string;
    }

    export interface CombatSkillBonus {
        skill: CombatSkill;
        bonus: number;
    }

    export interface AttributeBonus {
        attribute: Attribute;
        bonus: number;
    }

    export interface SecondarySkillBonus {
        skill: SecondarySkill;
        bonus: number;
    }

    export interface AttackResourceBonus {
        bonus: number;
        skill: EnergyType;
    }

    export interface Gear extends RarityClassifiedItem {
        skillBonuses: CombatSkillBonus[];
        attributeBonuses: AttributeBonus[];
        secondarySkillBonuses: SecondarySkillBonus[];
        atkResourceBonuses: AttackResourceBonus[];
        classExclusive: string;
        raceExclusive: string;
    }

    export interface Serializable {
    }

    export interface RarityClassifiedItem extends Dropable {
        rarity: ItemRarity;
    }

    export type LocationActionType = "TRAVEL" | "COMBAT" | "SECONDARY_SKILL" | "TRADING" | "GLOBAL_TRADING" | "OTHER";

    export type UserRegistrationStatus = "REGISTERED" | "USERNAME_FOUND" | "EMAIL_FOUND" | "INTERNAL_ERROR";

    export type EnergyType = "RAGE" | "FOCUS" | "MANA";

    export type EquipmentSlot = "HELMET" | "CHEST" | "GLOVES" | "PANTS" | "BOOTS" | "RING1" | "RING2" | "NECK" | "MAINHAND" | "OFFHAND";

    export type ItemRarity = "COMMON" | "UNCOMMON" | "RARE" | "EPIC" | "LEGENDARY" | "ARTIFACT";

    export type WeaponType = "NONE" | "SWORD" | "AXE" | "DAGGER" | "POLEARM" | "GREATSWORD" | "GREATAXE" | "BOW" | "CROSSBOW" | "STAFF" | "WAND" | "SHIELD";

    export type Hand = "LEFT" | "RIGHT" | "BOTH" | "TWOHANDED";

    export type EquipmentType = "HELMET" | "CHEST" | "GLOVES" | "PANTS" | "BOOTS" | "RING" | "NECK";

    export type CombatSkill = "DODGE" | "CRITICAL" | "BLOCK" | "PARRY" | "SPELLPOWER" | "HEALPOWER";

    export type Attribute = "STRENGTH" | "ENDURANCE" | "DEXTERITY" | "WISDOM" | "INTELLIGENCE" | "DEFENSE" | "LUCK" | "ALL";

    export type SecondarySkill = "WOODCUTTING" | "FISHING" | "MINING" | "ALCHEMY" | "ENCHANTING";

}
