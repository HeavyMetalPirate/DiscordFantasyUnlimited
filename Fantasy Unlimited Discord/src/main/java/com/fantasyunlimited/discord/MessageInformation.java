package com.fantasyunlimited.discord;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class MessageInformation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2369759146570056441L;
	
	private LocalDateTime originDate;
	private transient IUser originator;
	private transient IMessage message;
	
	private long guildId;
	private long channelId;
	private long messageId;
	private long originatorId;
	
	private MessageStatus status;
	private boolean canBeRemoved;
	
	private Map<Object,Object> vars = new HashMap<Object,Object>();

	public LocalDateTime getOriginDate() {
		return originDate;
	}
	public void setOriginDate(LocalDateTime originDate) {
		this.originDate = originDate;
	}
	public IUser getOriginator() {
		if(originator == null) {
			System.out.println("\nNow I'm fetching the author...");
			originator = FantasyUnlimited.getInstance().fetchUser(originatorId);
		}
		return originator;
	}
	public void setOriginator(IUser originator) {
		this.originator = originator;
		this.originatorId = originator.getLongID();
	}
	public IMessage getMessage() {
		if(message == null) {
			System.out.println("\nNow I'm fetching the message...");
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
	public MessageStatus getStatus() {
		return status;
	}
	public void setStatus(MessageStatus status) {
		this.status = status;
	}
	public boolean isCanBeRemoved() {
		return canBeRemoved;
	}
	public void setCanBeRemoved(boolean canBeRemoved) {
		this.canBeRemoved = canBeRemoved;
	}
	public Map<Object,Object> getVars() {
		return vars;
	}
}
