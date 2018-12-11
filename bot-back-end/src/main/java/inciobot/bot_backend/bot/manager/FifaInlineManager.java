package inciobot.bot_backend.bot.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputMessageContent;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerInlineQuery;

import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.Player;

@Component
public class FifaInlineManager extends AbstractFifaManager {

	public void handleRequest(Update update) {
		currentUpdate = update;

		List<InlineQueryResultArticle> results = new ArrayList<>();

		if (!checkInlineValidUser()) {
			results.add(new InlineQueryResultArticle("no_valid_user", IMessage.REGISTER_TO_ACCESS_FUNCTIONALITY,
					ICommands.REGISTER));
		} else {
			String filter = update.getInlinequery().query();

			List<Player> players = service.getActivePlayers();
			players = players.stream().filter(p -> !p.getUser().getId().equals(bot.getUserIdentifierInlineQuery(update))
					&& p.getUser().getUsername().contains(filter)).collect(Collectors.toList());

			for (Player player : players) {
				InputMessageContent messageContent = new InputTextMessageContent(
						ICommands.GET_STATISTICS + " <b>" + player.getUser().getUsername() + "</b>")
								.parseMode(ParseMode.HTML);
				results.add(new InlineQueryResultArticle(player.getId().toString(), player.getUser().getUsername(),
						messageContent));
			}
		}

		InlineQueryResultArticle[] resultes = new InlineQueryResultArticle[results.size()];
		resultes = results.toArray(resultes);

		bot.sendAnswerInlineQuery(new AnswerInlineQuery(update.getInlinequery().id(), resultes).isPersonal(true)
				.nextOffset("").cacheTime(15));
	}
}
