import React, { Component } from 'react';
import { Container, Table, Button } from 'reactstrap';
import { Link } from 'react-router-dom'

class ClassesTableComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {classes: []};
    }

    componentDidMount() {
        fetch('/api/content/classes')
            .then(response => response.json())
            .then(data => this.setState({classes: data}));
    }

    render() {
        const {classes} = this.state;

        const classesList = classes
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
            <Container>
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