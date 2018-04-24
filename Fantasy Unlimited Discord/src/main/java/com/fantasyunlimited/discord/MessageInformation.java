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
	private IUser originator;
	private IMessage message;
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
		return originator;
	}
	public void setOriginator(IUser originator) {
		this.originator = originator;
	}
	public IMessage getMessage() {
		return message;
	}
	public void setMessage(IMessage message) {
		this.message = message;
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
