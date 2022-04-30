import './App.css';
import React, { Component, useState, useEffect } from 'react';
import {
       BrowserRouter as Router,
       Routes,
       Route
} from "react-router-dom";
import { createContainer } from 'react-tracked';

import { Provider, useSetState, useTrackedState } from './SessionStore'
import Home from './Home'
import GameHome from './game/GameHome'
import { CharacterCreation } from './game/CharacterManagement'
import RegisterForm from './user/RegisterForm'
import { LoginForm } from './user/LoginForm'
import AppNavbar from './items/AppNavBar';
import ClassesTableComponent from './items/table/ClassesTableComponent.js'
import { ClassesDetailView, SkillsDetailView } from './items/details/DetailViews.js'

export const UserInformation = () => {
    const fooState = useTrackedState();
    const setUserState = useSetState();

    useEffect(() => {
        const userState = async() => {
            console.log("Fetching current user...");
            const resp = await fetch('/api/user/current');
            try {
                // will only return json if logged in
                const data = await resp.json();
                console.log(data);
                setUserState({user: data.principal, token: data.csrf});
            }
            catch(error) {
                // clean up state
                setUserState({user: null, token: null, character: null});
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
        return (
            <div id="baseContainer" className="App">
            <Provider>
                <UserInformation />
                <Router>
                    <AppNavbar/>
                    <main id="page-wrap">
                        <Routes>
                            <Route exact path='/' element=<Home /> />
                            <Route exact path='/login' element=<LoginForm /> />
                            <Route exact path='/register' element=<RegisterForm /> />
                            <Route exact path='/game' element=<GameHome /> />
                            <Route exact path='/game/characters/create' element=<CharacterCreation /> />
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
export default App;