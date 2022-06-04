import React, {Component, useEffect, useState} from 'react';
import { Container, Button } from 'reactstrap';
import { Link } from 'react-router-dom';
import { withTranslation, WithTranslation } from "react-i18next";

const { default: logo } = require('./logo.svg') as { default: string };



class Home extends Component<WithTranslation> {
    state = {};
    render() {
        const { t } = this.props;
        return (
            <div className="App">
                <header>
                    <img src={logo} className="App-logo" alt="logo" />
                </header>
                <h1>Test</h1>
                {t('Foo')}
                <Container fluid>
                    <Button color="link"><Link to="/content/classes">Classes</Link></Button>
                </Container>
            </div>
        );
    }
};
export default withTranslation()(Home);