package com.fantasyunlimited.discord.reactions;

import java.util.Properties;

import com.fantasyunlimited.discord.BattleInformation;
import com.fantasyunlimited.discord.BattlePlayerInformation;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.SerializableEmbedBuilder;
import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.entity.PlayerCharacter;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IEmbed.IEmbedField;
import sx.blah.discord.handle.obj.IMessage;

public class BattleHandler extends ReactionsHandler {

	public BattleHandler(Properties properties) {
		super(properties);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		//find out if the emoji used is a valid skill for the player of this player
		
		MessageInformation msgInfo = getInformationSecure(event);
		PlayerCharacter playerCharacter = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(msgInfo.getOriginator().getLongID()).getCurrentCharacter();
		BattlePlayerInformation battlePlayerInfo = FantasyUnlimited.getInstance().getBattles()
				.get(playerCharacter.getId());
		if (battlePlayerInfo == null) {
			throw new IllegalStateException("Battle Information lost for character " + playerCharacter.getName());
		}
		BattleInformation battleInfo = battlePlayerInfo.getBattle();

		IMessage battleMessage = battleInfo.getMessage();
		IMessage actionbar = msgInfo.getMessage();
		
		FantasyUnlimited.getInstance().editMessage(battleMessage, "This should have edited the main battle message, hopefully");
		FantasyUnlimited.getInstance().editMessage(actionbar, "This should have edited the actionbar used, hopefully");
		
	}
	
	private void calculateAndPrintResults() {
		//TODO
		//probably put this calculation stuff to a seperate utility class? Or even right into the battle information?
	}
	
	private void createEmbedBuilder(BattleInformation information) {
		StringBuilder players = new StringBuilder();
		for (Long id : information.getPlayers().keySet()) {
			BattlePlayer character = information.getPlayers().get(id).getCharacter();
			
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
	}

}
