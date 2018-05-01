package com.fantasyunlimited.discord.reactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.SerializableEmbedBuilder;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.commands.CommandSupportsPaginatorHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public abstract class PaginationHandler extends ReactionsHandler {
	public static final String VARNAME = "paginatorValues";

	public PaginationHandler(Properties properties) {
		super(properties);
	}

	/**
	 * <ul>
	 * <li>Boolean value whether the paginator needs to be added after
	 * delegation</li>
	 * <li>Array of unicodes if additional emojis need to be added prior</li>
	 * <li>Flag to tell the program if it should continue creating the content
	 * afterwards</li>
	 * 
	 * @param event
	 * @return
	 */
	public abstract Triple<Boolean, String[], Boolean> doDelegate(ReactionAddEvent event);

	@Override
	public void handle(ReactionAddEvent event) {
		MessageInformation information = getInformationSecure(event);
		String emojiName = getEmojiName(event);

		String[] emojisToUse = new String[] {};

		if (emojiName.equals(Unicodes.arrow_backward) == false && emojiName.equals(Unicodes.arrow_forward) == false) {
			Triple<Boolean, String[], Boolean> result = doDelegate(event);
			String[] emojisToAdd = result.getMiddle();
			Boolean resume = result.getRight();

			if (result.getLeft() != null && result.getLeft() == true) {
				// In case the delegate removes all reactions, add them to the
				// end again
				if (emojisToAdd != null) {
					emojisToAdd = ArrayUtils.addAll(emojisToAdd, Unicodes.arrow_backward, Unicodes.arrow_forward);
				} else {
					emojisToAdd = new String[] { Unicodes.arrow_backward, Unicodes.arrow_forward };
				}
			}

			if (emojisToAdd != null && emojisToAdd.length > 0 && (resume == null || resume == false)) {
				FantasyUnlimited.getInstance().addReactions(information.getMessage(), emojisToAdd);
			}
			if (resume == null || resume == false) {
				return;
			}
		}

		// Clear user reactions
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

		} else if (emojiName.equals(Unicodes.arrow_forward)) {
			// forward operation
			if (currentPage == maxPage) {
				return; // shouldn't really happen
			}
			currentPage += 1;
			information.getStatus().setCurrentPage(currentPage);
		}
		int breakCondition = itemsPerPage * currentPage >= values.size() ? values.size() : itemsPerPage * currentPage;
		int itemCounter = 0;
		for (int i = itemsPerPage * currentPage - itemsPerPage; i < breakCondition; i++) {
			builder.append(values.get(i) + "\n");
			itemCounter++;
		}
		builder.append("```");

		Boolean useEmbed = (Boolean) information.getVars().get(CommandSupportsPaginatorHandler.EMBED_VAR);
		String pagesString = "Page " + currentPage + " of " + (int) maxPage;

		if (useEmbed == null || useEmbed == false) {
			builder.append("\n" + pagesString);
			FantasyUnlimited.getInstance().editMessage(information.getMessage(), builder.toString());
		} else {
			if(information.getVars().get("embedBuilder") != null) {
				embedBuilder = 	(SerializableEmbedBuilder)information.getVars().get("embedBuilder");
				information.getVars().remove("embedBuilder"); //use it then remove it for sanity reasons
				//this forces the developer to take care of which embed builder he uses
				//else we end up with a stale instance instead
			}
			else if(embedBuilder == null) {
				embedBuilder = new SerializableEmbedBuilder();
			}
			embedBuilder.clearFields(); // clear first, else we end up with each
										// page
			FantasyUnlimited.getInstance().editMessage(information.getMessage(),
					embedBuilder.appendField(pagesString, builder.toString(), false).build());
		}

		//Check if we are in a handler using numbers for selection
		if (information.getVars().get("usedNumbers") != null) {
			RequestBuffer.request(() -> {
				event.getMessage().removeAllReactions();
			}).get();
			
			List<String> allowed = new ArrayList<>(Arrays.asList(Unicodes.numNames.clone()));
			allowed.subList(itemCounter, allowed.size()).clear();
			information.getVars().put("usedNumbers", allowed);
			
			String[] usedNumbers = Unicodes.numNames.clone();
			usedNumbers = ArrayUtils.subarray(usedNumbers, 0, itemCounter);
			emojisToUse = ArrayUtils.addAll(emojisToUse, usedNumbers);

		}
		emojisToUse = ArrayUtils.addAll(emojisToUse, Unicodes.arrow_backward, Unicodes.arrow_forward);
		// In case they got lost somewhere
		FantasyUnlimited.getInstance().addReactions(information.getMessage(), emojisToUse);
	}

}
