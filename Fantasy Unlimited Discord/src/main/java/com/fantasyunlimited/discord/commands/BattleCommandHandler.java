package com.fantasyunlimited.discord.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.fantasyunlimited.discord.BattleInformation;
import com.fantasyunlimited.discord.BattlePlayerInformation;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.SerializableEmbedBuilder;
import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class BattleCommandHandler extends CommandRequiresAuthenticationHandler {
	public static final String CMD = "battle";
	protected static final Logger logger = Logger.getLogger(BattleCommandHandler.class);

	public BattleCommandHandler(Properties properties) {
		super(properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(event.getAuthor().getLongID());
		PlayerCharacter character = player.getCurrentCharacter();

		if (character == null) {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
					"You don't have an active character, " + getDisplayNameForAuthor(event));
			return;
		}

		Location location = FantasyUnlimited.getInstance().getLocationsBag().getItem(character.getLocationId());
		// sanity
		if (location == null) {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
					"Your character sits in the void. Please contact a developer, " + getDisplayNameForAuthor(event));
			FantasyUnlimited.getInstance()
					.sendExceptionMessage(new IllegalStateException("Player " + getDisplayNameForAuthor(event)
							+ " on Server " + event.getGuild().getName()
							+ " tried to battle but was in a non-valid location '" + character.getLocationId() + "'!"));
			return;
		}
		String stripped = stripCommandFromMessage(event.getMessage());
		// Get option next
		String option = stripOptionFromMessage(stripped).toLowerCase();

		if (option.isEmpty() || option.equals(HandleInit.option)) {
			new HandleInit(location, character).accept(event);
		}

	}

	private class HandleInit implements OptionDescription, Consumer<MessageReceivedEvent> {

		public static final String option = "init";

		private final Location location;
		private final PlayerCharacter character;

		private final Random random;

		public HandleInit(Location location, PlayerCharacter character) {
			this.location = location;
			this.character = character;
			this.random = new Random();
		}

		@Override
		public void accept(MessageReceivedEvent t) {
			BattlePlayerInformation existing = FantasyUnlimited.getInstance().getBattles().get(character.getId());
			BattleInformation information;

			List<BattlePlayer> playerList = new ArrayList<>();

			if (existing != null && existing.getBattle().isFinished() == false) {
				information = existing.getBattle();
				logger.debug("Found existing battle for playerCharacter " + character.getName());
				for (Long id : information.getPlayers().keySet()) {
					playerList.add(information.getPlayers().get(id).getCharacter());
				}
			} else {
				information = new BattleInformation();
				information.setLocation(location);
				int enemyCounter = 0;
				for (HostileNPC hostile : findOpponents(location)) {
					information.getHostiles().put(++enemyCounter, new BattleNPC(hostile));
				}
				if (enemyCounter == 0) {
					FantasyUnlimited.getInstance().sendMessage(t.getChannel(),
							"You didn't find an opportunity to battle, " + getDisplayNameForAuthor(t));
					return;
				}
				BattlePlayer player = new BattlePlayer(character);
				playerList.add(player);

				BattlePlayerInformation playerBattleInfo = new BattlePlayerInformation();
				playerBattleInfo.setBattle(information);
				playerBattleInfo.setCharacter(player);

				information.getPlayers().put(t.getAuthor().getLongID(), playerBattleInfo);
			}

			StringBuilder players = new StringBuilder();
			for (BattlePlayer character : playerList) {
				players.append("```md\n");
				players.append("[" + character.getLevel() + "][" + character.getName() + "]\n");
				players.append(
						"<    Health   : " + character.getCurrentHealth() + "/" + character.getMaxHealth() + ">\n");
				players.append("#    " + character.getCharClass().getEnergyType().toString() + ": "
						+ character.getCurrentAtkResource() + "/" + character.getMaxAtkResource() + "#\n");
				players.append("```");
			}

			StringBuilder enemies = new StringBuilder();
			enemies.append("```md\n");
			for (int index : information.getHostiles().keySet()) {
				BattleNPC npc = information.getHostiles().get(index);
				enemies.append("(" + index + ") [" + npc.getLevel() + "][" + npc.getBase().getName() + "]\n");
				enemies.append("<    Health   : " + npc.getCurrentHealth() + "/" + npc.getMaxHealth() + ">\n");
				enemies.append("#    " + npc.getCharClass().getEnergyType().toString() + ": "
						+ npc.getCurrentAtkResource() + "/" + npc.getMaxAtkResource() + "#\n");
			}
			enemies.append("```");

			embedBuilder = new SerializableEmbedBuilder().withTitle("Battle")
					.appendField("Players (1)", players.toString(), true)
					.appendField("Enemies (" + information.getHostiles().size() + ")", enemies.toString(), true);

			IMessage message = FantasyUnlimited.getInstance().sendMessage(t.getChannel(), embedBuilder.build());
			information.setMessage(message);
			information.setFinished(false);

			for (BattlePlayer character : playerList) {
				IMessage actionbar = FantasyUnlimited.getInstance().sendMessage(t.getChannel(),
						"Here would be your actionbar, <@" + character.getDiscordId() + ">");

				// TODO message information
				BattlePlayerInformation battlePlayerInfo = information.getPlayers().get(character.getDiscordId());
				battlePlayerInfo.setMessage(actionbar);
				FantasyUnlimited.getInstance().getBattles().put(character.getCharacterId(), battlePlayerInfo);
			}

		}

		@Override
		public String getDescription() {
			return "Initializes a battle";
		}

		@Override
		public String getParameter() {
			return "init";
		}

		private List<HostileNPC> findOpponents(Location location) {
			if (location.getHostileNPCIds().isEmpty()) {
				return new ArrayList<>();
			}

			float chance = random.nextFloat();

			if (chance < 0.6f) {
				// only one
				return createOpponentList(1);
			} else if (chance < 0.75f) {
				// two
				return createOpponentList(2);
			} else if (chance < 0.85f) {
				// three
				return createOpponentList(3);
			} else if (chance < 0.92f) {
				// four
				return createOpponentList(4);
			} else {
				// five, holy crêpe
				return createOpponentList(5);
			}
		}

		private List<HostileNPC> createOpponentList(int size) {
			List<String> available = new ArrayList<>(location.getHostileNPCIds());
			List<HostileNPC> picks = new ArrayList<>();
			while (picks.size() < size) {
				if (available.isEmpty()) {
					break;
				}
				int pick = random.nextInt(available.size());
				HostileNPC npc = FantasyUnlimited.getInstance().getHostileNPCBag().getItem(available.get(pick));
				picks.add(npc);

				if (npc.isUnique()) {
					available.remove(npc.getId());
				}
			}
			return picks;
		}
	}

	@Override
	public String getDescription() {
		return "Initiates battles or brings up information about current battles.";
	}

	@Override
	public Type getType() {
		return Type.COMBAT;
	}

}
