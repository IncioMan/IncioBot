package inciobot.bot_backend.bot.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

import inciobot.bot_backend.annotations.BotManager;
import inciobot.bot_backend.bot.AbstractBotController;
import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.services.IService;

@BotManager
public class AbstractFifaManager {
	protected AbstractBotController bot;
	protected Update currentUpdate;

	@Autowired
	protected IService service;

	public AbstractBotController getBot() {
		return bot;
	}

	public void setBot(AbstractBotController bot) {
		this.bot = bot;
	}

	public boolean checkValidUser() {
		if (!service.isUserActive(bot.getUserIdentifier(currentUpdate))) {
			return false;
		}
		return true;
	}

	public boolean checkInlineValidUser() {
		if (!service.isUserActive(bot.getUserIdentifierInlineQuery(currentUpdate))) {
			return false;
		}
		return true;
	}

	protected ReplyKeyboardMarkup getKeyBoardFromPlayers(List<Player> players) {
		if (players == null)
			return null;

		List<String> keyboardButtons = new ArrayList<>();
		List<String[]> keyboardsRows = new ArrayList<>();

		int i = 0;
		for (Player p : players) {
			keyboardButtons.add(p.getUser().getUsername());
			if (++i % 2 == 0) {
				String[] keyBoard = new String[keyboardButtons.size()];
				keyBoard = keyboardButtons.toArray(keyBoard);
				keyboardsRows.add(keyBoard);
				keyboardButtons.clear();
			}
		}
		if ((i != 0) && i % 2 != 0) {
			String[] keyBoard = new String[keyboardButtons.size()];
			keyBoard = keyboardButtons.toArray(keyBoard);
			keyboardsRows.add(keyBoard);
		}

		String[][] keyBoard = new String[keyboardsRows.size()][2];
		keyBoard = keyboardsRows.toArray(keyBoard);

		return new ReplyKeyboardMarkup(keyBoard);
	}

	protected ReplyKeyboardMarkup getKeyboardFromArrayString(String[] commands, int buttonPerRow) {
		if (commands == null)
			return null;

		List<String> keyboardButtons = new ArrayList<>();
		List<String[]> keyboardsRows = new ArrayList<>();

		int i = 0;
		for (String s : commands) {
			keyboardButtons.add(s);
			if (++i % buttonPerRow == 0) {
				String[] keyBoard = new String[keyboardButtons.size()];
				keyBoard = keyboardButtons.toArray(keyBoard);
				keyboardsRows.add(keyBoard);
				keyboardButtons.clear();
			}
		}
		if ((i != 0) && i % buttonPerRow != 0) {
			String[] keyBoard = new String[keyboardButtons.size()];
			keyBoard = keyboardButtons.toArray(keyBoard);
			keyboardsRows.add(keyBoard);
		}

		String[][] keyBoard = new String[keyboardsRows.size()][buttonPerRow];
		keyBoard = keyboardsRows.toArray(keyBoard);

		return new ReplyKeyboardMarkup(keyBoard);
	}

	protected boolean isValidOpponent(String opponentUsername) {
		List<Player> activePlayers = service.getActivePlayers();
		return activePlayers.stream().anyMatch(p -> (!p.getUser().getUsername().equals(bot.getUsername(currentUpdate))
				&& p.getUser().getUsername().equals(opponentUsername)));
	}

	protected ReplyKeyboardMarkup getYesOrNoKeyboard() {
		String[] keyBoard = { ICommands.YES, ICommands.NO };
		return new ReplyKeyboardMarkup(keyBoard);
	}
}
