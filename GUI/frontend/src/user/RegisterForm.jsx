import React, { Component } from 'react';
import { Container, Button, Form, FormGroup, Col, Input, Label } from 'reactstrap';
import { Link } from 'react-router-dom';
import { Trans } from 'react-i18next';
import { withTranslation } from "react-i18next";

function submitForm(event) {
    event.preventDefault();
    const data = new FormData(event.target);
    console.log(data.get('username'));
    console.log(data.get('email'));
    console.log(data.get('password'));
    const registerRequestBody = {username: data.get('username'), email: data.get('email'), password: data.get('password')};

    (async () => {
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(registerRequestBody)
        };

        const response = await fetch('/api/user/register', requestOptions);
        const data = await response.json();
    })();
}

class RegisterForm extends Component {
    state = {};

    render() {
        const { t } = this.props;
        return (
            <Container>
                <div>
                    <Container fluid>
                        <Form id="registrationForm" onSubmit={submitForm}>
                            <FormGroup row>
                                <Label for="username" sm={2}>Username</Label>
                                <Col sm={10}>
                                    <Input id="username" name="username" placeholder="Username" type="text" />
                                </Col>
                            </FormGroup>
                            <FormGroup row>
                                <Label for="email" sm={2}>Email</Label>
                                <Col sm={10}>
                                    <Input id="email" name="email" placeholder="E-Mail" type="email" />
                                </Col>
                            </FormGroup>
                            <FormGroup row>
                                <Label for="password" sm={2}>Password</Label>
                                <Col sm={10}>
                                    <Input id="password" name="password" placeholder="Password" type="password" />
                                </Col>
                            </FormGroup>
                            <FormGroup check row>
                                <Col sm={{ offset: 2, size: 10 }}>
                                    <Button type="submit">Submit</Button>
                                </Col>
                            </FormGroup>
                        </Form>
                    </Container>
                </div>
            </Container>
        );
    }
};
export default withTranslation()(RegisterForm);