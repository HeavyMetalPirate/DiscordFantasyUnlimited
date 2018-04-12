package com.fantasyunlimited.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Simple JPA entity to test functionality of Spring Data JPA.
 * Stores an Id and String value
 * @author HeavyMetalPirate
 * @version 1.0.0
 *
 */
@Entity
@Table(name="TestValues")
public class TestingEntity {

	public TestingEntity() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String text;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
