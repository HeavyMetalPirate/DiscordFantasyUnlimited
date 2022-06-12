package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class TravelConnection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -896987314491601642L;
	private String targetLocationId;
	private int duration;
	private int toll;
	
	public String getTargetLocationId() {
		return targetLocationId;
	}
	public void setTargetLocationId(String targetLocationId) {
		this.targetLocationId = targetLocationId;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getToll() {
		return toll;
	}
	public void setToll(int toll) {
		this.toll = toll;
	}
}
