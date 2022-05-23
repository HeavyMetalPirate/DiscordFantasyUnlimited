import {TFunction} from "react-i18next";
import {ActiveUserItem, LocationAction, REST} from "./rest-entities";

export {};

declare global {

    import PlayerCharacterItem = REST.PlayerCharacterItem;

    interface TranslationAsProperty {
        translation: TFunction<"translation", undefined>;
    }

    interface LocationActionList {
        list: LocationAction[];
    }

    interface CharacterInformation {
        character: PlayerCharacterData;
    }

    interface FilteredUserList {
        filteredUsers: ActiveUserItem[];
    }

    interface UserList {
        details: ActiveUserItem[];
    }

    type PlayerCharacterData = {
        character: PlayerCharacterItem;
    }

    interface LocationActionDetails {
        minimumLevel?: number;
        maximumLevel?: number;
        duration?: number;
        toll?: number;
    }

    interface LocationAction extends REST.LocationAction {
        details: LocationActionDetails;
    }
}