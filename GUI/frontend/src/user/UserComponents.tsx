import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ListGroup, ListGroupItem } from 'reactstrap';

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

export function UserSearch({details}: UserList) {
    const { t, i18n } = useTranslation();
    const [searchField, setSearchField] = useState("");

    if(!details) {
        return <div/>
    }
    const filteredUsers = details.filter(
        user => {
            return (
                user.name.toLowerCase().includes(searchField.toLowerCase())
            );
        }
    );

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchField(e.target.value);
    };

    function searchList() {
        return (
            <SearchList filteredUsers={filteredUsers} />
        );
    }

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