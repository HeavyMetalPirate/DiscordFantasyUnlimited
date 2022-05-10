import './App.css';
import './game/GamePanel.css';
import React, { Component, useEffect } from 'react';
import {
       BrowserRouter as Router,
       Routes,
       Route
} from "react-router-dom";
import { withTranslation } from "react-i18next";

import { Provider, useSetState, useTrackedState } from './SessionStore'
import Home from './Home'
import GameHome from './game/GameHome'
import { CharacterCreation, CurrentCharacterPanel } from './game/CharacterManagement'
import RegisterForm from './user/RegisterForm'
import { LoginForm } from './user/LoginForm'
import AppNavbar from './items/AppNavBar';
import ClassesTableComponent from './items/table/ClassesTableComponent'
import { ClassesDetailView, SkillsDetailView } from './items/details/DetailViews'

export const UserInformation = () => {
    const setState = useSetState();
    const fooState = useTrackedState();

    useEffect(() => {
        const userState = async() => {
            console.log("Fetching current user...");
            const resp = await fetch('/api/user/current');
            try {
                // will only return json if logged in
                const data = await resp.json();
                console.log(data);
                setState({ user: data.principal, token: data.csrf, selectedCharacter: data.character});
                console.log("selected character: " + fooState.selectedCharacter);
            }
            catch(error) {
                // clean up state
                console.log(error);
                console.log("Resetting state");
                setState((prevState) => ({user: null, token: null, selectedCharacter: null}));
            }
        }
        userState();
    }, []);

    return (
        <span />
    )
}

class App extends Component {
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
                        <Routes>
                            <Route exact path='/' element=<Home /> />
                            <Route exact path='/login' element=<LoginForm /> />
                            <Route exact path='/register' element=<RegisterForm /> />
                            <Route exact path='/game' element=<GameHome currentPanel="GamePanel" translation={t} /> />
                            <Route exact path='/game/characters/create' element=<CharacterCreation translation={t} /> />
                            <Route exact path='/game/inventory' element=<GameHome currentPanel="InventoryPanel" translation={t} /> />
                            <Route exact path='/game/equipment' element=<GameHome currentPanel="EquipmentPanel" translation={t} /> />
                            <Route exact path='/content/classes' element=<ClassesTableComponent /> />
                            <Route path='/content/classes/:id' element=<ClassesDetailView /> />
                            <Route path='/content/skills/:id' element=<SkillsDetailView /> />
                        </Routes>
                    </main>
                </Router>
            </Provider>
            </div>
        );
    }
}
export default withTranslation(['translation', 'location', 'game', 'skills', 'character', 'race', 'characterClass', 'items'])(App);