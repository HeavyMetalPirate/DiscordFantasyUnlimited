import { useState } from "react";
import { createContainer } from "react-tracked";

const useValue = () => useState({
    user: null,
    token: null,
    character: null
});

export const {
  Provider,
  useTrackedState,
  useUpdate: useSetState
} = createContainer(useValue);