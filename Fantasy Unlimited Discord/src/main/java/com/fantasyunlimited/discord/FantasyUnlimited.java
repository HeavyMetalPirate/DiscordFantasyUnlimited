package com.fantasyunlimited.discord;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fantasyunlimited.discord.event.BotInitializedHandler;
import com.fantasyunlimited.discord.event.MessageReceivedHandler;
import com.fantasyunlimited.discord.event.ReactionForSelfAddHandler;
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
import com.fantasyunlimited.discord.xml.SecondarySkill;
import com.fantasyunlimited.discord.xml.SecondarySkillBonus;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.SkillRank;
import com.fantasyunlimited.discord.xml.TravelConnection;
import com.fantasyunlimited.discord.xml.Weapon;
import com.fantasyunlimited.discord.xml.items.ClassBag;
import com.fantasyunlimited.discord.xml.items.EquipmentBag;
import com.fantasyunlimited.discord.xml.items.LocationBag;
import com.fantasyunlimited.discord.xml.items.RaceBag;
import com.fantasyunlimited.discord.xml.items.WeaponBag;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.thoughtworks.xstream.XStream;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

public class FantasyUnlimited extends BaseBot {
	private static final Logger logger = Logger.getLogger(FantasyUnlimited.class);
	public static final String PREFIX_KEY = "prefix";

	private static FantasyUnlimited INSTANCE;

	private final CacheManager cacheManager;

	private IUser owner;

	private final Properties properties;
	private final MessageReceivedHandler messageReceivedHandler;
	private final ReactionForSelfAddHandler reactionAddHandler;

	private XStream xstream = new XStream();
	private WeaponBag weaponBag = new WeaponBag();
	private EquipmentBag equipmentBag = new EquipmentBag();
	private RaceBag raceBag = new RaceBag();
	private ClassBag classBag = new ClassBag();
	private LocationBag locationsBag = new LocationBag();

	private Map<Long, MessageInformation> messagesAwaitingReactions = new HashMap<>();

	public FantasyUnlimited(IDiscordClient discordClient, Properties properties) {
		super(discordClient);
		INSTANCE = this;
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("registeredUsersPlaying", 
						CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, DiscordPlayer.class, 
								ResourcePoolsBuilder.heap(10000).build()
						)
						.withExpiry(
								ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(15))
						).build()
				).build();
		cacheManager.init();
		
		this.properties = properties;
		messageReceivedHandler = new MessageReceivedHandler(properties);
		reactionAddHandler = new ReactionForSelfAddHandler(properties);
		
		EventDispatcher dispatcher = discordClient.getDispatcher();
		dispatcher.registerListeners(new BotInitializedHandler(), messageReceivedHandler, reactionAddHandler);
	}

	public Cache<Long, DiscordPlayer> getRegisteredUserCache() {
		return cacheManager.getCache("registeredUsersPlaying", Long.class, DiscordPlayer.class);
	}
	
	public MessageReceivedHandler getMessageReceivedHandler() {
		return messageReceivedHandler;
	}

	public void sendExceptionMessage(Throwable e) {
		if (owner == null) {
			owner = client.getUserByID(Long.parseLong(properties.getProperty("owner")));
		}

		logger.error(e);
		sendMessage(owner.getOrCreatePMChannel(), "An error occured.");
		StringBuilder builder = new StringBuilder();
		builder.append("```");
		builder.append(e.getClass().getCanonicalName() + ": ");
		builder.append(e.getMessage() + "\n");
		for (StackTraceElement element : e.getStackTrace()) {
			builder.append("\tat " + element.toString() + "\n");
		}
		Throwable next = e.getCause();
		while (next != null) {
			builder.append("Cause:\n");
			builder.append(next.getClass().getCanonicalName() + ": ");
			builder.append(next.getMessage() + "\n");
			int stackelements = 0;
			for (StackTraceElement element : next.getStackTrace()) {
				builder.append("\tat " + element.toString() + "\n");
				stackelements++;
				if (stackelements == 50) {
					break;
				}
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
		xstream.alias("SkillRank", SkillRank.class);
		xstream.alias("RacialBonus", RacialBonus.class);
		xstream.alias("Weapon", Weapon.class);
		xstream.alias("Equipment", Equipment.class);
		xstream.alias("AttributeBonus", AttributeBonus.class);
		xstream.alias("CombatSkillBonus", CombatSkillBonus.class);
		xstream.alias("SecondarySkill", SecondarySkill.class);
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

	public static void autowire(Object bean) {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		ServletContext servletContext = (ServletContext) externalContext.getContext();
		WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getAutowireCapableBeanFactory()
				.autowireBean(bean);

	}

	public void setPlayingText(String text) {
		client.changePlayingText(text);
	}

	public IMessage sendMessage(IChannel channel, String message) {
		return RequestBuffer.request(() -> {
			return new MessageBuilder(client).withChannel(channel).withContent(message).build();
		}).get();
	}

	public IMessage sendMessage(IChannel channel, EmbedObject message) {
		return RequestBuffer.request(() -> {
			return new MessageBuilder(client).withChannel(channel).withEmbed(message).build();
		}).get();
	}

	public IMessage editMessage(IMessage message, String content) {
		return RequestBuffer.request(() -> {
			return message.edit(content);
		}).get();
	}
	
	public IMessage editMessage(IMessage message, EmbedObject embed) {
		return RequestBuffer.request(() -> {
			return message.edit(embed);
		}).get();
	}

	public IMessage addReactions(final IMessage message, ReactionEmoji... emojis) {
		new Thread(() -> {
			for (ReactionEmoji emoji : emojis) {
				RequestBuffer.request(() -> {
					message.addReaction(emoji);
				}).get();
			}
		}).start();
		return message;
	}

	public IMessage addReactions(final IMessage message, String... emojiUnicodes) {
		new Thread(() -> {
			for (String emoji : emojiUnicodes) {
				RequestBuffer.request(() -> {
					message.addReaction(ReactionEmoji.of(emoji));
				}).get();
			}
		}).start();
		return message;
	}

	public IMessage addCustomReactions(final IMessage message, Map<String, Long> customEmojis) {
		new Thread(() -> {
			for (String emoji : customEmojis.keySet()) {
				RequestBuffer.request(() -> {
					message.addReaction(ReactionEmoji.of(emoji, customEmojis.get(emoji)));
				}).get();
			}
		}).start();
		return message;
	}

	public IMessage removeReactionForUser(final IMessage message, IReaction reaction, IUser user) {
		new Thread(() -> {
			RequestBuffer.request(() -> {
				message.removeReaction(user, reaction);
			});
		}).start();
		return message;
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

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public LocationBag getLocationsBag() {
		return locationsBag;
	}
}
