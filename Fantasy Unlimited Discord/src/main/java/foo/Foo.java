package foo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.SkillRank;
import com.fantasyunlimited.discord.xml.Weapon;
import com.fantasyunlimited.cache.DiscordPlayerLoaderWriter;
import com.fantasyunlimited.discord.BattlePlayerInformation;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageFormatUtils;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.xml.AttributeBonus;
import com.fantasyunlimited.discord.xml.Attributes;
import com.fantasyunlimited.discord.xml.Attributes.Attribute;
import com.fantasyunlimited.discord.xml.CharacterClass.EnergyType;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.ClassBonus;
import com.fantasyunlimited.discord.xml.CombatSkill;
import com.fantasyunlimited.discord.xml.CombatSkillBonus;
import com.fantasyunlimited.discord.xml.Consumable;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.EquipmentType;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.ItemRarity;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.discord.xml.NPC;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.discord.xml.RacialBonus;
import com.fantasyunlimited.discord.xml.SecondarySkill;
import com.fantasyunlimited.discord.xml.SecondarySkillBonus;
import com.fantasyunlimited.discord.xml.Skill.SkillType;
import com.fantasyunlimited.discord.xml.TravelConnection;
import com.fantasyunlimited.discord.xml.Weapon.Hand;
import com.fantasyunlimited.discord.xml.Weapon.WeaponType;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.fantasyunlimited.logic.DiscordPlayerLogic;
import com.thoughtworks.xstream.XStream;

public class Foo {

	public static void main(String[] args) throws IOException, URISyntaxException {
//		System.out.println(MessageFormatUtils.fillStringPrefix("Dargon Woraw", 50) + "*");
//		System.out.println(MessageFormatUtils.fillStringSuffix("Dargon Woraw", 50) + "*");
//		
//		
//		URL url = Foo.class.getClassLoader().getResource("hostiles");
//		listFiles(Paths.get(url.toURI()));
		elementGeneration();
		
//		cacheTest();
	}

	static void listFiles(Path path) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					listFiles(entry);
				}
				System.out.println(entry);
			}
		}
	}

	private static void elementGeneration() {
		// TODO Auto-generated method stub
		XStream xstream = new XStream();
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
		xstream.alias("Consumable", Consumable.class);

		Consumable consumable = new Consumable();
		consumable.setId("smallHealthPot");
		consumable.setName("Small Health Potion");
		consumable.setIconName("smallHealthpot.jpg");
		consumable.setAtkResourceRestored(0);
		consumable.setHealthRestored(30);
		xstream.toXML(consumable, System.out);

		System.out.println(
				"\n=========================================================================================\n");
		
		HostileNPC hostileNPC = new HostileNPC();
		hostileNPC.setId("dargon");
		hostileNPC.setName("Dargon Woraw");
		hostileNPC.setDescription(
				"Coming from the very slavic land of Massachusetts, he's here to kick ass and chew gum... and he's all out of ass.");
		hostileNPC.setIconName("dargon.png");
		hostileNPC.setLevel(1);
		hostileNPC.setClassId("1");
		hostileNPC.setRaceId("1");
		hostileNPC.setUnique(false);
		hostileNPC.getLoottable().put("torn-shirt", 100.0);
		hostileNPC.getLoottable().put("broken-sword", 100.0);
		xstream.toXML(hostileNPC, System.out);

		System.out.println(
				"\n=========================================================================================\n");
		Location location = new Location();
		location.setId("testbox");
		location.setName("Magical Testbox");
		location.setDescription("A small, yet simple box to test stuff.");
		location.setIconName("testbox.png");
		location.setMarketAccess(false);
		location.setGlobalMarketAccess(false);
		location.getAllowedSecondarySkills().add(SecondarySkill.MINING);
		location.getAllowedSecondarySkills().add(SecondarySkill.FISHING);

		TravelConnection connection = new TravelConnection();
		connection.setTargetLocationId("1");
		connection.setDuration(100);
		connection.setToll(9001);
		location.getConnections().add(connection);
		location.getHostileNPCIds().add(hostileNPC.getId());
		xstream.toXML(location, System.out);

		System.out.println(
				"\n=========================================================================================\n");
		Weapon weapon = new Weapon();
		weapon.setId("1");
		weapon.setName("Broken Sword");
		weapon.setDescription("It has seen better days for sure, but at least it is somewhat pointy and rusty...");
		weapon.setIconName("brknSwrd.png");
		weapon.setType(WeaponType.SWORD);
		weapon.setHand(Hand.RIGHT);
		weapon.setMinDamage(1);
		weapon.setMaxDamage(2);

		SecondarySkillBonus bonus = new SecondarySkillBonus();
		bonus.setSkill(SecondarySkill.WOODCUTTING);
		bonus.setBonus(5);
		weapon.getSecondarySkillBonuses().add(bonus);

		weapon.setRarity(ItemRarity.ARTIFACT);

		xstream.toXML(weapon, System.out);
		System.out.println(
				"\n=========================================================================================\n");

		Equipment equipment = new Equipment();
		equipment.setId("1");
		equipment.setName("Torn shirt");
		equipment
				.setDescription("Better than nothing, huh? If you wear it wrong, puts one of your nipples on display.");
		equipment.setIconName("trnshrt.png");
		equipment.setType(EquipmentType.CHEST);
		equipment.setArmor(0);
		equipment.setRarity(ItemRarity.ARTIFACT);

		CombatSkillBonus skillBonus = new CombatSkillBonus();
		skillBonus.setSkill(CombatSkill.DODGE);
		skillBonus.setBonus(5);
		equipment.getSkillBonuses().add(skillBonus);

		xstream.toXML(equipment, System.out);
		System.out.println(
				"\n=========================================================================================\n");

		CharacterClass charClass = new CharacterClass();
		charClass.setId("1");
		charClass.setName("Depraved");
		charClass.setIconName("depraved.png");
		charClass.setLore("The depraved is depraved. Depraving, huh?");

		charClass.setEnergyType(EnergyType.RAGE);
		
		charClass.setStartingMainhand("1");
		charClass.setStartingOffhand("-1");
		charClass.setStartingHelmet("-1");
		charClass.setStartingChest("-1");
		charClass.setStartingGloves("-1");
		charClass.setStartingPants("-1");
		charClass.setStartingBoots("-1");
		charClass.setStartingRing1("-1");
		charClass.setStartingRing2("-1");
		charClass.setStartingNeck("-1");

		Attributes attributes = new Attributes();
		attributes.setStrength(1);
		attributes.setStrengthGrowth(0);
		attributes.setEndurance(1);
		attributes.setEnduranceGrowth(0);
		attributes.setDexterity(1);
		attributes.setDexterityGrowth(0);
		attributes.setWisdom(1);
		attributes.setWisdomGrowth(0);
		attributes.setIntelligence(1);
		attributes.setIntelligenceGrowth(0);
		attributes.setDefense(1);
		attributes.setDefenseGrowth(0);
		attributes.setLuck(1);
		attributes.setLuckGrowth(0);
		charClass.setAttributes(attributes);

		Skill standardAttack = new Skill();
		standardAttack.setId("0");
		standardAttack.setName("Standard Attack");
		standardAttack.setDescription("Attack with your mainhand weapon equipped.");
		standardAttack.setIconName("stdAtk.png");

		standardAttack.setAttribute(Attribute.STRENGTH);

		SkillRank rank = new SkillRank();
		rank.setRank(1);
		rank.setCostModifier(0);
		rank.setDamageModifier(0);
		rank.setRequiredAttributeValue(1);
		rank.setRequiredPlayerLevel(1);
		standardAttack.getRanks().add(rank);
		
		SkillRank rank2 = new SkillRank();
		rank2.setRank(2);
		rank2.setCostModifier(0);
		rank2.setDamageModifier(0);
		rank2.setRequiredAttributeValue(2);
		rank2.setRequiredPlayerLevel(1);
		standardAttack.getRanks().add(rank2);
		
		SkillRank rank3 = new SkillRank();
		rank3.setRank(3);
		rank3.setCostModifier(0);
		rank3.setDamageModifier(0);
		rank3.setRequiredAttributeValue(1);
		rank3.setRequiredPlayerLevel(2);
		standardAttack.getRanks().add(rank3);

		standardAttack.setType(SkillType.OFFENSIVE);
		standardAttack.setMinDamage(1);
		standardAttack.setMaxDamage(1);
		charClass.getSkills().add(standardAttack);

		ClassBonus classBonus = new ClassBonus();
		classBonus.setId("1");
		classBonus.setName("Depraved strength");
		classBonus.setName("Raises strength by 5%. Who knew depraved could bear any strength at all?");
		classBonus.setIconName("dprStr.png");
		classBonus.setAttribute(Attribute.STRENGTH);
		classBonus.setModifier(5);
		charClass.getBonuses().add(classBonus);

		xstream.toXML(charClass, System.out);
		System.out.println(
				"\n=========================================================================================\n");

		Race race = new Race();
		race.setId("1");
		race.setName("Pitbull");
		race.setLore("It's a fucking pitbull, what else do you need to know?");

		RacialBonus racbonus = new RacialBonus();
		racbonus.setName("Holy Mother of God");
		racbonus.setDescription(
				"Raises critical chance by 5%. Holy mother of god, this is overpowered. Who allowed this?!");
		racbonus.setIconName("mthgd.png");
		racbonus.setCombatSkill(CombatSkill.CRITICAL);
		racbonus.setModifier(5);

		race.getBonuses().add(racbonus);

		xstream.toXML(race, System.out);
	}
	
	private static void cacheTest() {
		PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence("F:\\Java\\cache2"))
				.withCache("foobar2",
						CacheConfigurationBuilder
								.newCacheConfigurationBuilder(Long.class, DiscordPlayer.class, 
									ResourcePoolsBuilder.newResourcePoolsBuilder()
										.heap(1000, EntryUnit.ENTRIES)
										.offheap(5, MemoryUnit.MB)
										.disk(50, MemoryUnit.MB, true)
										.build()
								)
								.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(15)))
								.withLoaderWriter(new DiscordPlayerLoaderWriter())
								.build()
				)
		.build();
		cacheManager.init();
		
		Cache<Long,DiscordPlayer> cache = cacheManager.getCache("foobar2", Long.class, DiscordPlayer.class);
		
		System.out.println(cache.get(282393030269337600L));
		System.out.println(cache.get(282393030269337600L));
		System.out.println(cache.get(282393030269337600L));
		System.out.println(cache.get(282393030269337600L));
		cacheManager.close();
		
	}
	
	static class SampleLoaderWriter implements CacheLoaderWriter<Long, DiscordPlayer>{
		
		public SampleLoaderWriter() {
		}
		
		@Override
		public DiscordPlayer load(Long key) throws Exception {
			System.out.println("load:" + key);
			DiscordPlayer player = new DiscordPlayer();
			player.setDiscordId("foobar");
			return player;
		}

		@Override
		public void write(Long key, DiscordPlayer value) throws Exception {
			System.out.println("Save!");	
		}

		@Override
		public void delete(Long key) throws Exception {
			System.out.println("delete!");
		}

	}

}
