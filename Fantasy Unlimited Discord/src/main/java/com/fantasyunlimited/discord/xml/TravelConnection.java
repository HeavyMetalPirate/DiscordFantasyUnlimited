package com.fantasyunlimited.discord.xml;

public class TravelConnection {
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
