import React, {useEffect, useState} from 'react';
import { useTranslation } from 'react-i18next';
import { ListGroup, ListGroupItem } from 'reactstrap';
import {REST} from "../types/rest-entities";
import {IMessage} from "@stomp/stompjs";
import {useStompClient} from "../WebsocketClient";

export function SearchList({filteredUsers}: FilteredUserList) {
    const filtered = filteredUsers.map(user =>  {
        return (
            <ListGroupItem key={user.name}>{user.name}</ListGroupItem>
        )
    });
    return (
    <ListGroup>{filtered}</ListGroup>
    );
}

export function UserSearch() {
    const { t, i18n } = useTranslation();
    const [searchField, setSearchField] = useState("");
    const client = useStompClient();
    const [userList, setUserList] = useState<REST.ActiveUserItem[]>();

    useEffect(() => {
        client.subscribe('/topic/users', onMessageReceived);
        client.publish({
            destination : '/api/websocket/actives'
        });
    }, [])


    const onMessageReceived = (msg: IMessage) => {
        let foo: REST.ActiveUserItem[] = JSON.parse(msg.body);
        setUserList(foo);
    }

    if(!userList) {
        return <div/>
    }
    const filteredUsers = userList.filter(
        user => {
            return (
                user.name.toLowerCase().includes(searchField.toLowerCase())
            );
        }
    );

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchField(e.target.value);
    };

    return (
        <section>
            <div>
                <h2>{t('users.search.header')}</h2>
            </div>
            <div>
                <input type = "search" placeholder={t('users.search.placeholder')} onChange = {handleChange} />
            </div>
            <SearchList filteredUsers={filteredUsers} />
        </section>
    );
}