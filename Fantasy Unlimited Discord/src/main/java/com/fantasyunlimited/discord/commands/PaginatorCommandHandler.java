package com.fantasyunlimited.discord.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Triple;

import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.MessageStatus.Name;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class PaginatorCommandHandler extends CommandSupportsPaginatorHandler {

	public static final String CMD = "paginatorTest";

	private List<String> values = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
			"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28");

	public PaginatorCommandHandler(Properties properties) {
		super(properties, CMD, false);
	}

	@Override
	public Triple<Integer, MessageInformation, List<String>> doDelegate(MessageReceivedEvent event) {
				
		MessageInformation information = new MessageInformation();
		information.setCanBeRemoved(false);
		information.setOriginDate(event.getMessage().getTimestamp());
		information.setOriginator(event.getMessage().getAuthor());
		MessageStatus status = new MessageStatus();
		status.setName(Name.PAGINATION_TEST);
		information.setStatus(status);
		
		return Triple.of(10, information, values);
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
