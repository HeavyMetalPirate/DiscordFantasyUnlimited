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
    Label
} from 'reactstrap';
import {
    ControlledMenu,
    MenuItem,
    MenuHeader,
    useMenuState, ClickEvent, MenuCloseEvent
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

export const EquipmentManager = ({translation}: TranslationAsProperty) => {
    const t = translation;

    return (
        <div>EquipmentManager TODO</div>
    )
}

const ItemDetailView = ({translation, item, visible, x, y}: ItemDetailViewProperties) => {
    const t = translation;
    const [selectedItem, setSelectedItem] = useState<DropableItem | null>(null);

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

        if(item.type === 'weapon') {

            if(selectedItem.classExclusive) {
                classExclusiveElement = <span className="card-header-text">{t('items.weapon.exclusive.class.' + selectedItem.classExclusive, {ns:'items'})}</span>
            }
            if(selectedItem.raceExclusive) {
                raceExclusiveElement = <span className="card-header-text">{t('items.weapon.exclusive.race.' + selectedItem.raceExclusive, {ns:'items'})}</span>
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
                classExclusiveElement = <span className="card-header-text">{t('items.weapon.exclusive.class.' + selectedItem.classExclusive, {ns:'items'})}</span>
            }
            if(selectedItem.raceExclusive) {
                raceExclusiveElement = <span className="card-header-text">{t('items.weapon.exclusive.race.' + selectedItem.raceExclusive, {ns:'items'})}</span>
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

        const dropItemBody = {
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

    function handleMenuItemClick(action: string) {
        if(action === 'drop') {
            setDropDialogOpen(true);
        }
    }

    const handleDropDialogClose = (value: string) => {
        setDropDialogOpen(false);

        if(value === '400') {
            // item is not an actual known item
        }
        else if(value === '404') {
            // item is not in player inventory
        }
        else if(value === '406') {
            // tried to drop more than in inventory
        }
        else if(value === '200') {
            // item has been dropped
            onReload();
        }
        else {
            // unknown return code
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

        return (
            <ControlledMenu {...menuProps}
                anchorPoint={anchorPoint}
                onClose={closeContextMenu}>
                <MenuHeader>{contextMenuItem.name && contextMenuItem.name + ' (x' + contextMenuItem.item.count + ')'}</MenuHeader>
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
                    return a.type > b.type ? 1 : -1;
                case 'name':
                    const aTranslatedName = t(a.item.name, {ns:'items'});
                    const bTranslatedName = t(b.item.name, {ns:'items'});
                    return aTranslatedName > bTranslatedName ? 1 : -1;
                case 'value':
                    return a.item.value > b.item.value ? -1 : 1;
                case 'quantity':
                    return a.count > b.count ? -1 : 1;
                case 'rarity':
                default:
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
        });
    }

    const handleFilterChange = (e: ChangeEvent<HTMLInputElement>) => {
        setSearchField(e.target.value);
    };

    const handleSortModeChange = (e: ChangeEvent<HTMLInputElement>) => {
        setSortMode(e.target.value);
    }

    return (
        <div>
            <span>InventoryManager TODO</span>
            <InputGroup>
                <Input name="inventoryFilter" onChange={handleFilterChange} style={{width: "70%"}} placeholder={t('items.manager.filter.text', {ns: 'items'})} />
                <Label style={{margin: "auto", paddingLeft: "1em", paddingRight: "0.25em"}} for="inventorySortMode">{t('items.manager.sort', {ns: 'items'})}:</Label>
                <Input name="inventorySortMode" onChange={handleSortModeChange} type="select">
                    <option value="rarity">{t('items.manager.sort.rarity', {ns: 'items'})}</option>
                    <option value="type">{t('items.manager.sort.type', {ns: 'items'})}</option>
                    <option value="name">{t('items.manager.sort.name', {ns: 'items'})}</option>
                    <option value="value">{t('items.manager.sort.value', {ns: 'items'})}</option>
                    <option value="quantity">{t('items.manager.sort.quantity', {ns: 'items'})}</option>
                </Input>
            </InputGroup>

            <InventoryList inventory={filteredItems} translation={t} onReload={reloadInventory} />
        </div>
    )
}