import React, { Component, useEffect } from 'react';
import { Container, Button } from 'reactstrap';
import { Link } from 'react-router-dom';
import { Trans } from 'react-i18next';
import { withTranslation } from "react-i18next";

import { useSetState, useTrackedState } from '../SessionStore';
import logo from '../logo.svg';

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
                setUserState({user: null, token: null});
            }
        }
        userState();
    }, []);

    return (
        <span />
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
            </div>
        );
    }
};
export default withTranslation()(GameHome);