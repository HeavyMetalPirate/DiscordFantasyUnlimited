package com.fantasyunlimited.discord;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fantasyunlimited.discord.xml.AttributeBonus;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.ClassBonus;
import com.fantasyunlimited.discord.xml.CombatSkillBonus;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.discord.xml.NPC;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.discord.xml.RacialBonus;
import com.fantasyunlimited.discord.xml.SecondarySkillBonus;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.TravelConnection;
import com.fantasyunlimited.discord.xml.Weapon;
import com.thoughtworks.xstream.XStream;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;


public class FantasyUnlimited extends BaseBot {
	private static final Logger logger = Logger.getLogger(FantasyUnlimited.class);
	public static final String PREFIX_KEY = "prefix";
	
	private static FantasyUnlimited INSTANCE;
	
	private IUser owner;
	
	private final Properties properties;
	private final MessageReceivedHandler messageReceivedHandler;
	private final ReactionForSelfAddHandler reactionAddHandler;
	
	private XStream xstream = new XStream();
	private WeaponBag weaponBag = new WeaponBag();
	private EquipmentBag equipmentBag = new EquipmentBag();
	private RaceBag raceBag = new RaceBag();
	private ClassBag classBag = new ClassBag();
	
	private Map<Long, MessageInformation> messagesAwaitingReactions = new HashMap<>();
	
	public FantasyUnlimited(IDiscordClient discordClient, Properties properties) {
		super(discordClient);
		this.properties = properties;
		messageReceivedHandler = new MessageReceivedHandler(discordClient, properties);
		reactionAddHandler = new ReactionForSelfAddHandler(discordClient, properties);
		
		EventDispatcher dispatcher = discordClient.getDispatcher();
		dispatcher.registerListeners(new BotInitializedHandler(), messageReceivedHandler, reactionAddHandler);
				
		INSTANCE = this;
	}	
	
	public void sendExceptionMessage(Throwable e) {
		if(owner == null) {
			owner = client.getUserByID(Long.parseLong(properties.getProperty("owner")));
		}
		
		logger.error(e);
		sendMessage(owner.getOrCreatePMChannel(), "An error occured.");
		StringBuilder builder = new StringBuilder();
		builder.append("```");
		builder.append(e.getClass().getCanonicalName() + ": ");
		builder.append(e.getMessage() + "\n");
		for(StackTraceElement element: e.getStackTrace()) {
			builder.append("\tat " + element.toString() + "\n");
		}
		Throwable next = e.getCause();
		while(next != null) {
			builder.append("Cause:\n");
			builder.append(next.getClass().getCanonicalName() + ": ");
			builder.append(next.getMessage() + "\n");
			for(StackTraceElement element: next.getStackTrace()) {
				builder.append("\tat " + element.toString() + "\n");
			}
			next = e.getCause();
		}
		builder.append("```");
		sendMessage(owner.getOrCreatePMChannel(), builder.toString());
	}
	
	public XStream initializeXStream() {
		xstream.alias("Class", CharacterClass.class);
		xstream.alias("Race", Race.class);
		xstream.alias("ClassBonus", ClassBonus.class);
		xstream.alias("Skill", Skill.class);
		xstream.alias("RacialBonus", RacialBonus.class);
		xstream.alias("Weapon", Weapon.class);
		xstream.alias("Equipment", Equipment.class);
		xstream.alias("AttributeBonus", AttributeBonus.class);
		xstream.alias("CombatSkillBonus", CombatSkillBonus.class);
		xstream.alias("SecondarySkillBonus", SecondarySkillBonus.class);
		xstream.alias("Location", Location.class);
		xstream.alias("TravelConnection", TravelConnection.class);
		xstream.alias("NPC", NPC.class);
		xstream.alias("HostileNPC", HostileNPC.class);
		return xstream;
	}
	
	public static FantasyUnlimited getInstance() {
		return INSTANCE;
	}
	
	public static void autowire(Object bean)  {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).
                getAutowireCapableBeanFactory().
                autowireBean(bean);

	}
	
	public void setPlayingText(String text) {
		client.changePlayingText(text);
	}
	
	public IMessage sendMessage(IChannel channel, String message) {
		try {
			// Builds (sends) and new message in the channel that the original
			// message was sent with the content of the original message.
			return new MessageBuilder(client).withChannel(channel).withContent(message).build();

		} catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
			logger.error("Sending messages too quickly!", e);
			if(INSTANCE != null) INSTANCE.sendExceptionMessage(e);
			throw e;
		} catch (DiscordException e) { // DiscordException thrown. Many ossibilities. Use getErrorMessage() to see what went wrong.
			logger.error(e); // Print the error message sent by Discord
			if(INSTANCE != null) INSTANCE.sendExceptionMessage(e);
			throw e;
		} catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
			logger.error("Missing permissions for channel!", e);
			if(INSTANCE != null) INSTANCE.sendExceptionMessage(e);
			throw e;
		}
	}

	public WeaponBag getWeaponBag() {
		return weaponBag;
	}

	public EquipmentBag getEquipmentBag() {
		return equipmentBag;
	}

	public RaceBag getRaceBag() {
		return raceBag;
	}

	public ClassBag getClassBag() {
		return classBag;
	}

	public Map<Long, MessageInformation> getMessagesAwaitingReactions() {
		return messagesAwaitingReactions;
	}
}
