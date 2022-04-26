import React, {Component} from 'react';
import { push as Menu } from 'react-burger-menu'

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