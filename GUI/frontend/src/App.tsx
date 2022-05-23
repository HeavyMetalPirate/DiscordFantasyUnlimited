import './App.css';
import './game/GamePanel.css';
import React, { Component, useEffect } from 'react';
import {
       BrowserRouter as Router,
       Routes,
       Route
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

export const UserInformation = () => {
    const [fooState, setState] = useTrackedState();

    useEffect(() => {
        const userState = async() => {
            const resp = await fetch('/api/user/current');
            try {
                // will only return json if logged in
                const data = await resp.json();

                let principal: FantasyUnlimited.REST.Principal = {
                    name: data.principal.name
                }
                let csrfToken: FantasyUnlimited.REST.CsrfToken = {
                    token: data.csrf.token,
                    headerName: data.csrf.headerName,
                    parameterName: data.csrf.parameterName
                }
                let selectedChar: string = data.character;

                setState((prevState) => ({ ...prevState, user: principal, token: csrfToken, selectedCharacter: selectedChar}));
            }
            catch(error) {
                // clean up state
                console.log("Error in App/UserInformation/UseEffect")
                console.log(error);
                setState((prevState) => ({ ...prevState, user: null, token: null, selectedCharacter: null}));
            }
        }
        userState();
    }, []);

    return (
        <span />
    )
}

interface AppProperties extends WithTranslation {
}
interface AppState {

}

class App extends Component<AppProperties, AppState> {
    render() {
        const t = this.props.t;
        console.log("App/t:");
        console.log(t);
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
                        <Routes>
                            <Route path='/' element={<Home />} />
                            <Route path='/login' element={<LoginForm />} />
                            <Route path='/register' element={<RegisterForm />} />
                            <Route path='/game' element={<GameHome currentPanel="GamePanel" translation={t} />} />
                            <Route path='/game/characters/create' element={<CharacterCreation translation={t} />} />
                            <Route path='/game/inventory' element={<GameHome currentPanel="InventoryPanel" translation={t} />} />
                            <Route path='/game/equipment' element={<GameHome currentPanel="EquipmentPanel" translation={t} />} />
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
export default withTranslation(['translation', 'location', 'game', 'skills', 'character', 'race', 'characterClass', 'items'])(App);