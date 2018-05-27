package com.fantasyunlimited.discord.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.reactions.BasicPaginationDelegate;
import com.fantasyunlimited.discord.reactions.BattleHandler;
import com.fantasyunlimited.discord.reactions.CharacterCreationHandler;
import com.fantasyunlimited.discord.reactions.ReactionsHandler;
import com.fantasyunlimited.discord.reactions.SkillsHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class ReactionForSelfAddHandler extends EventHandler<ReactionAddEvent> {

	private Map<MessageStatus.Name, ReactionsHandler> reactionHandlers = new HashMap<>();

	public ReactionForSelfAddHandler(Properties properties) {
		super(properties);
		FantasyUnlimited.autowire(this);

		BasicPaginationDelegate basicPaginator = new BasicPaginationDelegate(properties);
		
		CharacterCreationHandler characterCreationHandler = new CharacterCreationHandler(properties);
		BattleHandler battleHandler = new BattleHandler(properties);
		
		reactionHandlers.put(Name.CREATE_CHAR_CLASS_SELECTION, characterCreationHandler);
		reactionHandlers.put(Name.CREATE_CHAR_RACE_SELECTION, characterCreationHandler);
		reactionHandlers.put(Name.CREATE_CHAR_CONFIRMATION, characterCreationHandler);
		reactionHandlers.put(Name.CHARACTER_LIST, basicPaginator);
		reactionHandlers.put(Name.PAGINATION_TEST, basicPaginator);
		reactionHandlers.put(Name.BATTLE_ACTIONBAR, battleHandler);
		reactionHandlers.put(Name.BATTLE_TARGETSELECTION, battleHandler);
		reactionHandlers.put(Name.SKILLS_INFO, new SkillsHandler(properties));
		
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
