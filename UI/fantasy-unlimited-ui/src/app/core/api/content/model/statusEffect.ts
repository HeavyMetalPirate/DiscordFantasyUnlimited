/**
 * FantasyUnlimited API
 * Fantasy Unlimited - REST API Description
 *
 * The version of the OpenAPI document: v0.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface StatusEffect { 
    statusName?: string;
    statusIcon?: string;
    bonus?: number;
    statusType?: StatusEffect.StatusTypeEnum;
    buffModifiesAttribute?: StatusEffect.BuffModifiesAttributeEnum;
    buffModifiesCombatSkill?: StatusEffect.BuffModifiesCombatSkillEnum;
    buffModifier?: number;
    skillIncapacitates?: boolean;
    durationInTurns?: number;
    healthChangedOverTime?: number;
}
export namespace StatusEffect {
    export type StatusTypeEnum = 'BUFF' | 'DEBUFF';
    export const StatusTypeEnum = {
        Buff: 'BUFF' as StatusTypeEnum,
        Debuff: 'DEBUFF' as StatusTypeEnum
    };
    export type BuffModifiesAttributeEnum = 'Strength' | 'Endurance' | 'Dexterity' | 'Wisdom' | 'Intelligence' | 'Defense' | 'Luck' | 'All';
    export const BuffModifiesAttributeEnum = {
        Strength: 'Strength' as BuffModifiesAttributeEnum,
        Endurance: 'Endurance' as BuffModifiesAttributeEnum,
        Dexterity: 'Dexterity' as BuffModifiesAttributeEnum,
        Wisdom: 'Wisdom' as BuffModifiesAttributeEnum,
        Intelligence: 'Intelligence' as BuffModifiesAttributeEnum,
        Defense: 'Defense' as BuffModifiesAttributeEnum,
        Luck: 'Luck' as BuffModifiesAttributeEnum,
        All: 'All' as BuffModifiesAttributeEnum
    };
    export type BuffModifiesCombatSkillEnum = 'Dodge' | 'Critical' | 'Block' | 'Parry' | 'Spellpower' | 'Healpower';
    export const BuffModifiesCombatSkillEnum = {
        Dodge: 'Dodge' as BuffModifiesCombatSkillEnum,
        Critical: 'Critical' as BuffModifiesCombatSkillEnum,
        Block: 'Block' as BuffModifiesCombatSkillEnum,
        Parry: 'Parry' as BuffModifiesCombatSkillEnum,
        Spellpower: 'Spellpower' as BuffModifiesCombatSkillEnum,
        Healpower: 'Healpower' as BuffModifiesCombatSkillEnum
    };
}

