package com.fantasyunlimited.discord.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.MessageStatus.Name;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class PaginatorCommandHandler extends CommandHandler {

	public static final String CMD = "paginatorTest";

	private List<String> values = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
			"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28");

	public PaginatorCommandHandler(Properties properties) {
		super(properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("```\n");
		
		for(int i = 0; i < 10; i++) {
			builder.append(values.get(i) + "\n");
		}
		builder.append("```");
		
		IMessage message = FantasyUnlimited.getInstance().sendMessage(event.getChannel(), builder.toString());
		FantasyUnlimited.getInstance().addReactions(message, Unicodes.arrow_forward);
		
		MessageInformation information = new MessageInformation();
		information.setCanBeRemoved(false);
		information.setOriginDate(event.getMessage().getTimestamp());
		information.setOriginator(event.getMessage().getAuthor());
		
		MessageStatus status = new MessageStatus();
		status.setCurrentPage(1);
		status.setPaginator(true);
		status.setItemsPerPage(10);
		status.setName(Name.PAGINATION_TEST);
		
		information.setStatus(status);
		information.setMessage(message);
		information.getVars().put("values", values);

		FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(message.getLongID(), information);
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Simple test command for pagination.";
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.OTHERS;
	}

}
