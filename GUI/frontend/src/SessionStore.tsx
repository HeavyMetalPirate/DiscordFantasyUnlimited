import { useState } from "react";
import { createContainer } from "react-tracked";

const initialState: {
    user: UserPrincipal | null;
    token: CsrfToken | null;
    selectedCharacter: string | null;
    characterData: PlayerCharacterData | null;
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