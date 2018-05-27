package com.fantasyunlimited.discord.xml;

import java.io.Serializable;

public class SkillRequirement implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5494338072135360114L;
	private String skillIdOnSelf;
	private String skillIdOnTarget;
	public String getSkillIdOnSelf() {
		return skillIdOnSelf;
	}
	public void setSkillIdOnSelf(String skillIdOnSelf) {
		this.skillIdOnSelf = skillIdOnSelf;
	}
	public String getSkillIdOnTarget() {
		return skillIdOnTarget;
	}
	public void setSkillIdOnTarget(String skillIdOnTarget) {
		this.skillIdOnTarget = skillIdOnTarget;
	}
}
