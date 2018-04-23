package com.fantasyunlimited.web.listener;

import java.io.IOException;
import java.util.Properties;

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
		FantasyUnlimited bot = (FantasyUnlimited)(event.getServletContext().getAttribute("DiscordBot"));
		bot.getCacheManager().close();
		bot.logout();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot.properties"));
			
			ClientBuilder builder = new ClientBuilder(); // Creates a new client builder instance
			Configuration.LOAD_EXTERNAL_MODULES = false;
			final String token = properties.getProperty("token"); //Testing token for a Test Bot
			builder.withToken(token);
			builder.withRecommendedShardCount(true);
			
			logger.debug("Logging Discord bot in...");
			IDiscordClient client = builder.login(); // Builds the IDiscordClient instance and logs it in
			event.getServletContext().setAttribute("DiscordBot", new FantasyUnlimited(client, properties)); // Creating the bot instance & tying it to the servlet context
		} catch (DiscordException | IOException e) { // Error occurred logging in
			System.err.println("Error intializing bot!");
			logger.error(e);
		}
	}

}
