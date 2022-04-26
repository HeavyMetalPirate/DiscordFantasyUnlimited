import './App.css';
import React, { Component } from 'react';
import {
       BrowserRouter as Router,
       Routes,
       Route
} from "react-router-dom";

import Home from './Home'
import AppNavbar from './items/AppNavBar';
import ClassesTableComponent from './items/table/ClassesTableComponent.js'
import { ClassesDetailView, SkillsDetailView } from './items/details/DetailViews.js'

class App extends Component {
    render() {
        return (
            <div id="baseContainer">
                <Router>
                    <AppNavbar/>
                    <main id="page-wrap">
                        <Routes>
                            <Route exact path='/' element=<Home /> />
                            <Route exact path='/content/classes' element=<ClassesTableComponent /> />
                            <Route path='/content/classes/:id' element=<ClassesDetailView /> />
                            <Route path='/content/skills/:id' element=<SkillsDetailView /> />
                        </Routes>
                    </main>
                </Router>
            </div>
        );
    }
}
export default App;