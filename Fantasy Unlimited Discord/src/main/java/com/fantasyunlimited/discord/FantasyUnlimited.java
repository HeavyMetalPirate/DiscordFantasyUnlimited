package com.fantasyunlimited.discord;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class FantasyUnlimited extends BaseBot implements IListener<MessageReceivedEvent> {

	public FantasyUnlimited(IDiscordClient discordClient) {
		super(discordClient);
		EventDispatcher dispatcher = discordClient.getDispatcher();
		dispatcher.registerListener(this);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		IChannel channel = message.getChannel();
		sendMessage(channel, "You typed: " + message.getContent());
	}
	
	private void sendMessage(IChannel channel, String message) {

		try {
			// Builds (sends) and new message in the channel that the original
			// message was sent with the content of the original message.
			new MessageBuilder(this.client).withChannel(channel).withContent(message).build();

		} catch (RateLimitException e) { // RateLimitException thrown. The bot
											// is sending messages too quickly!
			System.err.print("Sending messages too quickly!");
			e.printStackTrace();
		} catch (DiscordException e) { // DiscordException thrown. Many
										// possibilities. Use getErrorMessage()
										// to see what went wrong.
			System.err.print(e.getErrorMessage()); // Print the error message
													// sent by Discord
			e.printStackTrace();
		} catch (MissingPermissionsException e) { // MissingPermissionsException
													// thrown. The bot doesn't
													// have permission to send
													// the message!
			System.err.print("Missing permissions for channel!");
			e.printStackTrace();
		}
	}
}
