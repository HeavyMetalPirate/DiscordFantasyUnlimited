import React, {Component} from 'react';
import { Container, Button, Navbar, NavbarText } from 'reactstrap';
import { Link } from 'react-router-dom';
import { push as Menu } from 'react-burger-menu'
import { LoginForm } from '../user/LoginForm'

export default class AppNavbar extends Component {
    constructor(props) {
        super(props);
        this.state = {isOpen: false};
        this.toggle = this.toggle.bind(this);
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    render() {
        return (
        <div>
            <Navbar dark color="dark">
                <span className="bannerLogin">
                    <LoginForm />
                </span>
            </Navbar>
            <Menu
                noOverlay
                outerContainerId={"baseContainer"}
                pageWrapId={"page-wrap"}>
                    <a id="home" className="menu-item" href="/">Home</a>
                    <a id="about" className="menu-item" href="/content/classes">Classes</a>
            </Menu>
        </div>
        );
    }
}