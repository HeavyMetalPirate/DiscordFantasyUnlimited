import { useState } from "react";
import { createContainer } from "react-tracked";

import * as FantasyUnlimited from "./types/rest-entities";

const initialState: {
    user: FantasyUnlimited.REST.Principal | null;
    token: FantasyUnlimited.REST.CsrfToken | null;
    selectedCharacter: string | null;
    characterData: FantasyUnlimited.REST.PlayerCharacterItem | null;
    stateChanged: boolean;
} = {
    user: null,
    token: null,
    selectedCharacter: null,
    characterData: null,
    stateChanged: false
}

const useValue = () => useState(initialState);

export const {
    Provider,
    useTracked: useTrackedState
} = createContainer(useValue);