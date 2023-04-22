import React, { Component } from 'react';
import { Container, Table, Button } from 'reactstrap';
import { Link } from 'react-router-dom';

import {Items} from "../../types/itemhandling";

interface ClassesProps {

}
interface ClassesState {
    classes: Items.CharacterClass[];
}

class ClassesTableComponent extends Component<ClassesProps, ClassesState> {

    constructor(props: ClassesProps | Readonly<ClassesProps>) {
        super(props);
    }

    componentDidMount() {
        fetch('/api/content/classes')
            .then(response => response.json())
            .then(data => this.setState({classes: data}));
    }

    render() {
        console.log("classes:");
        console.log(this.state);

        if(!this.state) {
            return <div />
        }

        const classesList = this.state.classes!
                                .sort((a, b) => a.name > b.name ? 1 : -1)
                                .map(characterClass => {
            return <tr key={characterClass.id}>
                    <td>{characterClass.iconName}</td>
                    <td>{characterClass.id}</td>
                    <td>{characterClass.name}</td>
                    <td>{characterClass.description}</td>
                    <td>
                        <Button size="sm" color="primary" tag={Link}
                                to={"/content/classes/" + characterClass.id}
                                state={characterClass}>Details</Button>
                    </td>
                </tr>
        });

        return (
            <Container style={{marginRight: "250px"}}>
                <div>
                    <Container fluid>
                        <h3>Character classes</h3>
                        <Table className="mt-4">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Id</th>
                                    <th>Name</th>
                                    <th>Description</th>
                                    <th>Details</th>
                                </tr>
                            </thead>
                            <tbody>
                                {classesList}
                            </tbody>
                        </Table>
                    </Container>
                </div>
            </Container>
        );
    }
}
export default ClassesTableComponent;