import React, { Component, useEffect } from 'react';
import { Container, Button } from 'reactstrap';
import { Link } from 'react-router-dom';
import { Trans } from 'react-i18next';
import { withTranslation } from "react-i18next";

import { useSetState, useTrackedState } from '../SessionStore';
import logo from '../logo.svg';

import { CharacterSelection, CharacterCreation } from './CharacterManagement'

const GamePanel = (props) => {
    const setState = useSetState();
    const state = useTrackedState();

    if(!state.character) {
        return(
            <CharacterSelection />
        )
    }

    return(
        <div>
            GamePanel etc.
        </div>
    )
}

class GameHome extends Component {
    state = {};
    render() {
        const { t } = this.props;
        return (
            <div className="App">
                <header>
                    <img src={logo} className="App-logo" alt="logo" />
                </header>
                <h1>GameHome TODO</h1>
                <GamePanel />
            </div>
        );
    }
};
export default withTranslation()(GameHome);