package com.fantasyunlimited.discord.commands;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Triple;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.reactions.PaginationHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class CommandSupportsPaginatorHandler extends CommandRequiresAuthenticationHandler {

	private final boolean requiresActualAuthentication;
	
	/**
	 * Constructor passing the relevant information
	 * @param properties Passed through from the bot
	 * @param command The Command the implementing class is listening to
	 * @param requiresAuth Flag if the paginating control actually requires auth
	 */
	public CommandSupportsPaginatorHandler(Properties properties, String command, boolean requiresAuth) {
		super(properties, command);
		this.requiresActualAuthentication = requiresAuth;
	}
	
	@Override
	public boolean isAuthenticated(IUser user) {
		if(!requiresActualAuthentication) {
			return true;
		}
		return super.isAuthenticated(user);
	}

	/**
	 * This method needs to return three values:<br/>
	 * <ul>
	 * <li>Items per page (integer)</li>
	 * <li>MessageInformation object, prefilled with status and other relevant
	 * stuff</li>
	 * <li>List of values in use</li>
	 * </ul>
	 * 
	 * The delegating class will then build the message based on the values and
	 * items per page and adds relevant additional information about paging to
	 * the configuration.
	 * 
	 * @param event The event which will be passed from the bot
	 * @return See above
	 */
	public abstract Triple<Integer, MessageInformation, List<String>> doDelegate(MessageReceivedEvent event);

	@Override
	public void handle(MessageReceivedEvent event) {
		Triple<Integer, MessageInformation, List<String>> paginationParams = doDelegate(event);

		MessageInformation information = paginationParams.getMiddle();
		Integer itemsPerPage = paginationParams.getLeft();
		List<String> values = paginationParams.getRight();

		StringBuilder builder = new StringBuilder();
		builder.append("```\n");

		int breakCondition = itemsPerPage >= values.size() ? values.size()
				: itemsPerPage;
		for (int i = 0; i < breakCondition; i++) {
			builder.append(values.get(i) + "\n");
		}
		builder.append("```");

		IMessage message = FantasyUnlimited.getInstance().sendMessage(event.getChannel(), builder.toString());

		FantasyUnlimited.getInstance().addReactions(message, Unicodes.arrow_backward, Unicodes.arrow_forward);

		information.setMessage(message);
		information.getStatus().setPaginator(true);
		information.getStatus().setCurrentPage(1);
		information.getStatus().setItemsPerPage(itemsPerPage);
		information.getVars().put(PaginationHandler.VARNAME, values);
		FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(message.getLongID(), information);
	}

	@Override
	public abstract String getDescription();

	@Override
	public abstract Type getType();

}
