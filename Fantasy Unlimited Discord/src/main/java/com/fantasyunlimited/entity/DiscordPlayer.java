package com.fantasyunlimited.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class DiscordPlayer implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4348607811654981419L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(unique = true)
	private String discordId;
	@Column
	private String name;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	private Date joinDate;
	
	@OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<PlayerCharacter> characters = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.EAGER)
	private PlayerCharacter currentCharacter;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDiscordId() {
		return discordId;
	}
	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getJoinDate() {
		return this.joinDate;
	}
	public List<PlayerCharacter> getCharacters() {
		return characters;
	}
	public void setCharacters(List<PlayerCharacter> characters) {
		this.characters = characters;
	}
	public PlayerCharacter getCurrentCharacter() {
		return currentCharacter;
	}
	public void setCurrentCharacter(PlayerCharacter currentCharacter) {
		this.currentCharacter = currentCharacter;
	}
}
