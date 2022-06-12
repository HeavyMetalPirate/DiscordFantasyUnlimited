import React, {useEffect, useState} from 'react';
import {
    Container,
    Card
} from 'react-bootstrap'
import {useParams} from "react-router-dom";
import {REST} from "../types/rest-entities";
import {
    Button,
    CardBody,
    CardFooter, CardGroup,
    CardHeader,
    CardImg,
    CardText
} from "reactstrap";

import './BattlePanel.css';
import {
    calculateHealthPercentage,
    calculateResourcePercentage,
    get_steps
} from "./utils/StatusbarUtils";
import {useTrackedState} from "../SessionStore";
import {
    Accordion,
    AccordionDetails,
    AccordionSummary, Avatar, ImageList, ImageListItem, ImageListItemBar,
    Typography
} from "@mui/material";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {useStompClient} from "../WebsocketClient";
import {IMessage} from "@stomp/stompjs";
import {ItemDetailView} from "./ItemManagement";
import {DropableItem, InventoryItem} from "./utils/ItemTypes";

interface BattlePanelProperties extends TranslationAsProperty {

}
export const BattleMainPanel = ({translation}: BattlePanelProperties) => {
    const { id } = useParams();
    const t = translation;

    const [state, setState] = useTrackedState();
    const client = useStompClient();

    const [battleInfo, setBattleInfo] = useState<REST.BattleDetailInfo>();
    const [target, setTarget] = useState<REST.BattleParticipantDetails>();
    const [skill, setSkill] = useState<REST.BattleSkill>();
    const [consumable, setConsumable] = useState<REST.InventoryItem>();
    const [validAction, setValidAction] = useState<boolean>(false);
    const [startButtonText, setStartButtonText] = useState<string>(t('battle.button.start.no.selection', {ns:'battle'}));

    useEffect(() => {
        if(id === null || id === undefined) {
            return;
        }
        const getBattleInfo = async() => {
            const response = await fetch('/api/game/battle/' + id);
            const data = await response.json();
            return data;
        };

        getBattleInfo()
            .then(data => setBattleInfo(data));
        client.subscribe('/topic/battle/' + id, onMessageReceived);

    }, [id]);

    useEffect(() => {
        checkValidAction();
    }, [battleInfo]);

    useEffect(() => {
        checkValidAction();
    }, [consumable, target, skill]);

    const onMessageReceived = (msg: IMessage) => {
        let battleUpdate: REST.BattleUpdate = JSON.parse(msg.body);
        if(battleUpdate.hasUpdate && battleInfo !== undefined) {
            // only update battleLog, participants and active flag
            setBattleInfo({
                id: battleInfo.id,
                location: battleInfo.location,
                playerDetails: battleInfo.playerDetails,
                battleLog: battleUpdate.battleInfo.battleLog,
                hostiles: battleUpdate.battleInfo.hostiles,
                players: battleUpdate.battleInfo.players,
                active: battleUpdate.battleInfo.active,
                summary: battleUpdate.battleInfo.summary
            });
        }
    }

    if(battleInfo === null || battleInfo === undefined) {
        return <span>{t('battle.not.found', {ns: 'battle'})}: {id}</span>
    }

    let playerCards : JSX.Element[] = battleInfo.players
        .map(player => {
            if(player.id === target?.id) {
                return <PlayerCard className={"target"}
                            participant={player}
                            onTargetSelect={onTargetSelect}
                            translation={t} />;
            }

            return <PlayerCard participant={player}
                        onTargetSelect={onTargetSelect}
                        translation={t} />;

        });
    let hostileCards: JSX.Element[] = battleInfo.hostiles
        .map(hostile => {
                if(hostile.id === target?.id) {
                    return <HostileCard participant={hostile}
                                     className={"target"}
                                     onTargetSelect={onTargetSelect}
                                     translation={t} />;
                }
                return <HostileCard participant={hostile}
                                 onTargetSelect={onTargetSelect}
                                 translation={t} />;
            }
        );

    function onActionSelect(event: React.MouseEvent, action: REST.BattleSkill | REST.InventoryItem) {
        event.preventDefault();
        if("rank" in action) {
            let skill: REST.BattleSkill = action;
            setSkill(skill);
            setConsumable(undefined);
        }
        else {
            let consumable: REST.InventoryItem = action;
            setConsumable(consumable);
            setTarget(undefined);
            setSkill(undefined);
        }
    }

    function onTargetSelect(event: React.MouseEvent, target: REST.BattleParticipantDetails) {
        event.preventDefault();
        setTarget(target);
        setConsumable(undefined);
    }

    function checkValidAction() {

        // TODO check if battle actions has any not executed actions by executing
        // and disable button with message "waiting" or something if that's so
        for(const key in battleInfo?.battleLog.rounds) {
            const roundBattleLog = battleInfo?.battleLog.rounds[key];
            if(roundBattleLog?.find(item => item.executed === false && item.executing?.id === state.characterData!.id)) {
                setValidAction(false);
                setStartButtonText(t('battle.button.start.already.entered', {ns:'battle'}));
                return;
            }
        }

        if(skill === undefined
            && target === undefined
            && consumable === undefined) {
            setValidAction(false);
            setStartButtonText(t('battle.button.start.no.selection', {ns:'battle'}));
            return;
        }

        if(consumable !== undefined) {
            // selection valid for consumables
            setValidAction(true);
            setStartButtonText(t('battle.button.start.valid', {ns:'battle'}));
        }
        else {
            // skill; check if target type is correct
            let targetType = skill?.targetType;
            // Sanitize targetType
            if(targetType === null) {
                targetType = 'ENEMY';
            }

            if(targetType === 'AREA') {
                setValidAction(true);
                setStartButtonText(t('battle.button.start.valid', {ns:'battle'}));
            }
            else if(targetType === 'ENEMY') {
                let foundHostile = battleInfo?.hostiles.find(hostile => hostile.id === target?.id);
                if(foundHostile === undefined) {
                    setValidAction(false);
                    setStartButtonText(t('battle.button.start.invalid', {ns:'battle'}));
                }
                else {
                    setValidAction(true);
                    setStartButtonText(t('battle.button.start.valid', {ns:'battle'}));
                }
            }
            else if(targetType === 'FRIEND') {
                let foundPlayer = battleInfo?.players.find(player => player.id === target?.id && player.details.id !== battleInfo?.playerDetails.id);
                if(foundPlayer === undefined) {
                    setValidAction(false);
                    setStartButtonText(t('battle.button.start.invalid', {ns:'battle'}));
                }
                else {
                    setValidAction(true);
                    setStartButtonText(t('battle.button.start.valid', {ns:'battle'}));
                }
            }
            else if(targetType === 'OWN') {
                if(target?.details.id !== battleInfo?.playerDetails.id) {
                    setValidAction(false);
                    setStartButtonText(t('battle.button.start.invalid', {ns:'battle'}));
                }
                else {
                    setValidAction(true);
                    setStartButtonText(t('battle.button.start.valid', {ns:'battle'}));
                }
            }
        }
    }

    function performAction() {
        const addBattleAction = async() => {
            const playerCharacter = battleInfo?.players.find(player => player.details.id === battleInfo?.playerDetails.id)

            let actionType: REST.BattleActionType;
            if(skill !== undefined) {
                actionType = 'SKILL';
            }
            else if(consumable !== undefined) {
                actionType = 'CONSUMABLE';
            }
            else {
                // TODO Pass and Flee action buttons
                actionType = 'PASS';
            }

            let consumableId: string = "";
            if(consumable !== undefined && consumable?.item !== undefined) {
                consumableId = consumable.item.id;
            }

            const requestBody: REST.BattleParticipantAction = {
                actionType: actionType,
                executing: playerCharacter!,
                target: target!,
                usedSkill: skill!,
                usedConsumable: consumableId
            };
            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json' ,
                    'X-CSRF-TOKEN' : state.token!.token
                },
                body: JSON.stringify(requestBody)
            };

            return await fetch('/api/battle/' + battleInfo?.id + '/action', requestOptions);
        };

        addBattleAction()
            .then(response => {
                // Reset state
                setSkill(undefined);
                setTarget(undefined);
                setConsumable(undefined);
                setValidAction(false);
                setStartButtonText(t('battle.button.start.no.selection', {ns:'battle'}));

                switch(response.status) {
                    case 404:
                        // Battle not found
                        break;
                    case 405:
                        // Player not participating in battle
                        break;
                    case 400:
                        // any combination of action type, target, skill and consumable is not allowed
                        break;
                    case 200:
                        // Everything seems to be okay here. Set battle info.
                        response.json()
                            .then(json => setBattleInfo(json));
                        break;
                    default:
                        // unknown error, log
                        console.log("POST /api/battle/{id}/action: unexpected status " + response.status);
                }
            });
    }

    return (
        <Container fluid>
            <header className={"location-banner"}>
                <img src={battleInfo.location.banner} className="location-banner-image" alt={t(battleInfo.location.name, {ns:'location'})} />
            </header>
            <span>Battle Panel TODO {id}</span>
            <div className={"battle-container"}>
                <div>{playerCards}</div>
                <div>{hostileCards}</div>
            </div>
            <Toolbar skills={battleInfo.playerDetails.toolbarSkills}
                     consumables={battleInfo.playerDetails.consumables}
                     participation={battleInfo.playerDetails.participation}
                     translation={t}
                     onActionSelect={onActionSelect}/>
            <Button disabled={!validAction}
                    onClick={performAction}
                    style={{width: "80%", marginTop: "1em"}}>
                {startButtonText}
            </Button>
            <BattleResult result={battleInfo.summary} translation={t} />
            <BattleLog battleLog={battleInfo.battleLog} translation={t} />
        </Container>
    )
}

interface BattleResultProps extends TranslationAsProperty {
    result: REST.BattleResultSummary;
}

const BattleResult: React.FC<BattleResultProps> = (props) => {
    const t =  props.translation;
    const result = props.result;

    return (
        <Container>
            <div>BattleResult TODO</div>
            <div>{result.winningSide}</div>
            <div>{result.lootSummaryList.map(loot => {
                return <div>{loot.experience}</div>;
            })}</div>
        </Container>
    )
}

interface BattleLogProps extends TranslationAsProperty {
    battleLog: REST.BattleLog;
}

const BattleLog: React.FC<BattleLogProps> = (props) => {
    const t =  props.translation;
    const battleLog = props.battleLog;

    if(battleLog === undefined || battleLog === null) {
        return <div>{t('battle.log.empty', {ns: 'battle'})}</div>
    }

    let rounds: JSX.Element[] = [];
    for(const key in battleLog.rounds) {
        const roundBattleLog = battleLog.rounds[key];
        let roundElements: JSX.Element[];
        roundElements = roundBattleLog
            .sort((e1,e2) => e1.ordinal > e2.ordinal ? 1 : -1)
            .map(entry => {
                if(entry.usedSkill !== null) {
                    return (
                        <CardGroup>
                            <Card>
                                <CardBody>
                                    <CardText>{entry.executing.name}</CardText>
                                </CardBody>
                            </Card>
                            <Card>
                                <CardBody>
                                    <CardText>{entry.usedSkill.name} for {entry.amount}</CardText>
                                    <CardText>{entry.outcome}</CardText>
                                </CardBody>
                            </Card>
                            <Card>
                                <CardBody>
                                    <CardText>{entry.target.name}</CardText>
                                </CardBody>
                            </Card>
                        </CardGroup>
                    )
                }
                return (
                    <CardGroup>
                        <Card>
                            <CardBody>
                                <CardText>{entry.executing.name}</CardText>
                            </CardBody>
                        </Card>
                        <Card>
                            <CardBody>
                                <CardText>Used {entry.usedConsumable.name}</CardText>
                                <CardText>{entry.outcome}</CardText>
                            </CardBody>
                        </Card>
                        <Card>
                            <CardBody>
                                <CardText>-</CardText>
                            </CardBody>
                        </Card>
                    </CardGroup>
                )
        });

        rounds.push(
            <Accordion defaultExpanded={true}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}
                                            aria-controls="panel1a-content"
                                            id="panel1a-header">
                    <Typography>Round {key}</Typography>
                </AccordionSummary>
                <AccordionDetails>
                    {roundElements}
                </AccordionDetails>
            </Accordion>
        );
    }
    rounds = rounds.reverse();

    return(
        <Container>
            <div>BattleLog TODO</div>
            {rounds}
        </Container>
    )
}


interface ToolbarProps extends TranslationAsProperty {
    skills: REST.BattleSkill[];
    consumables: REST.ToolbarConsumableItem[];
    participation: boolean;
    onActionSelect(event: React.MouseEvent, action: REST.BattleSkill | REST.InventoryItem): void;
}

const Toolbar = ({skills, consumables, participation, translation, onActionSelect}: ToolbarProps) => {
    const t = translation;
    const [selectedSkill, setSelectedSkill] = useState<REST.BattleSkill>();
    const [selectedConsumable, setSelectedConsumable] = useState<InventoryItem>();
    const [hoveredSkill, setHoveredSkill] = useState<REST.BattleSkill>();
    const [hoveredConsumable, setHoveredConsumable] = useState<InventoryItem | null>(null);
    const [x, setX] = useState(0);
    const [y, setY] = useState(0);

    if(participation === false) {
        return (
            <div>TODO No-Participation Frame</div>
        )
    }

    function selectSkill(event: React.MouseEvent, skill: REST.BattleSkill) {
        setSelectedConsumable(undefined);
        if(skill.id === 'empty') {
            setSelectedSkill(undefined);
            return;
        }
        setSelectedSkill(skill);
        onActionSelect(event, skill);
    }

    function selectConsumable(event: React.MouseEvent, consumable: InventoryItem) {
        setSelectedSkill(undefined);
        if(consumable.type === 'empty') {
            setSelectedConsumable(undefined);
            return;
        }
        setSelectedConsumable(consumable);
        onActionSelect(event, consumable);
    }

    function showSkillTooltip(event: React.MouseEvent, skill: REST.BattleSkill) {
        event.preventDefault();
        setHoveredSkill(skill);
        setX(event.clientX);
        setY(event.clientY);
    }
    function showConsumableTooltip(event: React.MouseEvent, consumable: InventoryItem) {
        event.preventDefault();
        setHoveredConsumable(consumable);
        setX(event.clientX);
        setY(event.clientY);
    }
    function hideTooltip(event: React.MouseEvent) {
        event.preventDefault();
        setHoveredSkill(undefined);
        setHoveredConsumable(null);
    }

    let skillCards : JSX.Element[] = skills.map(skill => {
        let className = skill.id === selectedSkill?.id
                            ? "toolbar-skill-card select"
                            : "toolbar-skill-card";

        return (
            <Card className={className}
                onMouseMove={e => showSkillTooltip(e, skill)}
                onMouseLeave={hideTooltip}
                onClick={e => selectSkill(e, skill)}>
                <CardBody>
                    <CardImg className={"toolbar-skill-icon"}
                             center={true}
                             src={skill.iconName} />
                    {
                        skill.name !== 'empty' &&
                            <CardText tag="p" className="toolbar-icon-text">{skill.rank}</CardText>
                    }
                </CardBody>
            </Card>
        )
    });

    let consumableItems: InventoryItem[] = consumables.map(consumable => {
        let item: InventoryItem = {
            type: 'consumable',
            count: consumable.count,
            item: consumable.consumable
        }
        return item;
    });

    let consumablesCards : JSX.Element[] = consumableItems.map(consumable => {
        let className = consumable.item.id === selectedConsumable?.item.id
                            ? "toolbar-consumable-card select"
                            : "toolbar-consumable-card";

        return (
            <Card className={className}
                  onMouseOver={e => showConsumableTooltip(e, consumable)}
                  onMouseLeave={hideTooltip}
                  onClick={e => selectConsumable(e, consumable)}>
                <CardBody>
                    <CardImg className={"toolbar-consumable-icon"}
                             src={consumable.item.iconName} />
                    <CardText tag="p" className="toolbar-icon-text">x{consumable.count}</CardText>
                </CardBody>
            </Card>
        )
    });

    return (
        <Container className={"toolbar-container"}>
            <div className={"toolbar-skills-container"}>{skillCards}</div>
            <div className={"toolbar-consumables-container"}>{consumablesCards}</div>
            <SkillDetailView skill={hoveredSkill} x={x} y={y} translation={t} />
            <ItemDetailView item={hoveredConsumable} visible={'visible'} x={x} y={y} translation={t} />
        </Container>
    )
}

interface SkillDetailProperties extends  TranslationAsProperty {
    skill: REST.BattleSkill | null | undefined;
    x: number;
    y: number;
}

const SkillDetailView = ({translation, skill, x, y}: SkillDetailProperties) => {
    const t = translation;

    if(skill === null || skill === undefined || skill.id === 'empty') {
        return null;
    }

    let damage: JSX.Element | null;
    switch(skill.targetType) {
        case "FRIEND":
        case "OWN":
            damage = <span className="card-header-text">{t('skills.healing', {ns: 'skills'})}: {skill.minDamage} - {skill.maxDamage}</span>
            break;
        case "AREA":
        case "ENEMY":
        default:
            damage = <span className="card-header-text">{t('skills.damage', {ns: 'skills'})}: {skill.minDamage} - {skill.maxDamage}</span>
            break;
    }
    let cost: JSX.Element = <span className="card-header-text">{t('skills.cost', {ns: 'skills'})}: {skill.cost}</span>

    let statusApplied: JSX.Element | null = null;
    if(skill.incapacitates) {
        statusApplied = <span className="card-header-text">{t('skills.status.incapacitate', {ns: 'skills'})}: {skill.durationInTurns} {t('skills.duration.rounds', {ns: 'skills'})} </span>
    }
    else {
        // TODO when status handling has been done
    }

    let preparation: JSX.Element | null = null;
    if(skill.preparationRounds > 0) {
        preparation = <span className="card-header-text">{t('skills.preparation', {ns: 'skills'})}: {skill.preparationRounds} {t('skills.duration.rounds', {ns: 'skills'})} </span>
    }

    let skillType: JSX.Element = <span className="card-header-text">{t('skills.type.' + skill.skillType, {ns: 'skills'})}</span>
    let targetType: JSX.Element = <span className="card-header-text">{t('skills.target', {ns: 'skills'})}: {t('skills.target.' + skill.targetType, {ns: 'skills'})}</span>

    let attributeModifier: JSX.Element | null = <span className="card-header-text">{t('skills.modifiers.attribute', {ns: 'skills'})}: {t('character.attributes.' + skill.attribute, {ns: 'character'})}</span>;

    let weaponModifier:JSX.Element | null = null;
    if(skill.weaponModifier !== null && skill.weaponModifier !== 'NONE') {
        weaponModifier = <span className="card-header-text">{t('skills.modifiers.weapon.' + skill.weaponModifier, {ns: 'skills'})}</span>
    }

    return (
        <div className="skill-detail-view" style={{visibility: 'visible', left: x, top: y}}>
            <Card>
                <CardHeader>
                    <div className="card-header-text">
                        <span className="card-header-text">{t(skill.name, {ns: 'skills'})}</span>
                        <span className="card-header-text">{t('skills.rank', {ns: 'skills'})}: {skill.rank}</span>
                    </div>
                </CardHeader>
                <CardBody style={{textAlign: "left"}}>
                    <div className="card-body-text">
                        {damage}
                        {cost}
                        {preparation}
                        {statusApplied}
                        <span className="card-header-text" />
                        {skillType}
                        {targetType}
                        <span className="card-header-text" />
                        {attributeModifier}
                        {weaponModifier}
                    </div>
                </CardBody>
                <CardFooter style={{textAlign: "left"}}>
                    <span className="card-footer-text">{t(skill.description, {ns: 'skills'})}</span>

                </CardFooter>
            </Card>
        </div>
    )
}

interface ParticipantCard extends TranslationAsProperty {
    participant: REST.BattleParticipantDetails;
    className?: string;
    onTargetSelect(event: React.MouseEvent, target: REST.BattleParticipantDetails): void;
}
const PlayerCard = ({translation, participant, className, onTargetSelect}: ParticipantCard) => {
    const t = translation;

    console.log("Status:" , participant.statusEffects);

    let statusPanel: JSX.Element[] = participant.statusEffects.map(status => {
        return (
            <ImageListItem className={"status-icon status-type-" + status.statusType.toLowerCase()}
                           key={status.name}>
                <img src={status.iconName} alt={status.name} />
                <div className="player-status-rounds">{status.roundsRemaining}</div>
            </ImageListItem>
        )
    });

    return (
        <div onClick={(e) => onTargetSelect(e, participant)}>
            <Card className={className ? "player-card " + className : "player-card"}>
                <CardHeader>
                    <CardImg left className={"icon-character icon-character-race"}
                             src={participant.details.race.icon}
                             title={t(participant.details.race.name, {ns:'race'})}/>
                    <CardImg left className={"icon-character icon-character-class"}
                             src={participant.details.characterClass.icon}
                             title={t(participant.details.characterClass.name, {ns:'characterClass'})}/>
                    <div className="card-header-text">
                        <span className="card-header-text">{participant.details.name}</span>
                        <span className="card-header-text">{t('battle.stats.level', {ns:'battle'})}:{participant.details.level}</span>
                    </div>
                    <ImageList className="player-status-icons" >
                        {statusPanel}
                    </ImageList>
                </CardHeader>
                <CardBody>
                    <HealthBar id={participant.id} details={participant.details} statusEffects={participant.statusEffects} />
                    <ManaBar id={participant.id} details={participant.details} statusEffects={participant.statusEffects} />
                </CardBody>
            </Card>
        </div>
    )
}
const HostileCard = ({translation, participant, className, onTargetSelect}: ParticipantCard) => {
    const t = translation;

    let statusPanel: JSX.Element[] = participant.statusEffects.map(status => {
        return (
            <ImageListItem className={"status-icon status-type-" + status.statusType.toLowerCase()}
                           key={status.name}>
                <img src={status.iconName} alt={status.name} />
                <div className="hostile-status-rounds">{status.roundsRemaining}</div>
            </ImageListItem>
        )
    });

    return (
        <div onClick={(e) => onTargetSelect(e, participant)}>
            <Card className={className ? "hostile-card " + className : "hostile-card"}>
                <CardHeader>
                    <CardImg right className={"icon-character icon-character-race"}
                             src={participant.details.race.icon}
                             title={t(participant.details.race.name, {ns:'race'})} />
                    <CardImg right className={"icon-character icon-character-class"}
                             src={participant.details.characterClass.icon}
                             title={t(participant.details.characterClass.name, {ns:'characterClass'})} />
                    <div className="card-header-text">
                        <span className="card-header-text">{participant.details.name}</span>
                        <span className="card-header-text">{t('battle.stats.level', {ns:'battle'})}:{participant.details.level}</span>
                    </div>

                    <ImageList className="hostile-status-icons" >
                        {statusPanel}
                    </ImageList>
                </CardHeader>
                <CardBody>
                    <HealthBar id={participant.id} details={participant.details} statusEffects={participant.statusEffects} />
                    <ManaBar id={participant.id} details={participant.details} statusEffects={participant.statusEffects} />
                </CardBody>
            </Card>
        </div>
    )
}

const ManaBar = ({details}: REST.BattleParticipantDetails) => {
    let character = details;

    let steps;
    if(character.resources.energyType === 'RAGE') {
        // Red
        steps = get_steps('rgb(155, 0, 0)', 'rgb(255, 0, 0)' , 100);
    }
    else if(character.resources.energyType === 'FOCUS') {
        // Yellow
        steps = get_steps('rgb(155, 155, 0)', 'rgb(255, 255, 0)' , 100);
    }
    else {
        // Mana, blue
        steps = get_steps('rgb(0, 0, 155)', 'rgb(0, 0, 255)' , 100);
    }
    const p = calculateResourcePercentage(character);
    return (
        <div className="player">
            <div className="mana">
                <div className="bar" style= {{ width: p + '%', background: Object.keys(steps)[Math.floor(p)] }}></div>
                <span className="stat">
                    <span className="left">{ character.resources.currentResource }</span>
                    <span className="right">{ character.resources.maxResource }</span>
                </span>
            </div>
        </div>
    )
}

const HealthBar = ({details}: REST.BattleParticipantDetails) => {
    let character = details;

    const steps = get_steps('rgb(255, 0, 0)', 'rgb(0, 128, 0)' , 100);
    const p = calculateHealthPercentage(character);

    return (
        <div className="player">
            <div className="health">
                <div className="bar" style= {{ width: p + '%', background: Object.keys(steps)[Math.floor(p)] }}></div>
                <span className="stat">
                    <span className="left">{ character.resources.currentHealth }</span>
                    <span className="right">{ character.resources.maxHealth }</span>
                </span>
            </div>
        </div>
    )
}