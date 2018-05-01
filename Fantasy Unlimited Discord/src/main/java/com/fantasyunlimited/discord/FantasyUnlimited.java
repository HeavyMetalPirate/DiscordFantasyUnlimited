package com.fantasyunlimited.discord;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fantasyunlimited.cache.DiscordPlayerLoaderWriter;
import com.fantasyunlimited.cache.KryoSerializer;
import com.fantasyunlimited.cache.MessageInformationEventHandler;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.event.BotInitializedHandler;
import com.fantasyunlimited.discord.event.MessageReceivedHandler;
import com.fantasyunlimited.discord.event.ReactionForSelfAddHandler;
import com.fantasyunlimited.discord.xml.*;
import com.fantasyunlimited.discord.xml.items.*;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.thoughtworks.xstream.XStream;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

public class FantasyUnlimited extends BaseBot {
	private static final Logger logger = Logger.getLogger(FantasyUnlimited.class);
	public static final String PREFIX_KEY = "prefix";

	private static FantasyUnlimited INSTANCE;

	private final PersistentCacheManager cacheManager;

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
	private HostileNPCBag hostileNPCBag = new HostileNPCBag();

	public FantasyUnlimited(IDiscordClient discordClient, Properties properties) {
		super(discordClient);
		this.properties = properties;
		messageReceivedHandler = new MessageReceivedHandler(properties);
		reactionAddHandler = new ReactionForSelfAddHandler(properties);

		EventDispatcher dispatcher = discordClient.getDispatcher();
		dispatcher.registerListeners(new BotInitializedHandler(), messageReceivedHandler, reactionAddHandler);
		
		INSTANCE = this;
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence("F:\\Java\\cache"))
				.withCache("registeredUsersPlaying", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(Long.class, DiscordPlayer.class,
								ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1000, EntryUnit.ENTRIES)
										.offheap(5, MemoryUnit.MB).disk(50, MemoryUnit.MB, true).build())
						.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(15)))
						.withLoaderWriter(new DiscordPlayerLoaderWriter())
						.add(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 3)
								.queueSize(3).concurrencyLevel(1).enableCoalescing())
						.build())
				.withCache("messagesAwaitingReaction",
						CacheConfigurationBuilder
								.newCacheConfigurationBuilder(Long.class, MessageInformation.class,
										ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1000, EntryUnit.ENTRIES)
												.offheap(5, MemoryUnit.MB).disk(50, MemoryUnit.MB, true).build())
								.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(60))).build())
				.withCache("battles",
						CacheConfigurationBuilder
								.newCacheConfigurationBuilder(Long.class, BattlePlayerInformation.class,
										ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1000, EntryUnit.ENTRIES)
												.offheap(5, MemoryUnit.MB).disk(50, MemoryUnit.MB, true).build())
								.withValueSerializer(new KryoSerializer<>(null))
								.withKeySerializer(new KryoSerializer<>(null))
								.withExpiry(ExpiryPolicyBuilder.noExpiration()).build())
				.build();
		cacheManager.init();

		MessageInformationEventHandler listener = new MessageInformationEventHandler();
		cacheManager.getCache("messagesAwaitingReaction", Long.class, MessageInformation.class)
				.getRuntimeConfiguration().registerCacheEventListener(listener, EventOrdering.UNORDERED,
						EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED,
								EventType.REMOVED, EventType.UPDATED));

	}

	public Cache<Long, DiscordPlayer> getRegisteredUserCache() {
		return cacheManager.getCache("registeredUsersPlaying", Long.class, DiscordPlayer.class);
	}

	public Cache<Long, MessageInformation> getMessagesAwaitingReactions() {
		return cacheManager.getCache("messagesAwaitingReaction", Long.class, MessageInformation.class);
	}

	public Cache<Long, BattlePlayerInformation> getBattles() {
		return cacheManager.getCache("battles", Long.class, BattlePlayerInformation.class);
	}

	public MessageReceivedHandler getMessageReceivedHandler() {
		return messageReceivedHandler;
	}

	public void sendExceptionMessage(Throwable e) {
		if (owner == null) {
			owner = client.getUserByID(Long.parseLong(properties.getProperty("owner")));
		}

		logger.error("Error occured: ", e);
		sendMessage(owner.getOrCreatePMChannel(), "An error occured.");
		String builder = "";
		builder += ("```");
		builder += (e.getClass().getCanonicalName() + ": ");
		builder += (e.getMessage() + "\n");
		for (StackTraceElement element : e.getStackTrace()) {
			builder += ("\tat " + element.toString() + "\n");
		}
		Throwable next = e.getCause();
		while (next != null) {
			builder += ("Cause:\n");
			builder += (next.getClass().getCanonicalName() + ": ");
			builder += (next.getMessage() + "\n");
			int stackelements = 0;
			for (StackTraceElement element : next.getStackTrace()) {
				builder += ("\tat " + element.toString() + "\n");
				stackelements++;
				if (stackelements == 15) {
					break;
				}
			}
			next = next.getCause();
		}
		builder += ("```");
		sendMessage(owner.getOrCreatePMChannel(), builder);
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

	public IUser fetchUser(long id) {
		return RequestBuffer.request(() -> {
			return client.fetchUser(id);
		}).get();
	}

	public IMessage fetchMessage(long guildId, long channelId, long messageId) {
		IGuild guild = RequestBuffer.request(() -> {
			return client.getGuildByID(guildId);
		}).get();

		IChannel channel = RequestBuffer.request(() -> {
			return guild.getChannelByID(channelId);
		}).get();

		return RequestBuffer.request(() -> {
			return channel.getMessageByID(messageId);
		}).get();
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

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public LocationBag getLocationsBag() {
		return locationsBag;
	}

	public HostileNPCBag getHostileNPCBag() {
		return hostileNPCBag;
	}

	public void setHostileNPCBag(HostileNPCBag hostileNPCBag) {
		this.hostileNPCBag = hostileNPCBag;
	}
}
