interface GenericItem {
    id: string;
    name: string;
    description: string;
    iconName: string;
}

enum EnergyType {
    RAGE,
    FOCUS,
    MANA
}

type Attributes = {
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

interface Dropable extends GenericItem {
    value: number;
}

enum ItemRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    ARTIFACT
}

interface RarityClassifiedItem extends Dropable {
    rarity: ItemRarity;
}

type CombatSkillBonus = {
    skill: string;
    bonus: number;
}

type AttributeBonus = {
    attribute: string;
    bonus: number;
}

type SecondarySkillBonus = {
    skill: string;
    bonus: number;
}

type AttackResourceBonus = {
    energyType: string;
    bonus: number;
}

interface Gear extends RarityClassifiedItem {
    skillBonuses: CombatSkillBonus[];
    attributeBonuses: AttributeBonus[];
    secondarySkillBonuses: SecondarySkillBonus[];
    atkResourceBonuses: AttackResourceBonus[];

    classExclusive: boolean;
    raceExclusive: boolean;
}

enum WeaponType {
    NONE,
    SWORD,
    AXE,
    DAGGER,
    POLEARM,
    GREATSWORD,
    GREATAXE,
    BOW,
    CROSSBOW,
    STAFF,
    WAND,
    SHIELD
}

enum Hand {
    LEFT,
    RIGHT,
    BOTH,
    TWOHANDED
}

interface Weapon extends Gear {
    type: WeaponType;
    hand: Hand;
    minDamage: number;
    maxDamage: number;
}

enum EquipmentType {
    HELMET,
    CHEST,
    GLOVES,
    PANTS,
    BOOTS,
    RING,
    NECK
}

interface Equipment extends Gear {
    type: EquipmentType;
    armor: number;
}

enum Attribute {
    STRENGTH,
    ENDURANCE,
    DEXTERITY,
    WISDOM,
    INTELLIGENCE,
    DEFENSE,
    LUCK,
    ALL
}
enum CombatSkill {
    DODGE,
    CRITICAL,
    BLOCK,
    PARRY,
    SPELLPOWER,
    HEALPOWER
}

interface ClassBonus extends GenericItem {
    attribute: Attribute;
    weaponType: WeaponType;
    combatSkill: CombatSkill;
    modifier: number;
}

enum SkillType {
    OFFENSIVE,
    DEFENSIVE,
    BUFF,
    DEBUFF
}

enum TargetType {
    ENEMY,
    FRIEND,
    OWN,
    AREA
}

enum SkillWeaponModifier {
    WEAPON_MAINHAN,
    WEAPON_OFFHAND,
    NONE
}

interface SkillRank {
    rank: number;
    damageModifier: number;
    costModifier: number;
    requiredAttributeValue: number;
    requiredPlayerLevel: number;
}

interface SkillRequirement {
    skillIdOnSelf: string;
    skillIdOnTarget: string;
}

interface Skill extends GenericItem {
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

interface CharacterClass extends GenericItem {
    lore: string;
    humanPlayable: boolean;
    energyType: EnergyType;
    attributes: Attributes;

    bonuses: ClassBonus[];
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