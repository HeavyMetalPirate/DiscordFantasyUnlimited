package com.fantasyunlimited.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
}
