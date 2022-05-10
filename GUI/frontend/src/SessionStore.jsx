import { useState } from "react";
import { createContainer } from "react-tracked";

const useValue = () => useState({
    user: null,
    token: null,
    selectedCharacter: null,
    stateChanged: false
});

export const {
  Provider,
  useTrackedState,
  useUpdate: useSetState
} = createContainer(useValue);