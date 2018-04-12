package com.fantasyunlimited.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.fantasyunlimited.discord.FantasyUnlimited;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.Configuration;
import sx.blah.discord.util.DiscordException;

public class InitializeBotListener implements ServletContextListener {
	private static Logger logger = Logger.getLogger(InitializeBotListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		//NO OP
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ClientBuilder builder = new ClientBuilder(); // Creates a new client builder instance
		Configuration.LOAD_EXTERNAL_MODULES = false;
		final String token = "NDM0MDY3NTk5NTg0NjU3NDE5.DbFAnA.fJi6akk6NzLYEnHFPbFum1oTtQU"; //Testing token for a Test Bot
		builder.withToken(token);
		
		try {
			logger.debug("Logging Discord bot in...");
			IDiscordClient client = builder.login(); // Builds the IDiscordClient instance and logs it in
			event.getServletContext().setAttribute("DiscordBot", new FantasyUnlimited(client)); // Creating the bot instance & tying it to the servlet context
		} catch (DiscordException e) { // Error occurred logging in
			System.err.println("Error occurred while logging in!");
			logger.error(e);
		}
	}

}
