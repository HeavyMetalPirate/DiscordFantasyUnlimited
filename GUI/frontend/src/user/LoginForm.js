import React, { Component, useState } from 'react';
import { Container, Button, Form, FormGroup, Col, Input, Label, NavbarText } from 'reactstrap';
import { Link, useNavigate } from 'react-router-dom';
import { Trans } from 'react-i18next';
import { useTranslation } from "react-i18next";

import { useSetState, useTrackedState } from '../SessionStore';

export const LoginForm = (props) => {
    let navigate = useNavigate();
    const setUserState = useSetState();
    const fooState = useTrackedState();

    function submitForm(event) {
        event.preventDefault();
        const data = new FormData(event.target);

        const requestOptions = {
            method: 'POST'
        };

        fetch('/login?alias=' + data.get('username') + "&password=" + data.get('password'), requestOptions)
                .then((response) => window.location.href = response.url);
    }

    function logout(event) {
        event.preventDefault();
        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : fooState.token.token
            }
        };
        fetch('/logout', requestOptions)
                .then((response) => {
                    setUserState({user: null});
                    window.location.href = '/';
                });
    }

    // React i18n Hook
    const { t, i18n } = useTranslation();

    if(fooState.user) {
        return (
            <Container>
                <div>
                    <NavbarText>User: {fooState.user.name}</NavbarText>
                </div>
                <Container fluid>
                    <Form id="logoutForm" onSubmit={logout}>
                        <FormGroup check row>
                            <Col sm={{ offset: 2, size: 10 }}>
                                <Button type="submit">Logout</Button>
                            </Col>
                        </FormGroup>
                    </Form>
                </Container>
            </Container>
        )
    }

    return (
        <Container>
            <div>
                <Container fluid>
                    <Form id="registrationForm" onSubmit={submitForm}>
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
                </Container>
            </div>
        </Container>
    );
};