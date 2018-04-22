package com.fantasyunlimited.discord.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.reactions.CharacterCreationHandler;
import com.fantasyunlimited.discord.reactions.ReactionsHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class ReactionForSelfAddHandler extends EventHandler<ReactionAddEvent> {

	private Map<MessageStatus.Name, ReactionsHandler> reactionHandlers = new HashMap<>();

	public ReactionForSelfAddHandler(Properties properties) {
		super(properties);
		FantasyUnlimited.autowire(this);

		CharacterCreationHandler characterCreationHandler = new CharacterCreationHandler(properties);
		reactionHandlers.put(Name.CREATE_CHAR_CLASS_SELECTION, characterCreationHandler);
		reactionHandlers.put(Name.CREATE_CHAR_RACE_SELECTION, characterCreationHandler);
		reactionHandlers.put(Name.CREATE_CHAR_CONFIRMATION, characterCreationHandler);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		if(event.getUser().isBot()) return;
		
		MessageInformation information = FantasyUnlimited.getInstance().getMessagesAwaitingReactions()
				.get(event.getMessage().getLongID());
		if (information == null) {
			return;
		}
		if (information.isCanBeRemoved()) { // In case it hasn't been cleaned up
											// yet
			FantasyUnlimited.getInstance().getMessagesAwaitingReactions().remove(event.getMessage().getLongID());
			return;
		}

		if (information.getOriginator().getLongID() != event.getUser().getLongID()) {
			return;
		}
		/*
		 * Go on from here: check what the message was about, then pick a
		 * handler for it
		 */
		ReactionsHandler handler = reactionHandlers.get(information.getStatus().getName());
		if (handler == null) {
			FantasyUnlimited.getInstance().sendExceptionMessage(new IllegalStateException(
					"ReactionHandler for Status name " + information.getStatus().getName() + " is null."));
		} else {
			try {
				handler.handle(event);
			}
			catch(Exception e) {
				FantasyUnlimited.getInstance().sendExceptionMessage(e);
			}
		}
	}
}