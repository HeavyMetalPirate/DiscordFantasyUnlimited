package com.fantasyunlimited.discord.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;

import com.fantasyunlimited.discord.BattleInformation;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class BattleCommandHandler extends CommandRequiresAuthenticationHandler {
	public static final String CMD = "battle";

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
			BattleInformation information = new BattleInformation();
			information.setLocation(location);
			information.getPlayers().put(t.getAuthor().getLongID(), character);
			int enemyCounter = 0;
			for (HostileNPC hostile : findOpponents(location)) {
				information.getHostiles().put(++enemyCounter, hostile);
			}
			if (enemyCounter == 0) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(),
						"You didn't find an opportunity to battle, " + getDisplayNameForAuthor(t));
				return;
			}

			FantasyUnlimited.getInstance().sendMessage(t.getChannel(),
					"You would have to face off against " + enemyCounter
							+ " enemies, but I'm too lazy to implement this so you got away lucky this time, "
							+ getDisplayNameForAuthor(t));
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

			// copy the available list

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
