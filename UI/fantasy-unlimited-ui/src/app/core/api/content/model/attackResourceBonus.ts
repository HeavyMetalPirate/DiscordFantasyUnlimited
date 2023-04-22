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


export interface AttackResourceBonus { 
    bonus?: number;
    skill?: AttackResourceBonus.SkillEnum;
}
export namespace AttackResourceBonus {
    export type SkillEnum = 'Rage' | 'Focus' | 'Mana';
    export const SkillEnum = {
        Rage: 'Rage' as SkillEnum,
        Focus: 'Focus' as SkillEnum,
        Mana: 'Mana' as SkillEnum
    };
}


