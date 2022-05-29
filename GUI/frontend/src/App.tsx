import './App.css';
import './game/GamePanel.css';
import React, {Component, useEffect, useState} from 'react';
import {
    BrowserRouter as Router,
    Routes,
    Route,
    useLocation, Link
} from "react-router-dom";
import {WithTranslation, withTranslation} from "react-i18next";

import { Provider, useTrackedState } from './SessionStore'
import Home from './Home'
import GameHome from './game/GameHome'
import { CharacterCreation, CurrentCharacterPanel } from './game/CharacterManagement'
import RegisterForm from './user/RegisterForm'
import { LoginForm } from './user/LoginForm'
import AppNavbar from './items/AppNavBar';
import ClassesTableComponent from './items/table/ClassesTableComponent'
import { ClassesDetailView, SkillsDetailView } from './items/details/DetailViews'
import * as FantasyUnlimited from "./types/rest-entities";
import {TFunction} from "i18next";
import {Alert, Button} from "reactstrap";
import {BattleMainPanel} from "./game/BattleComponents";

export const UserInformation = () => {
    const [fooState, setState] = useTrackedState();

    useEffect(() => {
        const userState = async() => {
            const resp = await fetch('/api/user/current');
            const data = await resp.json();
            return data;
        }
        userState()
            .then(data => {
                try {
                    // will only return json if logged in
                    let principal: FantasyUnlimited.REST.Principal = {
                        name: data.principal.name
                    }
                    let csrfToken: FantasyUnlimited.REST.CsrfToken = {
                        token: data.csrf.token,
                        headerName: data.csrf.headerName,
                        parameterName: data.csrf.parameterName
                    }
                    let selectedChar: string = data.character;

                    setState((prevState) => ({ ...prevState,
                        user: principal,
                        token: csrfToken,
                        selectedCharacter: selectedChar
                    }));
                }
                catch(error) {
                    // clean up state
                    console.log("Error in App/UserInformation/UseEffect")
                    console.log(error);
                    setState((prevState) => ({ ...prevState,
                        user: null,
                        token: null,
                        selectedCharacter: null
                    }));
                }
            });
    }, []);

    useEffect(() => {
        const userBattle = async() => {
            const resp = await fetch('/api/game/battle/current');
            const data = await resp.json();
            return data;
        }
        userBattle()
            .then(data => {
                if(data === null) {
                    setState((prevState) => ({ ...prevState, activeBattleId: null}));
                }
                else {
                    let battleInfo: FantasyUnlimited.REST.BattleBasicInfo = data;
                    console.log("setting activeBattleId from " + fooState.activeBattleId)
                    setState((prevState) => ({ ...prevState, activeBattleId: battleInfo.id}));
                }
            });
    }, [fooState.characterData])

    return (
        <span />
    )
}

interface GlobalStateProps extends TranslationAsProperty {}
const GlobalStatePanel = ({translation}: GlobalStateProps) => {
    const t = translation;
    const [state, setState] = useTrackedState();
    const [activeBattleId, setActiveBattleId] = useState<string | null>();
    let location = useLocation();
    // figure out if the character currently is in an action like battle or travel
    // and display a message with link to the right spot

    useEffect(() => {
        console.log("global state");
        console.log(state.activeBattleId);
        setActiveBattleId(state.activeBattleId);
    }, [state.activeBattleId])

    if(activeBattleId !== null && (location && location.pathname.startsWith('/game/battle') === false)) {
        console.log(location)
        console.log('Returning activeBattleId');
        return (
            <div className={"global-info-panel"}>
                <Alert color="primary">
                    <h4 className="primary-heading">{t('game.battle.active.header', {ns: 'game'})}</h4>
                    <p className="mb-0">{t('game.battle.active.text', {ns: 'game'})}</p>
                    <Button color="link"><Link to={"/game/battle/" + activeBattleId}>{t('game.battle.active.button', {ns: 'game'})}</Link></Button>
                </Alert>
            </div>
        )
    }
    console.log('Returning nothing.');
    console.log(activeBattleId);
    return <div/>
}

interface ErrorPanelProps extends TranslationAsProperty {}
const GlobalErrorPanel = ({translation}: ErrorPanelProps) => {
    const t = translation;
    const [state, setState] = useTrackedState();
    let location = useLocation();
    const [errorMessage, setErrorMessage] = useState<string>();
    const [errorHeader, setErrorHeader] = useState<string>();

    useEffect(() => {
        setState((prevState) => ({ ...prevState, globalErrorMessage: null}));
    }, [location])

    useEffect(() => {
        console.log(state.globalErrorMessage);
        if(state.globalErrorMessage !== null) {
            setErrorMessage(state.globalErrorMessage.body);
            setErrorHeader(state.globalErrorMessage.header);
        }
        else {
            setErrorMessage(undefined);
            setErrorHeader(undefined);
        }
    }, [state.globalErrorMessage])

    if (errorMessage == null) return null;

    return (
        <div className={"global-error-panel"}>
            <Alert color="danger">
                <h4 className="alert-heading">{errorHeader}</h4>
                <p className="mb-0">{errorMessage}</p>
            </Alert>
        </div>
    )
}

interface AppProperties extends WithTranslation {
}
interface AppState {
}
class App extends Component<AppProperties, AppState> {

    render() {
        const t = this.props.t;
        return (
            <div id="baseContainer" className="App full-height">
            <Provider>
                <UserInformation />
                <Router>
                    <AppNavbar />
                    <div className="game-sidebar" style={{height: "100%"}}>
                        <LoginForm />
                        <CurrentCharacterPanel translation={t} />
                    </div>
                    <main id="page-wrap" className="full-height">
                        <GlobalErrorPanel translation={t} />
                        <GlobalStatePanel translation={t} />
                        <Routes>
                            <Route path='/' element={<Home />} />
                            <Route path='/login' element={<LoginForm />} />
                            <Route path='/register' element={<RegisterForm />} />
                            <Route path='/game' element={<GameHome currentPanel="GamePanel" translation={t} />} />
                            <Route path='/game/characters/create' element={<CharacterCreation translation={t} />} />
                            <Route path='/game/inventory' element={<GameHome currentPanel="InventoryPanel" translation={t} />} />
                            <Route path='/game/equipment' element={<GameHome currentPanel="EquipmentPanel" translation={t} />} />
                            <Route path='/game/battle/:id' element={<BattleMainPanel translation={t} />} />
                            <Route path='/content/classes' element={<ClassesTableComponent />} />
                            <Route path='/content/classes/:id' element={<ClassesDetailView />} />
                            <Route path='/content/skills/:id' element={<SkillsDetailView />} />
                        </Routes>
                    </main>
                </Router>
            </Provider>
            </div>
        );
    }
}
export default withTranslation(['translation', 'location', 'game', 'skills', 'character', 'race', 'characterClass', 'items', 'battle'])(App);