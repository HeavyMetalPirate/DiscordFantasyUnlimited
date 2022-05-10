import React, { Component, useEffect, useState } from 'react';
import { Button, Table } from 'reactstrap';
import { Link } from 'react-router-dom';
import { withTranslation } from "react-i18next";

import { useTrackedState } from '../SessionStore';
import logo from '../logo.svg';
import './GamePanel.css'

import { CharacterSelection, CurrentCharacterPanel } from './CharacterManagement'
import { EquipmentManager, InventoryManager } from './ItemManagement'

const GamePanel = (props) => {
    const state = useTrackedState();
    const t = props.translation;
    const [actions, setActions] = useState(null);

    useEffect(() => {
        if(!state.characterData || !state.selectedCharacter) return;

        const getActions = async() => {
            const res = await fetch('/api/game/location/' + state.characterData.location.id + '/actions')
            // await the json data in the response
            const data = await res.json();
            // set the state of the const 'skill'
            setActions(data);
        };
        getActions();
    }, [state.characterData]);

    function getTravelActions() {
        if(!actions) return;
        const travelActions = actions.filter(action => action.type === 'TRAVEL')
                                             .map(action => {
                                                if(action.requirementMet === false) {
                                                    return (
                                                        <tr key={action.text}>
                                                            <td>{t(action.text, {ns:'location'})}</td>
                                                            <td>{action.details.duration} {t('location.travel.duration', {ns: 'location'})}</td>
                                                            <td>{action.details.toll} {t('character.stats.gold', {ns: 'character'})}</td>
                                                            <td className="actionNotAllowed">{t(action.reason, {ns: 'location'})}</td>
                                                        </tr>
                                                    )
                                                }
                                                else {
                                                    return (
                                                        <tr key={action.text}>
                                                            <td>{t(action.text, {ns:'location'})}</td>
                                                            <td>{action.details.duration} {t('location.travel.duration')}</td>
                                                            <td>{action.details.toll} {t('character.stats.gold', {ns: 'character'})}</td>
                                                            <td><Button>{t('location.action.perform', {ns:'location'})}</Button></td>
                                                        </tr>
                                                    )
                                                }
                                             });
        return (
            <div className='travel-actions'>
                <h3>{t('game.actions.travel', {ns:'game'})}</h3>
                <Table>
                    <tbody>
                        {travelActions}
                    </tbody>
                </Table>
            </div>
        )
    }

    function getSecondarySkills() {
        if(!actions) return;

        const secondarySkillActions = actions.filter(action => action.type === 'SECONDARY_SKILL')
                                             .map(action => {
                                                if(action.requirementMet === false) {
                                                    return (
                                                        <tr key={action.text}>
                                                            <td>{t(action.text, {ns:'location'})}</td>
                                                            <td>{action.details.minimumLevel}</td>
                                                            <td className="actionNotAllowed">{t(action.reason, {ns: 'location'})}</td>
                                                        </tr>
                                                    )
                                                }
                                                else {
                                                    return (
                                                        <tr key={action.text}>
                                                            <td>{t(action.text, {ns:'location'})}</td>
                                                            <td>{action.details.minimumLevel}</td>
                                                            <td><Button>{t('location.action.perform', {ns:'location'})}</Button></td>
                                                        </tr>
                                                    )
                                                }
                                             });
        return (
            <div className='skills-actions'>
                <h3>{t('game.actions.skills', {ns:'game'})}</h3>
                <Table>
                    <tbody>
                        {secondarySkillActions}
                    </tbody>
                </Table>
            </div>
        )
    }

    function getTradingActions() {
        if(!actions) return;
        const tradingActions = actions
            .filter(action => (action.type === 'GLOBAL_TRADING' || action.type === 'TRADING'))
            .map(action => {
                if(action.requirementMet === false) {
                    return (
                        <tr key={action.text}>
                            <td>{t(action.text, {ns:'location'})}</td>
                            <td className="actionNotAllowed">{t('location.action.market.no.access', {ns: 'location'})}</td>
                        </tr>
                    )
                }
                else {
                    return (
                        <tr key={action.text}>
                            <td>{t(action.text, {ns:'location'})}</td>
                            <td><Button>{t('location.action.market.perform', {ns:'location'})}</Button></td>
                        </tr>
                    )
                }
            });
        return (
            <div className='market-actions'>
                <h3>{t('game.actions.trading', {ns:'game'})}</h3>
                <Table>
                    <tbody>
                        {tradingActions}
                    </tbody>
                </Table>
            </div>
        )
    }

    function getCombatActions() {
        if(!actions) return;
        const combatActions = actions
            .filter(action => (action.type === 'COMBAT'))
            .map(action => {
                return (
                    <tr key={action.text}>
                        <td>{t(action.text, {ns:'location'})}</td>
                        <td>{action.details.minimumLevel} - {action.details.maximumLevel}</td>
                        <td><Button>{t('location.action.combat.perform', {ns:'location'})}</Button></td>
                    </tr>
                )
            });
        return (
            <div className='combat-actions'>
                <h3>{t('game.actions.combat', {ns:'game'})}</h3>
                <Table>
                    <tbody>
                        {combatActions}
                    </tbody>
                </Table>
            </div>
        );
    }

    function getMiscActions() {
        if(!actions) return;
        const miscActions = actions
            .filter(action => (action.type === 'OTHER'))
            .map(action => {
                if(action.requirementMet === false) {
                    return (
                        <tr key={action.text}>
                            <td>{t(action.text, {ns:'location'})}</td>
                            <td className="actionNotAllowed">{t(action.reason, {ns: 'location'})}</td>
                        </tr>
                    )
                }
                else {
                    return (
                        <tr key={action.text}>
                            <td>{t(action.text, {ns:'location'})}</td>
                            <td><Button>{t('location.action.perform', {ns:'location'})}</Button></td>
                        </tr>
                    )
                }
            });
        return (
            <div className='misc-actions'>
                <h3>{t('game.actions.misc', {ns:'game'})}</h3>
                <Table>
                    <tbody>
                        {miscActions}
                    </tbody>
                </Table>
            </div>
        )
    }

    if(!state.selectedCharacter || state.selectedCharacter === '0') {
        return(
            <div>
                <CharacterSelection translation={t} />
            </div>
        )
    }

    if(!state.characterData) {
        return <div/>
    }

    return(
        <div>
            <h2>{t(state.characterData.location.name, {ns: 'location'})}</h2>
            {getCombatActions()}
            {getTravelActions()}
            {getSecondarySkills()}
            {getTradingActions()}
            {getMiscActions()}
        </div>
    )
}

const GameMainPanel = (props) => {
    const state = useTrackedState();
    const t = props.translation;
    const currentPanel = props.currentPanel;
    const [component, setComponent] = useState(null);

    useEffect(() => {
        if(currentPanel === 'GamePanel') {
            setComponent(<GamePanel translation={t} />);
        }
        else if(currentPanel === 'InventoryPanel') {
            setComponent(<InventoryManager translation={t} />);
        }
        else if(currentPanel === 'EquipmentPanel') {
            setComponent(<EquipmentManager translation={t} />);
        }
        else {
            setComponent(<div>Unknown Panel: '{currentPanel}'</div>);
        }
    }, [currentPanel]);

    return (
        <div>
            <div className="App Game MainPanel">
                <header>
                    <img src={logo} className="App-logo" alt="logo" />
                </header>
                {component}
            </div>
        </div>
    );
}

class GameHome extends Component {
    componentDidMount() {
    }

    render() {
        const t = this.props.t;
        const currentPanel = this.props.currentPanel;
        return (
            <GameMainPanel currentPanel={currentPanel} translation={t} />
        );
    }
};
export default withTranslation()(GameHome);