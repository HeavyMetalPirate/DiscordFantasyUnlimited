import React, {Component} from 'react';
import { Container, Button, Navbar, NavbarBrand, NavbarText } from 'reactstrap';
import { Link } from 'react-router-dom';
import { push as Menu } from 'react-burger-menu'
import { LoginForm } from '../user/LoginForm'

interface NavbarProps {

}
interface NavbarState {
    isOpen: boolean;
}

export default class AppNavbar extends Component<NavbarProps, NavbarState> {
    constructor(props: NavbarProps) {
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
        <div id="menuContainer" style={{"marginBottom": "9em"}}>
            <Navbar dark color="dark" fixed="top" style={{ "minHeight":"9em", "maxHeight": "9em", right: "250px" }}>
                <NavbarBrand style={{"marginLeft": "4em"}} href='/'>
                    <h1>Fantasy Unlimited</h1>
                </NavbarBrand>
            </Navbar>
            <Menu
                customBurgerIcon={<img src="/images/fu.png" />}
                noOverlay
                outerContainerId={"menuContainer"}
                pageWrapId={"page-wrap"}>
                    <a id="home" className="menu-item" href="/">Home</a>
                    <a id="about" className="menu-item" href="/content/classes">Classes</a>
            </Menu>
        </div>
        );
    }
}