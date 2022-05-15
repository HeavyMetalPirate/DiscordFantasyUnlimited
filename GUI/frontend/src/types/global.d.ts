import {TFunction} from "react-i18next";

export {};

declare global {

    interface TranslationAsProperty {
        translation: TFunction<"translation", undefined>;
    }

    interface LocationActionList {
        list: LocationAction[];
    }

    interface CharacterInformation {
        character: PlayerCharacterData;
    }

    interface User {
        name: string;
        role: string;
    }

    interface FilteredUserList {
        filteredUsers: User[];
    }

    interface UserList {
        details: User[];
    }

    type CsrfToken = {
        token: string;
        headerName: string;
        parameterName: string;
    }

    type UserPrincipal = {
        name: string;
    }

    type CharacterClassInfo = {
        id: string;
        name: string;
        icon: string;
    }

    type CharacterRaceInfo = {
        id: string;
        name: string;
        icon: string;
    }

    type CharacterLocationInfo = {
        id: string;
        name: string;
        icon: string;
    }

    type CharacterBattleResourceInfo = {
        currentHealth: number;
        maxHealth: number;
        currentResource: number;
        maxResource: number;
        energyType: string;
    }

    type PlayerCharacterData = {
        id: string;
        name: string;
        level: number;
        exp: number;
        characterClass: CharacterClassInfo;
        race: CharacterRaceInfo;
        location: CharacterLocationInfo;
        resources: CharacterBattleResourceInfo;
    }

    type LocationActionDetails = {
        minimumLevel?: number;
        maximumLevel?: number;
        duration?: number;
        toll?: number;
    }

    type LocationAction = {
        type: string;
        text: string;
        iconName: string;
        endpoint: string;
        requirementMet: boolean;
        reason: string;
        details: LocationActionDetails;
    }
}