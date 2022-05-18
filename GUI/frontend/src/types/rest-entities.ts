/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.36.1070 on 2022-05-18 20:53:23.

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

    export interface Serializable {
    }

    export type LocationActionType = "TRAVEL" | "COMBAT" | "SECONDARY_SKILL" | "TRADING" | "GLOBAL_TRADING" | "OTHER";

    export type UserRegistrationStatus = "REGISTERED" | "USERNAME_FOUND" | "EMAIL_FOUND" | "INTERNAL_ERROR";

    export type EnergyType = "RAGE" | "FOCUS" | "MANA";

}
