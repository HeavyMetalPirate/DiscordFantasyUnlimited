import React, {useEffect, useState} from 'react';
import {
    Container,
    Table,
    Button,
    Form,
    FormGroup,
    FormFeedback,
    Label,
    Col,
    Input,
    Card,
    CardBody,
    CardTitle,
    CardSubtitle
} from 'reactstrap';
import { useNavigate, Link } from 'react-router-dom';
import * as FantasyUnlimited from "../types/rest-entities";
import * as ItemTypes from "../types/itemhandling"

import './GamePanel.css'
import { get_steps, calculateHealthPercentage, calculateResourcePercentage } from './utils/StatusbarUtils'
import { useTrackedState } from '../SessionStore';
import { UserSearch } from '../user/UserComponents'

const ManaBar = ({character}: PlayerCharacterData) => {
    let steps;

    if(character.resources.energyType === 'RAGE') {
        // Red
        steps = get_steps('rgb(155, 0, 0)', 'rgb(255, 0, 0)' , 100);
    }
    else if(character.resources.energyType === 'FOCUS') {
        // Yellow
        steps = get_steps('rgb(155, 155, 0)', 'rgb(255, 255, 0)' , 100);
    }
    else {
        // Mana, blue
        steps = get_steps('rgb(0, 0, 155)', 'rgb(0, 0, 255)' , 100);
    }

    const p = calculateResourcePercentage(character);

    return (
        <div className="player">
            <div className="mana">
                <div className="bar" style= {{ width: p + '%', background: Object.keys(steps)[Math.floor(p)] }}></div>
                <span className="stat">
                    <span className="left">{ character.resources.currentResource }</span>
                    <span className="right">{ character.resources.maxResource }</span>
                </span>
            </div>
        </div>
    )
}

const HealthBar = ({character}: PlayerCharacterData) => {

    const steps = get_steps('rgb(255, 0, 0)', 'rgb(0, 128, 0)' , 100);
    const p = calculateHealthPercentage(character);

    return (
        <div className="player">
            <div className="health">
                <div className="bar" style= {{ width: p + '%', background: Object.keys(steps)[Math.floor(p)] }}></div>
                <span className="stat">
                    <span className="left">{ character.resources.currentHealth }</span>
                    <span className="right">{ character.resources.maxHealth }</span>
                </span>
            </div>
        </div>
    )
}

export const CurrentCharacterPanel = ({translation}: TranslationAsProperty) => {
    const t = translation;
    const [state, setState] = useTrackedState();
    const navigate = useNavigate();

    const [character, setCharacter] = useState<FantasyUnlimited.REST.PlayerCharacterItem | null>(null);
    const [currentActiveUsers, setCurrentActiveUsers] = useState<FantasyUnlimited.REST.ActiveUserItem[]>([]);

    useEffect(() => {
        const getCharacter = async() => {
            // await the response from the server
            const res = await fetch('/api/user/characters/get');
            try {
                // await the json data in the response
                const data = await res.json();
                // set the state of the const 'skill'
                setCharacter(data);
                setState((prevState) => ({ ...prevState, characterData: data}));
            }
            catch(error) {
                setCharacter(null);
                setState((prevState) => ({ ...prevState, characterData: null}));
            }
        };
        if(state.selectedCharacter && state.selectedCharacter !== null && state.selectedCharacter !== '0') {
            getCharacter();
        }
    }, [state.selectedCharacter, state.characterEquipmentChange]);

    useEffect(() => {
        const getActiveUsers = async() => {
            const res = await fetch('/api/user/actives')
            const data = await res.json();

            setCurrentActiveUsers(data);
        };
        getActiveUsers();
    }, []); // TODO how to refresh? https://stackoverflow.com/questions/59667278/react-hooks-periodic-run-useeffect

    if(!character) {
        return (
            <Container fluid style={{paddingRight: "unset", paddingLeft: "unset"}}>
                <Card>

                </Card>
            </Container>
        )
    }

    function goToSelection() {
        setState((prev) => ({ ...prev,
            selectedCharacter: null,
            characterData: null,
            stateChanged: true,
            activeBattleId: null
        }));
        fetch('/api/user/characters/select?id=0')
            .then(response => navigate('/game'));
    }

    function goToEquipment() {
        navigate('/game/equipment');
    }
    function goToInventory() {
        navigate('/game/inventory');
    }

    return (
        <Container fluid style={{paddingRight: "unset", paddingLeft: "unset" }}>
            <Card>
                <CardBody>
                    <CardTitle tag="h5">{character.name}</CardTitle>
                    <CardSubtitle className="mb-2 text-muted" tag="h6">{t('character.current.panel.level', {ns: 'character'})} {character.level} {t(character.race.name, {ns: 'race'})} {t(character.characterClass.name, {ns: 'characterClass'})} </CardSubtitle>
                    <CardSubtitle className="mb-2 text-muted" tag="h6">{t(character.location.name, {ns:'location'})}</CardSubtitle>
                </CardBody>
                <CardBody>
                    <span style={{"textAlign":"left", display: "block"}}>{t('character.current.panel.health', {ns: 'character'})}:</span>
                    <span style={{ width: "9em" }}><HealthBar character={character} /></span>

                    <span style={{"textAlign":"left", display: "block"}}>{t('character.current.panel.' + character.resources.energyType, {ns: 'character'})}:</span>
                    <span style={{ width: "9em" }}><ManaBar character={character} /></span>
                </CardBody>
                <Container style={{marginBottom: "2px"}}>
                    <Button style={{"margin": "3px", width: "46%"}} onClick={() => goToEquipment()}>{t('character.current.panel.manage.equipment', {ns: 'character'})}</Button>
                    <Button style={{"margin": "3px", width: "46%"}} onClick={() => goToInventory()}>{t('character.current.panel.manage.inventory', {ns: 'character'})}</Button>
                </Container>
                <img alt="Card cap" src="images/fu.png" />
                <Button style={{"margin": "5px"}} onClick={() => goToSelection()}>{t('character.current.panel.select', {ns: 'character'})}</Button>

                <CardBody>
                    <UserSearch details={currentActiveUsers} />
                </CardBody>
            </Card>
        </Container>
    );
}

export const CharacterSelection = ({translation}: TranslationAsProperty) => {

    const t = translation;
    const [state, setState] = useTrackedState();
    const navigate = useNavigate();

    // Variables
    const [characters, setCharacters] = useState<PlayerCharacterData[]>([]);
    const [characterList, setCharacterList] = useState(null);

    function selectCharacter(id: number) {
        fetch('/api/user/characters/select?id=' + id)
            .then(() => {
                setState((prevState) => ({ ...prevState, selectedCharacter: '' + id, stateChanged: true}))
                navigate('/game');
            });

    }

    useEffect(() => {
        // Define getSkill method as async
        const getCharacters = async() => {
            // await the response from the server
            const res = await fetch('/api/user/characters');
            // await the json data in the response
            const data = await res.json();
            // set the state of the const 'skill'
            setCharacters(data);

            setCharacterList(
                data.map((character: FantasyUnlimited.REST.CharacterListItem) => {
                    return (
                        <tr key={character.id}>
                            <td>{character.name}</td>
                            <td>{character.level}</td>
                            <td>{character.race.name}</td>
                            <td>{character.characterClass.name}</td>
                            <td>{character.location.name}</td>
                            <td><Button onClick={() => selectCharacter(character.id)}>{t('character.list.select', {ns: 'character'})}</Button></td>
                        </tr>
                    )
                })
            );
        }

        // call async method getCharacters()
        getCharacters();
    }, [state.user]);

    return (
        <Container fluid>
            <h2>{t('character.selection.header', {ns: 'character'})}</h2>
            <span>Characters: {characters && characters.length}</span>

            <Table>
                <thead>
                    <tr>
                        <th>{t('character.list.name', {ns: 'character'})}</th>
                        <th>{t('character.list.level', {ns: 'character'})}</th>
                        <th>{t('character.list.race', {ns: 'character'})}</th>
                        <th>{t('character.list.class', {ns: 'character'})}</th>
                        <th>{t('character.list.location', {ns: 'character'})}</th>
                        <th>
                            <Button size="sm" color="primary" tag={Link} to={'/game/characters/create'}>{t('character.create', {ns: 'character'})}</Button>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    {characterList}
                </tbody>
            </Table>
        </Container>
    )
}
export const CharacterCreation = ({translation}: TranslationAsProperty) => {
    const t = translation;
    const [userState, setUserState] = useTrackedState();
    const navigate = useNavigate();

    // Variables
    const [classes, setClasses] = useState(null);
    const [races, setRaces] = useState(null);

    const [nameTaken, setNameTaken] = useState<boolean>(false);

    useEffect(() => {
        // Define getCharacters method as async
        const getClasses = async() => {
            // await the response from the server
            const res = await fetch('/api/content/classes/playable');
            // await the json data in the response
            const data = await res.json();

            setClasses(
                data.map((clazz: FantasyUnlimited.REST.ClassItem) => {
                    return (
                        <option key={clazz.id} value={clazz.id}>{t(clazz.name, {ns: 'characterClass'})}</option>
                    )
                })
            );
        }
        // Define getRaces method as async
        const getRaces = async() => {
            // await the response from the server
            const res = await fetch('/api/content/races/playable');
            // await the json data in the response
            const data = await res.json();

            setRaces(
                data.map((race: FantasyUnlimited.REST.RaceItem) => {
                    return (
                        <option key={race.id} value={race.id}>{t(race.name, {ns: 'race'})}</option>
                    )
                })
            );
        }

        // call async method getClasses() + getRaces()
        getClasses();
        getRaces();
    }, []);

    function createCharacter(event: React.SyntheticEvent) {
        event.preventDefault();

        const target = event.target as typeof event.target & {
            name: { value: string };
            class: { value: string };
            race: { value: string };
        };
        const creationBody: FantasyUnlimited.REST.CharacterCreationBody = {
            name: target.name.value,
            classId: target.class.value,
            raceId: target.race.value
        }

        let token: string = userState.token!.token;
        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(creationBody)
        };

        fetch('/api/user/characters/create', requestOptions)
            .then((response) => {
                console.log(response);
                if(response.status === 409) {
                    // conflict because of name
                    console.log('Character exists by name!');
                    setNameTaken(true);
                }
                else if (response.status === 200) {
                    // success
                    console.log('Character created successfully!');
                    setNameTaken(false);
                    navigate('/game');
                }
                else {
                    console.log('Unknown error ' + response.status);
                    setNameTaken(false);
                }
            });

    };

    return (
        <Container fluid>
            <h2>CharacterCreation TODO</h2>
            {t('character.foo', {ns: 'character'})}
            <Form onSubmit={createCharacter}>
                <FormGroup row>
                    <Label for="name" sm={2}>{t('creation.name', {ns: 'character'})}</Label>
                    <Col sm={10}>
                        <Input invalid={nameTaken} id="name" name="name" placeholder={t('creation.name.long', {ns: 'character'})} type="text" />
                        <FormFeedback>{t('creation.name.taken', {ns: 'character'})}</FormFeedback>
                    </Col>
                </FormGroup>
                <FormGroup row>
                    <Label for="race" sm={2}>{t('creation.race', {ns: 'character'})}</Label>
                    <Col sm={10}>
                        <Input id="race" name="race" placeholder={t('creation.name.long', {ns: 'character'})} type="select">
                            {races}
                        </Input>
                    </Col>
                </FormGroup>
                <FormGroup row>
                    <Label for="class" sm={2}>{t('creation.class', {ns: 'character'})}</Label>
                    <Col sm={10}>
                        <Input id="class" name="class" placeholder={t('creation.name.long', {ns: 'character'})} type="select">
                            {classes}
                        </Input>
                    </Col>
                </FormGroup>
                <FormGroup check row>
                    <Col sm={{ offset: 2, size: 10 }}>
                        <Button type="submit">{t('creation.button.create', {ns: 'character'})}</Button>
                    </Col>
                </FormGroup>
            </Form>
        </Container>
    )
}
