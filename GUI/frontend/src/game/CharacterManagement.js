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
    Input
} from 'reactstrap';
import { useParams, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

import { useSetState, useTrackedState } from '../SessionStore';

export const CharacterSelection = (props) => {
    // React i18n Hook
    const { t, i18n } = useTranslation(['character']);
    const setState = useSetState();
    const state = useTrackedState();

    // Variables
    const [characters, setCharacters] = useState(null);
    const [characterList, setCharacterList] = useState(null);

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
                data.map(character => {
                    return (
                        <tr>
                            <td>{character.name}</td>
                            <td>{character.race.name}</td>
                            <td>{character.characterClass.name}</td>
                            <td>{character.location.name}</td>
                            <td><Button>{t('character.list.select')}</Button></td>
                        </tr>
                    )
                })
            );
        }

        // call async method getSkill()
        getCharacters();
    }, [state.user]);

    return (
        <Container fluid>
            <h2>CharacterSelection TODO</h2>
            <span>Characters: {characters && characters.length}</span>

            <Table>
                <thead>
                    <tr>
                        <th>{t('character.list.name')}</th>
                        <th>{t('character.list.race')}</th>
                        <th>{t('character.list.class')}</th>
                        <th>{t('character.list.location')}</th>
                        <th>
                            <Button size="sm" color="primary" tag={Link} to={'/game/characters/create'}>{t('character.create')}</Button>
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
export const CharacterCreation = (props) => {
    // React i18n Hook
    const { t, i18n } = useTranslation(['character']);
    const userState = useTrackedState();

    // Variables
    const [classes, setClasses] = useState(null);
    const [races, setRaces] = useState(null);

    const [nameTaken, setNameTaken] = useState(null);

    useEffect(() => {
        // Define getCharacters method as async
        const getClasses = async() => {
            // await the response from the server
            const res = await fetch('/api/content/classes/playable');
            // await the json data in the response
            const data = await res.json();

            setClasses(
                data.map(clazz => {
                    return (
                        <option key={clazz.id} value={clazz.id}>{clazz.name}</option>
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
                data.map(race => {
                    return (
                        <option key={race.id} value={race.id}>{race.name}</option>
                    )
                })
            );
        }

        // call async method getClasses() + getRaces()
        getClasses();
        getRaces();
    }, []);

    function createCharacter(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        const creationBody = {name: data.get('name'), classId: data.get('class'), raceId: data.get('race')}

        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : userState.token.token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(creationBody)
        };

        fetch('/api/user/characters/create', requestOptions)
            .then((response) => {
                console.log(response);
                if(response.status == '409') {
                    // conflict because of name
                    console.log('Character exists by name!');
                    setNameTaken(true);
                }
                else if (response.status == '200') {
                    // success
                    console.log('Character created successfully!');
                    setNameTaken(false);
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
            {t('character.foo')}
            <Form onSubmit={createCharacter}>
                <FormGroup row>
                    <Label for="name" sm={2}>{t('creation.name')}</Label>
                    <Col sm={10}>
                        <Input invalid={nameTaken} id="name" name="name" placeholder={t('creation.name.long')} type="text" />
                        <FormFeedback>{t('creation.name.taken')}</FormFeedback>
                    </Col>
                </FormGroup>
                <FormGroup row>
                    <Label for="race" sm={2}>{t('creation.race')}</Label>
                    <Col sm={10}>
                        <Input id="race" name="race" placeholder={t('creation.name.long')} type="select">
                            {races}
                        </Input>
                    </Col>
                </FormGroup>
                <FormGroup row>
                    <Label for="class" sm={2}>{t('creation.class')}</Label>
                    <Col sm={10}>
                        <Input id="class" name="class" placeholder={t('creation.name.long')} type="select">
                            {classes}
                        </Input>
                    </Col>
                </FormGroup>
                <FormGroup check row>
                    <Col sm={{ offset: 2, size: 10 }}>
                        <Button type="submit">{t('creation.button.create')}</Button>
                    </Col>
                </FormGroup>
            </Form>
        </Container>
    )
}
