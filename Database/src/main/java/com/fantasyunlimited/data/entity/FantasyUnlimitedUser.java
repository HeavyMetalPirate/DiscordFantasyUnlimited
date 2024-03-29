package com.fantasyunlimited.data.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "FU_Users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"userName", "userEmail"})}
)
public class FantasyUnlimitedUser {
    @Id
    private String userName;

    private String userEmail;
    private String password;

    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PlayerCharacter> characters = new java.util.ArrayList<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<PlayerCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<PlayerCharacter> characters) {
        this.characters = characters;
    }
}
