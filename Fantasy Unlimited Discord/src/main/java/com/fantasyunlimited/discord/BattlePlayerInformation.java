package com.fantasyunlimited.discord;

import java.io.Serializable;

import com.fantasyunlimited.discord.entity.BattleParticipant;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.xml.Skill;

import sx.blah.discord.handle.obj.IMessage;

public class BattlePlayerInformation implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5898708685904083520L;

	private BattlePlayer character;
	
	private Skill skillUsed;
	private BattleParticipant target;
	
	private transient IMessage message;
	private long guildId;
	private long channelId;
	private long messageId;
	
	public BattlePlayer getCharacter() {
		return character;
	}
	public void setCharacter(BattlePlayer character) {
		this.character = character;
	}
	public IMessage getMessage() {
		if(message == null) {
			message = FantasyUnlimited.getInstance().fetchMessage(guildId, channelId, messageId);
		}
		return message;
	}
	public void setMessage(IMessage message) {
		this.message = message;
		this.guildId = message.getGuild().getLongID();
		this.channelId = message.getChannel().getLongID();
		this.messageId = message.getLongID();
	}
	public Skill getSkillUsed() {
		return skillUsed;
	}
	public void setSkillUsed(Skill skillUsed) {
		this.skillUsed = skillUsed;
	}
	public BattleParticipant getTarget() {
		return target;
	}
	public void setTarget(BattleParticipant target) {
		this.target = target;
	}
}
