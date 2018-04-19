package com.fantasyunlimited.discord;

import java.time.LocalDateTime;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class MessageInformation {
	private LocalDateTime originDate;
	private IUser originator;
	private IMessage message;
	private MessageStatus status;
	private boolean canBeRemoved;
	
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
}
