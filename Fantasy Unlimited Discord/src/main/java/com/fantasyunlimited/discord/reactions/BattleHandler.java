package com.fantasyunlimited.discord.reactions;

import java.util.Properties;

import com.fantasyunlimited.discord.BattleInformation;
import com.fantasyunlimited.discord.BattlePlayerInformation;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.entity.PlayerCharacter;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class BattleHandler extends ReactionsHandler {

	public BattleHandler(Properties properties) {
		super(properties);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		MessageInformation msgInfo = getInformationSecure(event);
		PlayerCharacter playerCharacter = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(msgInfo.getOriginator().getLongID()).getCurrentCharacter();
		BattlePlayerInformation battlePlayerInfo = FantasyUnlimited.getInstance().getBattles()
				.get(playerCharacter.getId());
		if (battlePlayerInfo == null) {
			throw new IllegalStateException("Battle Information lost for character " + playerCharacter.getName());
		}
		BattleInformation battleInfo = battlePlayerInfo.getBattle();

		FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
				"Found your battle! Hah! Also: \n" + battleInfo.getMessage().getEmbeds().toString());
	}

}
