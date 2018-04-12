package com.fantasyunlimited.discord.commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public abstract class CommandHandler implements IListener<MessageReceivedEvent> {
	
	protected final IDiscordClient client;
	public CommandHandler(IDiscordClient client) {
		this.client = client;
	}
	
	protected void sendMessage(IChannel channel, String message) {
		try {
			// Builds (sends) and new message in the channel that the original
			// message was sent with the content of the original message.
			new MessageBuilder(this.client).withChannel(channel).withContent(message).build();

		} catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
			System.err.print("Sending messages too quickly!");
			e.printStackTrace();
		} catch (DiscordException e) { // DiscordException thrown. Many ossibilities. Use getErrorMessage() to see what went wrong.
			System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
			e.printStackTrace();
		} catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
			System.err.print("Missing permissions for channel!");
			e.printStackTrace();
		}
	}
}
