package com.fantasyunlimited.discord;

import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;


public class FantasyUnlimited extends BaseBot {
	
	public static final String PREFIX_KEY = "prefix";
	
	private static FantasyUnlimited INSTANCE;
	
	private final MessageReceivedHandler messageReceivedHandler;
	private final ReactionAddHandler reactionAddHandler;
		
	private WeaponBag weaponBag;
	
	public FantasyUnlimited(IDiscordClient discordClient, Properties properties) {
		super(discordClient);		
		client.changePlayingText("SUCK IT SLAYDEN");
		
		messageReceivedHandler = new MessageReceivedHandler(discordClient, properties);
		reactionAddHandler = new ReactionAddHandler(discordClient, properties);
		
		EventDispatcher dispatcher = discordClient.getDispatcher();
		dispatcher.registerListeners(messageReceivedHandler, reactionAddHandler);
		
		INSTANCE = this;
	}	
	
	public FantasyUnlimited getInstance() {
		return INSTANCE;
	}
	
	public static void autowire(Object bean)  {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).
                getAutowireCapableBeanFactory().
                autowireBean(bean);

	}
	
	public static void sendMessage(IDiscordClient client, IChannel channel, String message) {
		try {
			// Builds (sends) and new message in the channel that the original
			// message was sent with the content of the original message.
			new MessageBuilder(client).withChannel(channel).withContent(message).build();

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
