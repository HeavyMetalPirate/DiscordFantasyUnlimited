package com.fantasyunlimited.discord.reactions;

import java.util.List;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.Unicodes;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.util.RequestBuffer;

public abstract class PaginationHandler extends ReactionsHandler {
	public static final String VARNAME = "paginatorValues";
	
	public PaginationHandler(Properties properties) {
		super(properties);
	}

	public abstract void doDelegate(ReactionAddEvent event);

	@Override
	public void handle(ReactionAddEvent event) {
		MessageInformation information = getInformationSecure(event);
		String emojiName = getEmojiName(event);

		if (emojiName.equals(Unicodes.arrow_backward) == false && emojiName.equals(Unicodes.arrow_forward) == false) {
			doDelegate(event);
			return;
		}

		// Clear all reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeReaction(event.getUser(), event.getReaction());
		});

		@SuppressWarnings("unchecked")
		List<String> values = (List<String>) information.getVars().get(VARNAME);
		int itemsPerPage = information.getStatus().getItemsPerPage();
		double maxPage = Math.ceil(values.size() / (double) itemsPerPage);
		int currentPage = information.getStatus().getCurrentPage();

		StringBuilder builder = new StringBuilder();
		builder.append("```\n");

		if (emojiName.equals(Unicodes.arrow_backward)) {
			// backwards operation
			if (currentPage == 1) {
				return; // sanity check, shouldn't happen really
			}
			currentPage -= 1;
			information.getStatus().setCurrentPage(currentPage);

			int breakCondition = itemsPerPage * currentPage >= values.size() ? values.size()
					: itemsPerPage * currentPage;
			for (int i = itemsPerPage * currentPage - itemsPerPage; i < breakCondition; i++) {
				builder.append(values.get(i) + "\n");
			}

		} else if (emojiName.equals(Unicodes.arrow_forward)) {
			// forward operation
			if (currentPage == maxPage) {
				return; // shouldn't really happen
			}
			currentPage += 1;
			information.getStatus().setCurrentPage(currentPage);

			int breakCondition = itemsPerPage * currentPage >= values.size() ? values.size()
					: itemsPerPage * currentPage;
			for (int i = itemsPerPage * currentPage - itemsPerPage; i < breakCondition; i++) {
				builder.append(values.get(i) + "\n");
			}

		}
		builder.append("```");

		FantasyUnlimited.getInstance().editMessage(information.getMessage(), builder.toString());
		if (currentPage == 1) {
			FantasyUnlimited.getInstance().addReactions(information.getMessage(), Unicodes.arrow_forward);
		} else if (currentPage == maxPage) {
			FantasyUnlimited.getInstance().addReactions(information.getMessage(), Unicodes.arrow_backward);
		} else {
			FantasyUnlimited.getInstance().addReactions(information.getMessage(), Unicodes.arrow_backward,
					Unicodes.arrow_forward);
		}
	}

}
