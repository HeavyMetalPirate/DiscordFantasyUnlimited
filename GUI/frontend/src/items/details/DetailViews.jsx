import React, {useEffect, useState} from 'react';
import { Container, Table, Button } from 'reactstrap';
import { useParams, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

// https://overreacted.io/a-complete-guide-to-useeffect/

// Little helper to get the bonus text value
function foo(bonus) {
    // TODO right now it reads the enums directly
    // so do some i18n voodoo I guess
    if(bonus.combatSkill) {
        return bonus.combatSkill;
    }
    if(bonus.weaponType) {
        return bonus.weaponType;
    }
    if(bonus.attribute) {
        return bonus.attribute;
    }
}

export const SkillsDetailView = (props) => {
    // React Router Dom Hook
    const { id } = useParams();
    // React i18n Hook
    const { t, i18n } = useTranslation();

    const [skill, setSkill] = useState(null);

    useEffect(() => {
        // Define getSkill method as async
        const getSkill = async() => {
            // await the response from the server
            const res = await fetch('/api/content/skills/' + id);
            // await the json data in the response
            const data = await res.json();
            // set the state of the const 'skill'
            setSkill(data);
        }

        // call async method getSkill()
        getSkill();
    }, [id]); // add id to dependency array, that way useEffect is only triggered when id changes

    return (
        <Container>
            <h1>TODO {skill && skill.name}</h1>
        </Container>
    )
}

export const ClassesDetailView = (props) => {

    // React Router Dom Hook
    const { id } = useParams();
    // React i18n Hook
    const { t, i18n } = useTranslation();

    const [state, setState] = useState(null);
    const [skillsList, setSkillsList] = useState(null);
    const [bonusList, setBonusList] = useState(null);

    useEffect(() => {
        const getState = async() => {
            const res = await fetch('/api/content/classes/' + id);
            const data = await res.json();
            setState(data);

            setBonusList(data.bonuses
                .map(bonus  =>  {
                    return (
                        <tr key={bonus.id}>
                            <td>{foo(bonus)}</td>
                            <td>{bonus.modifier}</td>
                            <td>{bonus.name}</td>
                        </tr>
                    )
            }));
            setSkillsList(data.skillInstances
                .map(skill => {
                    return (
                        <tr key={skill.id}>
                            <td>{skill.iconName}</td>
                            <td>{skill.name}</td>
                            <td>{skill.minDamage} - {skill.maxDamage}</td>
                            <td>{skill.description}</td>
                            <td><Button size="sm" color="primary" tag={Link}
                                                                to={"/content/skills/" + skill.id}
                                                                state={skill}>Details</Button></td>
                        </tr>
                    )
             }));
        }
        getState();
    }, [id]);

    return (
        <Container>
            <h2>Information</h2>
                <Table className="classDetails detailsTable">
                  <tbody>
                      <tr>
                          <td rowSpan="2">{state && state.iconName}</td>
                          <td>Class:</td>
                          <td colSpan="2">{state && state.name}</td>
                      </tr>
                      <tr>
                          <td>Description:</td>
                          <td colSpan="2">{state && state.description}</td>
                      </tr>
                      <tr>
                          <td colSpan="4">{state && state.lore}</td>
                      </tr>
                      <tr>
                          <td rowSpan={state && state.bonuses.length + 1} style={{verticalAlign: "top"}}>Bonus</td>
                      </tr>
                      {bonusList}
                  </tbody>
              </Table>

              <h2>Attributes</h2>
              <Table className="classAttributes detailsTable">
                  <thead>
                      <tr>
                          <th>Attribute</th>
                          <th>Starting score</th>
                          <th>Autogrowth per level</th>
                      </tr>
                  </thead>
                  <tbody>
                      <tr>
                          <td>Endurance</td>
                          <td>{state && state.attributes.endurance}</td>
                          <td>+{state && state.attributes.enduranceGrowth}</td>
                      </tr>
                      <tr>
                          <td>Strength</td>
                          <td>{state && state.attributes.strength}</td>
                          <td>+{state && state.attributes.strengthGrowth}</td>
                      </tr>
                      <tr>
                          <td>Dexterity</td>
                          <td>{state && state.attributes.dexterity}</td>
                          <td>+{state && state.attributes.dexterityGrowth}</td>
                      </tr>
                      <tr>
                          <td>Wisdom</td>
                          <td>{state && state.attributes.wisdom}</td>
                          <td>+{state && state.attributes.wisdomGrowth}</td>
                      </tr>
                      <tr>
                          <td>Intelligence</td>
                          <td>{state && state.attributes.intelligence}</td>
                          <td>+{state && state.attributes.intelligenceGrowth}</td>
                      </tr>
                      <tr>
                          <td>Defense</td>
                          <td>{state && state.attributes.defense}</td>
                          <td>+{state && state.attributes.defenseGrowth}</td>
                      </tr>
                      <tr>
                          <td>Luck</td>
                          <td>{state && state.attributes.luck}</td>
                          <td>+{state && state.attributes.luckGrowth}</td>
                      </tr>
                  </tbody>
              </Table>

              <h2>Skills</h2>

              <Table className="classSkills detailsTable">
                  <thead>
                      <tr>
                          <th />
                          <th>Name</th>
                          <th>Base damage</th>
                          <th>Description</th>
                          <th />
                      </tr>
                  </thead>
                  <tbody>
                      {state && skillsList}
                  </tbody>
              </Table>

              <h2>Starting equipment</h2>
              TODO Table
        </Container>
    );
}