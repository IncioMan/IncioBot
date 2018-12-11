package inciobot.bot_backend.bot.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.FifaConversationState;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.services.IService;
import inciobot.bot_backend.utils.fifa.StatisticsUtilBean;

@Component
public class FifaDebugManager extends AbstractFifaManager {

	@Autowired
	private FifaStatisticsManager fifaStatisticsManager;

	@Autowired
	private IService service;

	@Autowired
	private StatisticsUtilBean statisticsUtils;

	public FifaDebugManager() {
	}

	public boolean handleRequest(Update update) {
		currentUpdate = update;

		if (bot.getTextMessage(update).contains(ICommands.DEBUG[6])) {
			sendMessageToActivePlayers(bot.getTextMessage(update));
			return true;
		}

		String command = currentUpdate.getMessage().getText();
		if (command.equals(ICommands.DEBUG[0])) {
			sendListDebugCommands();
			return true;
		}
		if (command.equals(ICommands.DEBUG[1])) {
			sendAllPlayers();
			return true;
		}
		if (command.equals(ICommands.DEBUG[2])) {
			sendAllConversations();
			return true;
		}
		if (command.equals(ICommands.DEBUG[3])) {
			sendAllMatches();
			return true;
		}
		if (command.equals(ICommands.DEBUG[4])) {
			sendWeeklyReport();
			return true;
		}
		if (command.equals(ICommands.DEBUG[5])) {
			regenerateGeneralStats();
			return true;
		}
		return false;
	}

	private void regenerateGeneralStats() {
		statisticsUtils.postConstruct();
	}

	private void sendListDebugCommands() {
		StringBuilder retVal = new StringBuilder();
		retVal.append(IMessage.HI_DEVELOPER + "\n\n");
		for (String command : ICommands.DEBUG) {
			retVal.append(command + "\n");
		}

		bot.sendMessage(bot.getMyChatId(), retVal.toString());
	}

	private void sendMessageToActivePlayers(String command) {
		String message = command.replace(ICommands.DEBUG[6] + " ", "");
		if (StringUtils.isBlank(message))
			return;
		for (Player player : service.getActivePlayers()) {
			bot.sendMessage(player.getChatId().toString(), message);
		}
	}

	private void sendWeeklyReport() {
		fifaStatisticsManager.sendWeeklyReports();
	}

	private void sendAllMatches() {
		List<FifaMatch> matches = service.getAllMatches();

		StringBuilder message = new StringBuilder();
		matches.stream().forEach(m -> message.append(m.getRappresentation(true, true) + "\n"));

		bot.sendMessage(bot.getMyChatId(), message.toString());
	}

	private void sendAllConversations() {
		List<FifaConversationState> allConversations = service.getAllConversations();
		StringBuilder message = new StringBuilder();
		allConversations.stream().forEach(c -> message.append(c.getRappresentation() + "\n\n"));

		bot.sendMessage(bot.getMyChatId(), message.toString());
	}

	private void sendAllPlayers() {
		List<Player> players = service.getPlayers();
		StringBuilder message = new StringBuilder();
		players.stream().forEach(p -> message.append(p.getRappresentation() + "\n"));

		bot.sendMessage(bot.getMyChatId(), message.toString());
	}
}
