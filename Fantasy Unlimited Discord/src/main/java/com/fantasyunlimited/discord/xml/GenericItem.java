package com.fantasyunlimited.discord.xml;

import java.io.Serializable;

public abstract class GenericItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2981498309896173220L;
	private String id;
	private String name;
	private String description;
	private String iconName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public boolean valuesFilled() {
		return (id != null && id.isEmpty() == false) && (name != null && name.isEmpty() == false)
				&& (description != null && description.isEmpty() == false);
	}
}
