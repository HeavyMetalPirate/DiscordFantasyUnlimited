import React, { Component, useState } from 'react';
import { Container,
         Button,
         Form,
         FormGroup,
         Col,
         Input,
         Label,
         NavbarText,
         Card,
         CardBody,
         CardTitle,
         CardSubtitle
} from 'reactstrap';
import { Link, useNavigate } from 'react-router-dom';
import { Trans } from 'react-i18next';
import { useTranslation } from "react-i18next";

import { useTrackedState } from '../SessionStore';

export const LoginForm = (): JSX.Element => {
    let navigate = useNavigate();
    const [fooState, setFooState] = useTrackedState();

    function submitForm(event: React.SyntheticEvent) {
        event.preventDefault();
        //const data = new FormData(event.target);

        const target = event.target as typeof event.target & {
              username: { value: string };
              password: { value: string };
        };
        const username = target.username.value; // typechecks!
        const password = target.password.value; // typechecks!

        const requestOptions = {
            method: 'POST'
        };

        fetch('/login?alias=' + username + "&password=" + password, requestOptions)
                .then((response) => {
                    setFooState((prev) => ({ ...prev, stateChanged: true}));
                    window.location.href = response.url;
                });
    }

    function logout(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        let token = fooState.token!.token;

        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : token
            }
        };
        fetch('/logout', requestOptions)
                .then((response) => {
                    setFooState({user: null, token: null, selectedCharacter: null, stateChanged: true, characterData: null});
                    window.location.href = '/';
                });
    }

    // React i18n Hook
    const { t, i18n } = useTranslation();

    if(fooState.user) {
        return (
            <Container fluid style={{paddingRight: "unset", paddingLeft: "unset", height: "9em"}}>
                <Card style={{height: "100%"}}>
                    <CardBody>
                        <CardTitle tag="h5">User: {fooState.user.name}</CardTitle>
                        <Form id="logoutForm" onSubmit={logout}>
                            <FormGroup check row>
                                <Col sm={{ offset: 2, size: 10 }}>
                                    <Button type="submit">Logout</Button>
                                </Col>
                            </FormGroup>
                        </Form>
                    </CardBody>
                </Card>
            </Container>
        )
    }

    return (
        <Container fluid style={{paddingRight: "unset", paddingLeft: "unset", height: "9em"}}>
            <Card style={{height: "100%"}}>
                <CardBody>
                    <Form className="loginForm" onSubmit={submitForm}>
                        <FormGroup row>
                            <Col sm={10}>
                                <Input id="username" name="username" placeholder="Username/Email" type="text" />
                            </Col>
                        </FormGroup>
                        <FormGroup row>
                            <Col sm={10}>
                                <Input id="password" name="password" placeholder="Password" type="password" />
                            </Col>
                        </FormGroup>
                        <FormGroup check row>
                            <Col sm={{ offset: 2, size: 10 }}>
                                <Button type="submit">Login</Button>
                            </Col>
                        </FormGroup>
                    </Form>
                </CardBody>
            </Card>
        </Container>
    );
};