import { useState } from "react";
import { createContainer } from "react-tracked";

import * as FantasyUnlimited from "./types/rest-entities";

export const initialState: {
    user: FantasyUnlimited.REST.Principal | null;
    token: FantasyUnlimited.REST.CsrfToken | null;
    selectedCharacter: string | null;
    characterData: FantasyUnlimited.REST.PlayerCharacterItem | null;
    characterEquipmentChange: number;
    stateChanged: boolean;
    globalErrorMessage: ErrorMessage | null;
    activeBattleId: string | null;
} = {
    user: null,
    token: null,
    selectedCharacter: null,
    characterData: null,
    characterEquipmentChange: 0,
    stateChanged: false,
    globalErrorMessage: null,
    activeBattleId: null
}

const useValue = () => useState(initialState);

export const {
    Provider,
    useTracked: useTrackedState
} = createContainer(useValue);