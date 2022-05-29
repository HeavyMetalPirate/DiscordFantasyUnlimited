import React, {ChangeEvent, MouseEventHandler, useEffect, useState} from 'react';
import {
    Button,
    Card,
    CardBody,
    CardTitle,
    CardText,
    CardImg,
    CardGroup,
    CardHeader,
    CardFooter,
    Container,
    InputGroup,
    Input,
    Label,
    Table
} from 'reactstrap';
import {
    ControlledMenu,
    MenuItem,
    MenuHeader,
    useMenuState, ClickEvent, MenuCloseEvent, SubMenu
} from '@szhsin/react-menu';
import '@szhsin/react-menu/dist/index.css';
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';
import { useTrackedState } from '../SessionStore';
import './ItemManagement.css'

import {
    ItemDetailViewProperties,
    DropableItem,
    SimpleDialogProps,
    InventoryListProperties,
    InventoryItem,
    InventoryType,
    ContextMenuItemSelection
} from './utils/ItemTypes';

import { Items } from '../types/itemhandling';
import { REST } from "../types/rest-entities";
import {TFunction} from "react-i18next";

function itemTypeSort(a:InventoryItem, b:InventoryItem) {
    return a.type > b.type ? 1 : -1;
}
function itemQuantitySort(a: InventoryItem, b:InventoryItem) {
    return a.count > b.count ? -1 : 1;
}
function itemValueSort(a:InventoryItem, b:InventoryItem) {
    return a.item.value > b.item.value ? -1 : 1;
}
function itemNameSort(a:InventoryItem, b: InventoryItem, t: TFunction) {
    const aTranslatedName = t(a.item.name, {ns:'items'});
    const bTranslatedName = t(b.item.name, {ns:'items'});
    return aTranslatedName > bTranslatedName ? 1 : -1;
}
function itemRaritySort(a: InventoryItem, b: InventoryItem) {
    if(a.item.rarity === b.item.rarity) {
        // Both same rarity: use value
        return a.item.value > b.item.value ? -1 : 1;
    }
    // Artifacts
    if(a.item.rarity === 'ARTIFACT') {
        return -1;
    }
    if(b.item.rarity === 'ARTIFACT') {
        return 1;
    }
    // Legendary
    if(a.item.rarity === 'LEGENDARY') {
        return -1;
    }
    if(b.item.rarity === 'LEGENDARY') {
        return 1;
    }
    // Epic
    if(a.item.rarity === 'EPIC') {
        return -1;
    }
    if(b.item.rarity === 'EPIC') {
        return 1;
    }
    // Rare
    if(a.item.rarity === 'RARE') {
        return -1;
    }
    if(b.item.rarity === 'RARE') {
        return 1;
    }
    // Uncommon
    if(a.item.rarity === 'UNCOMMON') {
        return -1;
    }
    if(b.item.rarity === 'UNCOMMON') {
        return 1;
    }
    // Common: value again but should be dead code really...
    return a.item.value > b.item.value ? -1 : 1;
}

export const EquipmentManager = ({translation}: TranslationAsProperty) => {
    const t = translation;
    const [state, setState] = useTrackedState();
    const [inventory, setInventory] = useState<InventoryItem[] | null>(null);
    const [equipmentStats, setEquipmentStats] = useState<REST.PlayerEquipmentDetails | null>(null);
    const [reload, setReload] = useState(0);
    const [searchField, setSearchField] = useState("");
    const [sortMode, setSortMode] = useState("");

    const [selectedItem, setSelectedItem] = useState<InventoryItem | null>(null);
    const [detailsVisible, setDetailsVisible] = useState("");
    const [x, setX] = useState(0);
    const [y, setY] = useState(0);

    useEffect(() => {
        const getInventory = async() => {
            const resp = await fetch('/api/game/inventory');
            const data = await resp.json();

            const inventoryData: InventoryType = data;
            const inventoryItems: InventoryItem[] = inventoryData.items;

            const gearInventoryItems: InventoryItem[] = inventoryItems
                .filter((item) => {
                   if(item.type === 'weapon') return true;
                   if(item.type === 'equipment') return true;
                   return false;
                })
                .sort((a,b) => itemRaritySort(a,b));

            setInventory(gearInventoryItems);
        };
        const getCharacterStats = async() => {
            const resp = await fetch('/api/game/player/equipment');
            const data = await resp.json();
            setEquipmentStats(data);
        };

        getInventory();
        getCharacterStats();

    }, [reload]);

    function reloadInventory() {
        if(!reload) {
            setReload(1);
        }
        else {
            setReload(reload + 1);
        }
    }

    const handleFilterChange = (e: ChangeEvent<HTMLInputElement>) => {
        setSearchField(e.target.value);
    };

    const handleSortModeChange = (e: ChangeEvent<HTMLInputElement>) => {
        setSortMode(e.target.value);
    }

    let filteredItems: InventoryItem[] | null = null;
    if(inventory) {
        filteredItems = inventory.filter(
            entry => {
                if(entry.count === 0) {
                    return false;
                }
                const translatedName = t(entry.item.name, {ns:'items'});
                return (
                    translatedName.toLowerCase().includes(searchField.toLowerCase())
                );
            }
        );
    }
    if(filteredItems) {
        filteredItems.sort((a, b) => {
            switch(sortMode) {
                case 'type':
                    return itemTypeSort(a,b);
                case 'name':
                    return itemNameSort(a,b,t);
                case 'value':
                    return itemValueSort(a,b);
                case 'quantity':
                    return itemQuantitySort(a,b);
                case 'rarity':
                default:
                    return itemRaritySort(a,b);
            }
        });
    }

    let characterStatsBlock, equipmentBlock;
    if(!equipmentStats || !equipmentStats.stats) {
        characterStatsBlock = <div />
    }
    else {
        characterStatsBlock = (
            <CharacterStatsBlock
                stats={equipmentStats.stats}
                secondarySkills={equipmentStats.secondaryStats}
                combatSkills={equipmentStats.combatSkills}
                t={t}
            />
        )
    }

    if(!equipmentStats || !equipmentStats.equipment) {
        equipmentBlock = <div />
    }
    else {
        equipmentBlock = <CurrentCharacterEquipment onRefresh={reloadInventory} t={t} equipment={equipmentStats.equipment} />
    }

    function mouseOverItem(event: React.MouseEvent<HTMLElement>) {
        event.preventDefault();
        setDetailsVisible('hidden');

        let itemId : string | null = document.elementsFromPoint(event.clientX, event.clientY)
            .filter(element => element.getAttribute('foo') != null)
            .at(0)!
            .getAttribute('foo');

        let selectedInventoryItems: InventoryItem[] | undefined
            = inventory?.filter(item => item.item.id === itemId);

        if(!selectedInventoryItems || selectedInventoryItems.length === 0) {
            setSelectedItem(null);
            setDetailsVisible('hidden');
            return;
        }
        else {
            setSelectedItem(selectedInventoryItems[0]);
        }
        setX(event.clientX);
        setY(event.clientY);
        setDetailsVisible('visible');
    }
    function mouseLeftItem(event: React.SyntheticEvent) {
        event.preventDefault();
        setSelectedItem(null);
        setDetailsVisible('hidden');
    }

    return (
        <Container className={"equipment-manager"}>
            <div className={"equipment-manager-column equipment"}
                onMouseOver={mouseOverItem}
                onMouseLeave={mouseLeftItem}>
                {equipmentBlock}
            </div>
            <div className={"equipment-manager-column inventory"}>
                <InputGroup>
                    <Input name="inventoryFilter" onChange={handleFilterChange} style={{width: "16em"}} placeholder={t('items.manager.filter.text', {ns: 'items'})} />
                    <Input name="inventorySortMode" onChange={handleSortModeChange} style={{minWidth: "8em"}} type="select">
                        <option value="rarity">{t('items.manager.sort.rarity', {ns: 'items'})}</option>
                        <option value="type">{t('items.manager.sort.type', {ns: 'items'})}</option>
                        <option value="name">{t('items.manager.sort.name', {ns: 'items'})}</option>
                        <option value="value">{t('items.manager.sort.value', {ns: 'items'})}</option>
                        <option value="quantity">{t('items.manager.sort.quantity', {ns: 'items'})}</option>
                    </Input>
                </InputGroup>
                <InventoryList inventory={filteredItems} translation={t} onReload={reloadInventory} />
            </div>
            <div className={"equipment-manager-column stats"}>
                {characterStatsBlock}
            </div>
            <ItemDetailView translation={t} visible={detailsVisible} item={selectedItem} x={x} y={y} />
        </Container>
    )
}


interface EquipmentProps {
    equipment: REST.PlayerEquipment;
    t: TFunction<"translation", undefined>;
    onRefresh(): void;
}

const CurrentCharacterEquipment = ({equipment, t, onRefresh}: EquipmentProps) => {
    if(!equipment) {
        return <div/>
    }

    return (
        <div className={"item-equipment-slot"}>
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.helmet} slot={"HELMET"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.chest} slot={"CHEST"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.gloves} slot={"GLOVES"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.pants} slot={"PANTS"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.boots} slot={"BOOTS"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.ring1} slot={"RING1"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.ring2} slot={"RING2"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.neck} slot={"NECK"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.mainhand} slot={"MAINHAND"} />
            <EquipmentSlotItem onReload={onRefresh} t={t} equipment={equipment.offhand} slot={"OFFHAND"} />
        </div>
    )
}

interface EquipmentSlotProps {
    equipment: REST.Equipment | REST.Weapon | null | undefined;
    slot: REST.EquipmentSlot;
    t: TFunction<"translation", undefined>;
    onReload(): void;
}

const EquipmentSlotItem = ({equipment, slot, t, onReload}: EquipmentSlotProps) => {
    const [userState, setUserState] = useTrackedState();

    let emptySlot: JSX.Element = (
        <div>
            <Card>
                <CardHeader>
                    <CardImg className={"item-icon"} left src="../images/emptySlotIcon.png" />
                    <div className="card-header-text">
                        <span className="card-header-text">{t('items.equipment.slot.' + slot, {ns: 'items'})}</span>
                        <span className="card-header-text">{t('items.equipment.slot.empty', {ns: 'items'})}</span>
                    </div>
                </CardHeader>
            </Card>
        </div>
    );

    function unequipItem() {
        const unequipRequestBody: REST.UnequipRequest = {
            slot: slot
        };

        // perform sync drop item call to REST
        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : userState.token!.token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(unequipRequestBody)
        };

        fetch('/api/game/character/unequip', requestOptions)
            .then((response) => {
                if(response.status === 200) {
                    onReload();
                    setUserState((prevState) => ({...prevState, characterEquipmentChange: userState.characterEquipmentChange + 1, globalErrorMessage: null}));
                }
                else {
                    setUserState((prevState) => ({...prevState,
                        globalErrorMessage: {
                            header: t('items.unequip.error.header', {ns:'items'}),
                            body: t('items.unequip.error.generic', {ns: 'items'})
                        }
                    }));
                }
            });
    }

    if(!equipment) {
        return emptySlot;
    }

    return (
        <div>
            <Card foo={equipment.id}>
                <CardHeader>
                    <CardImg className={"item-icon " + equipment.rarity.toLowerCase()} left src={equipment.iconName} />
                    <div className="card-header-text">
                        <span className="card-header-text">{t('items.equipment.slot.' + slot, {ns: 'items'})}</span>
                        <span className="card-header-text">{t(equipment.name, {ns: 'items'})}</span>
                    </div>
                </CardHeader>
                <Button onClick={unequipItem} className={"item-unequip"}>{t('items.equipment.slot.button.unequip', {ns:'items'})}</Button>
            </Card>
        </div>
    );
}

interface CharacterStatsProps {
    stats: REST.PlayerStats;
    secondarySkills: REST.PlayerSecondaryStats;
    combatSkills: REST.PlayerCombatSkills;
    t: TFunction<"translation", undefined>;
}

const CharacterStatsBlock = ({stats, secondarySkills, combatSkills, t} :CharacterStatsProps) => {

    return(
        <div>
            <Table>
                <thead>
                    <tr>
                        <th>{t('character.attributes.header', {ns:'character'})}</th>
                        <th>{t('character.attributes.header.allocated', {ns:'character'})}</th>
                        <th>{t('character.attributes.header.gear', {ns:'character'})}</th>
                        <th>{t('character.attributes.header.total', {ns:'character'})}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>{t('character.attributes.ENDURANCE', {ns: 'character'})}</td>
                        <td>{stats.characterAttributes.endurance}</td>
                        <td>+{stats.equipmentAttributes.endurance}</td>
                        <td>{stats.characterAttributes.endurance + stats.equipmentAttributes.endurance}</td>
                    </tr>
                    <tr>
                        <td>{t('character.attributes.STRENGTH', {ns: 'character'})}</td>
                        <td>{stats.characterAttributes.strength}</td>
                        <td>+{stats.equipmentAttributes.strength}</td>
                        <td>{stats.characterAttributes.strength + stats.equipmentAttributes.strength}</td>
                    </tr>
                    <tr>
                        <td>{t('character.attributes.DEXTERITY', {ns: 'character'})}</td>
                        <td>{stats.characterAttributes.dexterity}</td>
                        <td>+{stats.equipmentAttributes.dexterity}</td>
                        <td>{stats.characterAttributes.dexterity + stats.equipmentAttributes.dexterity}</td>
                    </tr>
                    <tr>
                        <td>{t('character.attributes.WISDOM', {ns: 'character'})}</td>
                        <td>{stats.characterAttributes.wisdom}</td>
                        <td>+{stats.equipmentAttributes.wisdom}</td>
                        <td>{stats.characterAttributes.wisdom + stats.equipmentAttributes.wisdom}</td>
                    </tr>
                    <tr>
                        <td>{t('character.attributes.INTELLIGENCE', {ns: 'character'})}</td>
                        <td>{stats.characterAttributes.intelligence}</td>
                        <td>+{stats.equipmentAttributes.intelligence}</td>
                        <td>{stats.characterAttributes.intelligence + stats.equipmentAttributes.intelligence}</td>
                    </tr>
                    <tr>
                        <td>{t('character.attributes.DEFENSE', {ns: 'character'})}</td>
                        <td>{stats.characterAttributes.defense}</td>
                        <td>+{stats.equipmentAttributes.defense}</td>
                        <td>{stats.characterAttributes.defense + stats.equipmentAttributes.defense}</td>
                    </tr>
                    <tr>
                        <td>{t('character.attributes.LUCK', {ns: 'character'})}</td>
                        <td>{stats.characterAttributes.luck}</td>
                        <td>+{stats.equipmentAttributes.luck}</td>
                        <td>{stats.characterAttributes.luck + stats.equipmentAttributes.luck}</td>
                    </tr>
                </tbody>
            </Table>
            <Table>
                <thead>
                    <tr>
                        <th>{t('character.attributes.header', {ns:'character'})}</th>
                        <th>{t('character.attributes.header.total', {ns:'character'})}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>{t('character.skills.DODGE', {ns: 'character'})}</td>
                        <td>{combatSkills.dodge}%</td>
                    </tr>
                    <tr>
                        <td>{t('character.skills.CRITICAL', {ns: 'character'})}</td>
                        <td>{combatSkills.critical}%</td>
                    </tr>
                    <tr>
                        <td>{t('character.skills.BLOCK', {ns: 'character'})}</td>
                        <td>{combatSkills.block}%</td>
                    </tr>
                    <tr>
                        <td>{t('character.skills.PARRY', {ns: 'character'})}</td>
                        <td>{combatSkills.parry}%</td>
                    </tr>
                    <tr>
                        <td>{t('character.skills.SPELLPOWER', {ns: 'character'})}</td>
                        <td>+{combatSkills.spellpower}</td>
                    </tr>
                    <tr>
                        <td>{t('character.skills.HEALPOWER', {ns: 'character'})}</td>
                        <td>+{combatSkills.healpower}</td>
                    </tr>
                </tbody>
            </Table>
            <Table>
                <thead>
                    <tr>
                        <th>{t('character.attributes.header', {ns:'character'})}</th>
                        <th>{t('character.attributes.header.allocated', {ns:'character'})}</th>
                        <th>{t('character.attributes.header.gear', {ns:'character'})}</th>
                        <th>{t('character.attributes.header.total', {ns:'character'})}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>{t('character.secondary.skills.WOODCUTTING', {ns: 'character'})}</td>
                        <td>{secondarySkills.playerSkills.woodcutting}</td>
                        <td>+{secondarySkills.equipmentSkills.woodcutting}</td>
                        <td>{secondarySkills.playerSkills.woodcutting + secondarySkills.equipmentSkills.woodcutting}</td>
                    </tr>
                    <tr>
                        <td>{t('character.secondary.skills.FISHING', {ns: 'character'})}</td>
                        <td>{secondarySkills.playerSkills.fishing}</td>
                        <td>+{secondarySkills.equipmentSkills.fishing}</td>
                        <td>{secondarySkills.playerSkills.fishing + secondarySkills.equipmentSkills.fishing}</td>
                    </tr>
                    <tr>
                        <td>{t('character.secondary.skills.MINING', {ns: 'character'})}</td>
                        <td>{secondarySkills.playerSkills.mining}</td>
                        <td>+{secondarySkills.equipmentSkills.mining}</td>
                        <td>{secondarySkills.playerSkills.mining + secondarySkills.equipmentSkills.mining}</td>
                    </tr>
                    <tr>
                        <td>{t('character.secondary.skills.ALCHEMY', {ns: 'character'})}</td>
                        <td>{secondarySkills.playerSkills.alchemy}</td>
                        <td>+{secondarySkills.equipmentSkills.alchemy}</td>
                        <td>{secondarySkills.playerSkills.alchemy + secondarySkills.equipmentSkills.alchemy}</td>
                    </tr>
                    <tr>
                        <td>{t('character.secondary.skills.ENCHANTING', {ns: 'character'})}</td>
                        <td>{secondarySkills.playerSkills.enchanting}</td>
                        <td>+{secondarySkills.equipmentSkills.enchanting}</td>
                        <td>{secondarySkills.playerSkills.enchanting + secondarySkills.equipmentSkills.enchanting}</td>
                    </tr>
                </tbody>
            </Table>
        </div>
    )
}

const ItemDetailView = ({translation, item, visible, x, y}: ItemDetailViewProperties) => {
    const t = translation;
    const [selectedItem, setSelectedItem] = useState<DropableItem | null>(null);
    const state = useTrackedState();

    useEffect(() => {
        if(!item) {
            return;
        }
        setSelectedItem(item.item);
    }, [item]);

    function getGearBonuses() {
        let attributeBonus: JSX.Element[] = [];
        let skillBonus: JSX.Element[]  = [];
        let secondarySkillBonus: JSX.Element[]  = [];
        let attackResourceBonus: JSX.Element[]  = [];

        if(selectedItem && selectedItem.attributeBonuses && selectedItem.attributeBonuses.length > 0) {
            attributeBonus = selectedItem.attributeBonuses.map((bonus: Items.AttributeBonus) => {
                return <li className="buff-text">+{bonus.bonus} {t('character.attributes.' + bonus.attribute, {ns:'character'})}</li>
            });
        }

        if(selectedItem && selectedItem.skillBonuses && selectedItem.skillBonuses.length > 0) {
            skillBonus = selectedItem.skillBonuses.map((bonus: Items.CombatSkillBonus) => {
                return <li className="buff-text">+{bonus.bonus}% {t('character.skills.' + bonus.skill, {ns:'character'})}</li>
            });
        }

        if(selectedItem && selectedItem.secondarySkillBonuses && selectedItem.secondarySkillBonuses.length > 0) {
            secondarySkillBonus = selectedItem.secondarySkillBonuses.map(bonus => {
                return <li className="buff-text">+{bonus.bonus} {t('character.secondary.skills.' + bonus.skill, {ns:'character'})}</li>
            });
        }
        if(selectedItem && selectedItem.atkResourceBonuses && selectedItem.atkResourceBonuses.length > 0) {
            attackResourceBonus = selectedItem.atkResourceBonuses.map(bonus => {
                return <li className="buff-text">+{bonus.bonus} {t('character.energy.type.' + bonus.skill, {ns:'character'})}</li>
            });
        }

        return (
            <ul style={{paddingLeft: "0", marginTop: "1em"}}>
                {attributeBonus}
                {skillBonus}
                {secondarySkillBonus}
                {attackResourceBonus}
            </ul>
        );
    }

    function getDetailsContainer() {
        if(!item) return <div/>
        if(!selectedItem) return <div/>

        let bodyContent = null;

        let classExclusiveElement: JSX.Element = <span />;
        let raceExclusiveElement: JSX.Element = <span />;
        let exclusiveClass;

        if(item.type === 'weapon') {
            if(selectedItem.classExclusive) {
                if(selectedItem.classExclusive === state[0].characterData!.characterClass.id) {
                    exclusiveClass = 'item-exclusive-allowed';
                }
                else {
                    exclusiveClass = 'item-exclusive-denied';
                }
                classExclusiveElement = <span className={"card-header-text " + exclusiveClass}>{t('items.gear.exclusive.class.' + selectedItem.classExclusive, {ns:'characterClass'})}</span>
            }
            if(selectedItem.raceExclusive) {
                if(selectedItem.raceExclusive === state[0].characterData!.race.id) {
                    exclusiveClass = 'item-exclusive-allowed';
                }
                else {
                    exclusiveClass = 'item-exclusive-denied';
                }

                raceExclusiveElement = <span className={"card-header-text " + exclusiveClass}>{t('items.gear.exclusive.race.' + selectedItem.raceExclusive, {ns:'race'})}</span>
            }
            bodyContent = (
                <CardBody style={{ textAlign: "left", paddingTop: "0" }}>
                    <CardText>
                        <span className="card-header-text">{t('items.weapon.hand.' + selectedItem.hand, {ns:'items'})} {t('items.weapon.type.' + selectedItem.type, {ns:'items'})}</span>
                    </CardText>
                    <CardText>
                        {selectedItem.minDamage} - {selectedItem.maxDamage} {t('items.weapon.damage', {ns:'items'})}
                    </CardText>
                    {getGearBonuses()}
                    <CardText>
                        {raceExclusiveElement}
                        {classExclusiveElement}
                    </CardText>
                </CardBody>

            )
        }
        else if(item.type === 'equipment') {
            if(selectedItem.classExclusive) {
                if(selectedItem.classExclusive === state[0].characterData!.characterClass.id) {
                    exclusiveClass = 'item-exclusive-allowed';
                }
                else {
                    exclusiveClass = 'item-exclusive-denied';
                }
                classExclusiveElement = <span className={"card-header-text " + exclusiveClass}>{t('items.gear.exclusive.class.' + selectedItem.classExclusive, {ns:'characterClass'})}</span>
            }
            if(selectedItem.raceExclusive) {
                if(selectedItem.raceExclusive === state[0].characterData!.race.id) {
                    exclusiveClass = 'item-exclusive-allowed';
                }
                else {
                    exclusiveClass = 'item-exclusive-denied';
                }
                raceExclusiveElement = <span className={"card-header-text " + exclusiveClass}>{t('items.gear.exclusive.race.' + selectedItem.raceExclusive, {ns:'race'})}</span>
            }
            bodyContent = (
                <CardBody style={{ textAlign: "left", paddingTop: "0"  }}>

                    <CardText>
                        <span className="card-header-text">{t('items.armor.type.' + selectedItem.type, {ns:'items'})}</span>
                    </CardText>
                    <CardText>
                        {selectedItem.armor} {t('items.armor.value', {ns:'items'})}
                    </CardText>

                    {getGearBonuses()}
                    <CardText>
                        {raceExclusiveElement}
                        {classExclusiveElement}
                    </CardText>
                </CardBody>
            )
        }
        else if(item.type === 'consumable') {
            let healthRestore: JSX.Element = <span />;
            let resoureRestore: JSX.Element = <span />;

            if(selectedItem.healthRestored && selectedItem.healthRestored > 0) {
                healthRestore = <span className="card-header-text">+{selectedItem.healthRestored} {t('items.consumable.restore.health', {ns:'items'})}</span>
            }
            if(selectedItem.atkResourceRestored && selectedItem.atkResourceRestored > 0) {
                resoureRestore = <span className="card-header-text">+{selectedItem.atkResourceRestored} {t('items.consumable.restore.resource.' + selectedItem.resourceType, {ns:'items'})}</span>
            }

            let attributeBonus: JSX.Element[] = [];
            let skillBonus: JSX.Element[] = [];
            let roundsDuration: JSX.Element = <li />;

            if(selectedItem.attributeModifiers && selectedItem.attributeModifiers.length > 0) {
                attributeBonus = selectedItem.attributeModifiers.map(bonus => {
                    return <li className="buff-text">+{bonus.bonus} {t('character.attributes.' + bonus.attribute, {ns:'character'})}</li>
                });
                console.log(attributeBonus);
            }

            if(selectedItem.combatSkillModifiers && selectedItem.combatSkillModifiers.length > 0) {
                skillBonus = selectedItem.combatSkillModifiers.map(bonus => {
                    return <li className="buff-text">+{bonus.bonus}% {t('character.skills.' + bonus.skill, {ns:'character'})}</li>
                });
            }

            if(skillBonus.length > 0 || attributeBonus.length > 0) {
                let consumableDuration = "";
                if(selectedItem.durationRounds && selectedItem.durationRounds > 0) {
                    consumableDuration = selectedItem.durationRounds + " " + t('items.consumable.duration.rounds', {ns: 'items'})
                }
                else {
                    consumableDuration = t('items.consumable.duration.rounds.full', {ns: 'items'})
                }
                roundsDuration = <li>{t('items.consumable.duration', {ns: 'items'})}: {consumableDuration}</li>
            }

            let bonusDetails: JSX.Element = <div/>;
            bonusDetails = (
                <CardText>
                    <ul style={{paddingLeft: "0", marginTop: "1em"}}>
                        {attributeBonus}
                        {skillBonus}
                        {roundsDuration}
                    </ul>
                </CardText>
            )

            bodyContent = (
                <CardBody style={{ textAlign: "left", paddingTop: "0"  }}>
                    <CardText>
                        {healthRestore}
                        {resoureRestore}
                    </CardText>
                    {bonusDetails}
                    <CardText>
                        <span className="card-header-text">{t('items.consumable.usable.battle.' + selectedItem.duringBattle, {ns:'items'})}</span>
                        <span className="card-header-text">{t('items.consumable.usable.inventory.' + selectedItem.fromInventory, {ns:'items'})}</span>
                    </CardText>
                </CardBody>
            )
        }
        else {
            return <div>Unknown item type {item.type}</div>
        }

        return (
            <Card>
                <CardHeader>
                    <CardImg className={"item-icon " + selectedItem.rarity.toLowerCase()} left src={selectedItem.iconName} />
                    <div className="card-header-text">
                        <span className="card-header-text">{t(selectedItem.name, {ns: 'items'})}</span>
                        <span className="card-header-text">{t('items.rarity.' + selectedItem.rarity.toLowerCase(), {ns: 'items'})}</span>
                    </div>
                </CardHeader>
                {bodyContent}
                <CardFooter style={{ textAlign: "left" }}>
                    <span className="card-footer-text">{t(selectedItem.description, {ns: 'items'})}</span>
                    <CardText>
                        {t('items.value', {ns:'items'})}: {selectedItem.value} {t('character.stats.gold', {ns:'character'})}
                    </CardText>
                </CardFooter>
            </Card>
        )
    }

    if(visible === 'hidden') {
        return null;
    }

    return (
        <div className="item-detail-view" style={{visibility: 'visible', left: x, top: y}}>
            {getDetailsContainer()}
        </div>
    )
}

function SimpleDialog({translation, selectedValue, onClose, open}: SimpleDialogProps) {
    const t = translation;
    const [value1, setValue1] = useState(1);
    const [userState, setUserState] = useTrackedState();

    const handleClose = () => {
        // some graceful stuff? idk
        setValue1(1);
        onClose('cancel');
    };

    const handleListItemClick = () => {

        const dropItemBody: REST.DropItemDetails = {
            itemId: selectedValue!.item.id,
            count: value1
        };

        // perform sync drop item call to REST
        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : userState.token!.token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dropItemBody)
        };

        fetch('/api/game/inventory/drop', requestOptions)
        .then((response) => {
            onClose(response.status.toString());
        });

        setValue1(1);
    };

    if(!selectedValue || !selectedValue.item) {
        return <div />
    }

    function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
        event.preventDefault();
        let valueAsString: string = event.currentTarget.value;
        let value: number = parseInt(valueAsString);
        setValue1(value);
    }

    return (
        <Dialog open={open}>
            <DialogTitle>{t('items.manager.dialog.drop.title', {ns: 'items'})}: {selectedValue.item.name} (x{selectedValue.count})</DialogTitle>
            <div style={{padding: "0 1em"}}>
                <Input type={"range"}
                    min={1}
                    max={selectedValue.count}
                    onChange={handleChange}
                    value={value1}
                />
                <span>{value1} / {selectedValue.count}</span>
            </div>
            <Container fluid>
                <Button onClick={handleListItemClick}>{t('items.manager.dialog.drop.confirm', {ns:'items'})}</Button>
                <Button onClick={handleClose}>{t('items.manager.dialog.drop.cancel', {ns:'items'})}</Button>
            </Container>
        </Dialog>
    );
}

const InventoryList = ({inventory, translation, onReload}: InventoryListProperties) => {
    const t = translation;
    const [userState, setUserState] = useTrackedState();
    const [selectedItem, setSelectedItem] = useState<InventoryItem | null>(null);
    const [detailsVisible, setDetailsVisible] = useState("");
    const [x, setX] = useState(0);
    const [y, setY] = useState(0);
    const [menuProps, toggleMenu] = useMenuState();
    const [anchorPoint, setAnchorPoint] = useState({ x: 0, y: 0 });
    const [contextMenuItem, setContextMenuItem] = useState<ContextMenuItemSelection>({item: null, name: null});
    const [dropDialogOpen, setDropDialogOpen] = useState(false);

    useEffect(() => {
        if(!selectedItem) {
            setDetailsVisible("hidden");
        }
        else {
            setDetailsVisible("visible");
        }
    }, [selectedItem, x, y]);

    function mouseLeftItem(event: React.SyntheticEvent) {
        event.preventDefault();
        setSelectedItem(null);
    }

    function getItemFromEvent(event: React.MouseEvent<HTMLElement>) {
        let card: HTMLElement | null = event.currentTarget;

        while(!card.attributes.getNamedItem("foo")) {
            if(!card.parentElement) {
                card = null;
                return;
            }
            card = card.parentElement;
        }

        const itemId = card.attributes.getNamedItem("foo")!.value;

        return inventory!.find(element => element.item.id === itemId);
    }

    function mouseOverItem(event: React.MouseEvent<HTMLElement>) {
        event.preventDefault();
        let selectedItem = getItemFromEvent(event);
        if(!selectedItem) {
            setSelectedItem(null);
        }
        else {
            setSelectedItem(selectedItem);
        }
        setX(event.clientX);
        setY(event.clientY);
    }

    function mouseClickOnItem(event: React.MouseEvent<HTMLElement>) {
        event.preventDefault();

        setAnchorPoint({ x: event.clientX, y: event.clientY });
        setContextMenuItem({item: selectedItem, name: t(selectedItem!.item.name, {ns: 'items'})})
        toggleMenu(true);
    }
    function closeContextMenu(event: MenuCloseEvent) {
        toggleMenu(false);
    }

    function performUseItem(item: InventoryItem) {
        const useItemBody: REST.UseItemDetails = {
            itemId: item!.item.id
        };

        // perform sync drop item call to REST
        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : userState.token!.token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(useItemBody)
        };

        fetch('/api/game/inventory/use', requestOptions)
            .then((response) => {
                toggleMenu(false);
                onReload();

                if(response.status === 400) {
                    setUserState((prevState) => ({...prevState,
                        globalErrorMessage: {
                            header: t('items.use.error.header', {ns: 'items'}),
                            body: t('items.use.error.invalid.item', {ns: 'items'})
                        }
                    }));
                }
                if(response.status === 404) {
                    setUserState((prevState) => ({...prevState,
                        globalErrorMessage: {
                            header: t('items.use.error.header', {ns: 'items'}),
                            body: t('items.use.error.missing.item', {ns: 'items'})
                        }
                    }));
                }
                if(response.status === 406) {
                    setUserState((prevState) => ({...prevState,
                        globalErrorMessage: {
                            header: t('items.use.error.header', {ns: 'items'}),
                            body: t('items.use.error.too.many', {ns: 'items'})
                        }
                    }));
                }
                else if(response.status === 500) {
                    setUserState((prevState) => ({...prevState,
                        globalErrorMessage: {
                            header: t('items.use.error.header', {ns: 'items'}),
                            body: t('items.use.error.server', {ns: 'items'})
                        }
                    }));
                }
                else {
                    setUserState((prevState) => ({...prevState, characterEquipmentChange: userState.characterEquipmentChange + 1 ,globalErrorMessage: null}));
                }
            });
    }

    function performEquipItem(item: InventoryItem) {
        // figure out the slot based on the item if you can..
        if(item.item.hand) {
            // it's a weapon
            let hand: string = item.item.hand;
            switch (hand) {
                case 'RIGHT':
                case 'TWOHANDED':
                    performEquipItemWithSlot(item, 'MAINHAND');
                    break;
                case 'LEFT':
                    performEquipItemWithSlot(item, 'OFFHAND');
                    break;
                case 'BOTH':
                default:
                    console.log('method not allowed for hand ' + hand);
            }
        }
        else {
            let type: string = item.item.type!;
            switch (type) {
                case 'HELMET':
                case 'CHEST':
                case 'GLOVES':
                case 'PANTS':
                case 'BOOTS':
                case 'NECK':
                    performEquipItemWithSlot(item, type);
                    break;
                default:
                    console.log('method not allowed for type ' + type);
            }
        }
    }

    function performEquipItemWithSlot(item:InventoryItem, slot: REST.EquipmentSlot) {
        const equipItemBody: REST.EquipRequest = {
            itemId: item.item.id,
            slot: slot
        }

        // perform sync drop item call to REST
        const requestOptions = {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN' : userState.token!.token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(equipItemBody)
        };

        fetch('/api/game/character/equip', requestOptions)
            .then((response) => {
                toggleMenu(false);
                onReload();
                if(response.status === 400) {
                    setUserState((prevState) => ({...prevState,
                        globalErrorMessage: {
                            header: t('items.equip.error.header', {ns: 'items'}),
                            body: t('items.equip.error.generic', {ns: 'items'})
                        }
                    }));
                }
                else if(response.status === 500) {
                    setUserState((prevState) => ({...prevState,
                        globalErrorMessage: {
                            header: t('items.equip.error.header', {ns: 'items'}),
                            body: t('items.equip.error.server', {ns: 'items'})
                        }
                    }));
                }
                else {
                    setUserState((prevState) => ({...prevState, characterEquipmentChange: userState.characterEquipmentChange + 1 ,globalErrorMessage: null}));
                }
            });

        // TODO refresh character panel
    }

    function handleMenuItemClick(action: 'drop' | 'use' | 'equip' | 'close') {
        if(action === 'drop') {
            setDropDialogOpen(true);
        }
        else if(action === 'use') {
            performUseItem(contextMenuItem.item!);
        }
        else if(action === 'equip') {
            performEquipItem(contextMenuItem.item!)
        }
        else {
            toggleMenu(false);
        }
    }

    function handleEquipSubMenuItemClick(slot: REST.EquipmentSlot) {
        performEquipItemWithSlot(contextMenuItem.item!, slot);
    }

    const handleDropDialogClose = (value: string) => {
        setDropDialogOpen(false);

        if(value === '400') {
            // item is not an actual known item
            setUserState((prevState) => ({...prevState,
                globalErrorMessage: {
                    header: t('items.drop.error.header', {ns:'items'}),
                    body: t('items.drop.error.invalid.item', {ns:'items'})
                }
            }));
        }
        else if(value === '404') {
            // item is not in player inventory
            setUserState((prevState) => ({...prevState,
                globalErrorMessage: {
                    header: t('items.drop.error.header', {ns:'items'}),
                    body: t('items.drop.error.missing.inventory', {ns:'items'})
                }
            }));
        }
        else if(value === '406') {
            // tried to drop more than in inventory
            setUserState((prevState) => ({...prevState,
                globalErrorMessage: {
                    header: t('items.drop.error.header', {ns:'items'}),
                    body: t('items.drop.error.too.many', {ns:'items'})
                }
            }));
        }
        else if(value === '200') {
            // item has been dropped
            setUserState((prevState) => ({...prevState, globalErrorMessage: null}));
            onReload();
        }
        else {
            // unknown return code
            setUserState((prevState) => ({...prevState,
                globalErrorMessage: {
                    header: t('items.drop.error.header', {ns:'items'}),
                    body: t('items.drop.error.unknown', {ns:'items'})
                }
            }));
        }
    };

    if(!inventory) {
        return <div className="inventory-items-list" />;
    }
    const itemCards = inventory.map(
        entry => {
          return (
              <Card foo={entry.item.id}
                    onClick={mouseClickOnItem}
                    onMouseOver={mouseOverItem}
                    onMouseLeave={mouseLeftItem}
                    className="item-card"
                    key={entry.item.id}>
                  <CardBody className="item-card-body">
                      <CardImg className={"item-icon " + entry.item.rarity.toLowerCase()} top src={entry.item.iconName} />
                      <CardTitle tag="p">{t(entry.item.name, {ns: 'items'})}</CardTitle>
                      <CardText tag="p" className="item-count">x{entry.count}</CardText>
                  </CardBody>
              </Card>
          )
      })

    function contextMenu() {
        if(!contextMenuItem.name || !contextMenuItem.item) {
            return <div/>;
        }

        let useMenuButton: JSX.Element | null = null;
        if(contextMenuItem.item.type === 'consumable' && contextMenuItem.item.item.fromInventory === true) {
            useMenuButton = <MenuItem onClick={() => handleMenuItemClick('use')}>{t('items.manager.context.menu.use', {ns:'items'})}</MenuItem>
        }

        let equipMenuButton: JSX.Element | null = null;
        // Context menu 'equip' for standard items
        // = non rings (2 slots) and weapons that can go in both hands
        // plus check on race and class restrictions
        if((contextMenuItem.item.type === 'weapon' || contextMenuItem.item.type === 'equipment')
            && (contextMenuItem.item.item.type !== 'RING' && contextMenuItem.item.item.hand !== 'BOTH')
            && (contextMenuItem.item.item.classExclusive == null || contextMenuItem.item.item.classExclusive === userState.characterData!.characterClass.id)
            && (contextMenuItem.item.item.raceExclusive == null || contextMenuItem.item.item.raceExclusive === userState.characterData!.race.id)) {
            equipMenuButton = <MenuItem onClick={() => handleMenuItemClick('equip')}>{t('items.manager.context.menu.equip', {ns:'items'})}</MenuItem>
        }
        else if(contextMenuItem.item.item.type === 'RING') {
            equipMenuButton = (
                <SubMenu label={t('items.manager.context.menu.equip', {ns:'items'})}>
                    <MenuItem onClick={() => handleEquipSubMenuItemClick('RING1')}>{t('items.manager.context.menu.slot.ring1', {ns:'items'})}</MenuItem>
                    <MenuItem onClick={() => handleEquipSubMenuItemClick('RING2')}>{t('items.manager.context.menu.slot.ring2', {ns:'items'})}</MenuItem>
                </SubMenu>
            )
        }
        else if(contextMenuItem.item.item.hand == 'BOTH') {
            equipMenuButton = (
                <SubMenu label={t('items.manager.context.menu.equip', {ns:'items'})}>
                    <MenuItem onClick={() => handleEquipSubMenuItemClick('MAINHAND')}>{t('items.manager.context.menu.slot.mainhand', {ns:'items'})}</MenuItem>
                    <MenuItem onClick={() => handleEquipSubMenuItemClick('OFFHAND')}>{t('items.manager.context.menu.slot.offhand', {ns:'items'})}</MenuItem>
                </SubMenu>
            )
        }

        return (
            <ControlledMenu {...menuProps}
                anchorPoint={anchorPoint}
                onClose={closeContextMenu}
                menuStyle={{zIndex:9999}}>
                <MenuHeader>{contextMenuItem.name && contextMenuItem.name + ' (x' + contextMenuItem.item.count + ')'}</MenuHeader>
                {equipMenuButton}
                {useMenuButton}
                <MenuItem onClick={() => handleMenuItemClick('drop')}>{t('items.manager.context.menu.drop', {ns:'items'})}</MenuItem>
                <MenuItem onClick={() => handleMenuItemClick('close')}>{t('items.manager.context.menu.close', {ns:'items'})}</MenuItem>
            </ControlledMenu>
        )
    }

    return(
        <div className="inventory-items-list">
            <CardGroup>
                {itemCards}
            </CardGroup>
            <ItemDetailView translation={t} visible={detailsVisible} item={selectedItem} x={x} y={y} />
            {contextMenu()}
            <SimpleDialog
                selectedValue={contextMenuItem.item}
                open={dropDialogOpen}
                onClose={handleDropDialogClose}
                translation={t}
            />
        </div>
    );
// <ItemContextMenu onMenuClosing={removeClickedItem} translation={t} item={clickedItem} x={x} y={y} />
}

export const InventoryManager = ({translation}: TranslationAsProperty) => {
    const t = translation;

    const [inventory, setInventory] = useState<InventoryType | null>(null);
    const [searchField, setSearchField] = useState("");
    const [sortMode, setSortMode] = useState("");
    const [reload, setReload] = useState(0);

    useEffect(() => {
        const getInventory = async() => {
            const resp = await fetch('/api/game/inventory');
            const data = await resp.json();
            setInventory(data);
        };
        getInventory();
    }, [reload]);

    function reloadInventory() {
        if(!reload) {
            setReload(1);
        }
        else {
            setReload(reload + 1);
        }
    }

    let filteredItems = null;
    if(inventory) {
        filteredItems = inventory.items.filter(
            entry => {
                if(entry.count === 0) {
                    return false;
                }
                const translatedName = t(entry.item.name, {ns:'items'});
                return (
                    translatedName.toLowerCase().includes(searchField.toLowerCase())
                );
            }
        );
    }
    if(filteredItems) {
        filteredItems.sort((a, b) => {
            switch(sortMode) {
                case 'type':
                    return itemTypeSort(a,b);
                case 'name':
                    return itemNameSort(a,b,t);
                case 'value':
                    return itemValueSort(a,b);
                case 'quantity':
                    return itemQuantitySort(a,b);
                case 'rarity':
                default:
                    return itemRaritySort(a,b);
            }
        });
    }

    const handleFilterChange = (e: ChangeEvent<HTMLInputElement>) => {
        setSearchField(e.target.value);
    };

    const handleSortModeChange = (e: ChangeEvent<HTMLInputElement>) => {
        setSortMode(e.target.value);
    }

    let currency: number = 0;
    if(inventory !== null) {
        currency = inventory.gold;
    }

    return (
        <div className={"inventory-manager"}>
            <InputGroup>
                <Input name="inventoryFilter" onChange={handleFilterChange} placeholder={t('items.manager.filter.text', {ns: 'items'})} />
                <Label style={{margin: "auto", paddingLeft: "1.25em", paddingRight: "0.5em"}} for="inventorySortMode">{t('items.manager.sort', {ns: 'items'})}:</Label>
                <Input name="inventorySortMode" onChange={handleSortModeChange} type="select">
                    <option value="rarity">{t('items.manager.sort.rarity', {ns: 'items'})}</option>
                    <option value="type">{t('items.manager.sort.type', {ns: 'items'})}</option>
                    <option value="name">{t('items.manager.sort.name', {ns: 'items'})}</option>
                    <option value="value">{t('items.manager.sort.value', {ns: 'items'})}</option>
                    <option value="quantity">{t('items.manager.sort.quantity', {ns: 'items'})}</option>
                </Input>
                <div className={"currency"}>
                    <img className={"gold-icon"} src={"../images/gold-icon.png"}/> {currency}
                </div>
            </InputGroup>

            <InventoryList inventory={filteredItems} translation={t} onReload={reloadInventory} />
        </div>
    )
}