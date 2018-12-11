package inciobot.bot_backend.bot.manager;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

import inciobot.bot_backend.annotations.AccessPolicy;
import inciobot.bot_backend.annotations.AccessPolicy.ChatType;
import inciobot.bot_backend.annotations.ValidUser;
import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IEmoji;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.enums.FifaConversationSecondStage;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.Player;

@Component
public class FifaMatchesManager extends AbstractFifaManager {

	public FifaMatchesManager() {
	}

	@AccessPolicy(ChatType.PRIVATE)
	@ValidUser
	public void handleRequest(Update update) {
		currentUpdate = update;

		switch (bot.getTextLowerCaseMessage(currentUpdate)) {
		case ICommands.ADD_MATCH:
			addMatchRequest(currentUpdate, null);
			break;
		case ICommands.DELETE_MATCH:
			deleteMatchRequest(currentUpdate, null);
			break;
		default:
			break;
		}

	}

	@AccessPolicy(ChatType.PRIVATE)
	public void addMatchRequest(Update update, FifaConversationSecondStage secondStageState) {
		currentUpdate = update;
		service.setFirstStageConversation(bot.getUserIdentifier(update), FifaConversationFirstStage.ADDMATCH);

		if (secondStageState == null) {
			beginAddMatchHandler();
			return;
		}

		switch (secondStageState) {
		case SELECT_OPPONENT:
			selectOpponentHandler();
			break;
		case INSERT_RESULT:
			insertResultHandler();
			break;
		case INSERT_COMMENT:
			insertCommentHandler();
			break;
		default:
			break;
		}
	}

	public void beginAddMatchHandler() {
		List<Player> players = service.getOtherActivePlayers(bot.getUserIdentifier(currentUpdate));

		ReplyKeyboardMarkup keyBoard = getKeyBoardFromPlayers(players);
		keyBoard.resizeKeyboard(true);
		keyBoard.oneTimeKeyboard(true);

		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.SELECT_MATCH_OPPONENT,
				keyBoard);

		service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
				FifaConversationSecondStage.SELECT_OPPONENT);
		service.createNewFifaMatch(bot.getUserIdentifier(currentUpdate));
	}

	private void selectOpponentHandler() {
		if (isValidOpponent(currentUpdate.getMessage().getText())) {
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.INSERT_RESULT);
			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.INSERT_RESULT);
			service.setOpponentIntoMatch(bot.getUserIdentifier(currentUpdate), currentUpdate.getMessage().getText());
			return;
		}
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.OPPONENT_NON_VALID);
		beginAddMatchHandler();
	}

	private void insertResultHandler() {
		String result = bot.getTextLowerCaseMessage(currentUpdate);
		result = result.replace(" ", "");
		if (result.matches("[0-9]{1,2}\\-[0-9]{1,2}")) {
			String[] goals = result.split("-");

			service.setMatchResult(bot.getUserIdentifier(currentUpdate), Integer.parseInt(goals[0]),
					Integer.parseInt(goals[1]));

			String[] keyboard = { ICommands.NO_COMMENT };
			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(),
					IMessage.ADD_COMMENT_OR_CLICK_BUTTON_MATCH, keyboardMarkup);

			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.INSERT_COMMENT);
			return;
		}
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.RESULT_NON_VALID);
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.INSERT_RESULT);
	}

	private void insertCommentHandler() {
		if (!bot.getTextLowerCaseMessage(currentUpdate).equals(ICommands.NO_COMMENT.toLowerCase())) {
			service.addMatchComment(bot.getUserIdentifier(currentUpdate), currentUpdate.getMessage().getText());
		}
		FifaMatch match = service.setMatchCompleted(bot.getUserIdentifier(currentUpdate));
		matchCompleted(match);
	}

	public void matchCompleted(FifaMatch match) {
		if (match == null || match.getTeam2() == null) {

		}
		for (Player player : match.getTeam2().getPlayers()) {
			bot.sendMessage(player.getChatId().toString(),
					IMessage.NEW_MATCH_ADDED_FROM_USER + ": " + bot.getUsername(currentUpdate));
			bot.sendMessage(player.getChatId().toString(), match.getRappresentation(false, false));
		}
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(),
				IMessage.MATCH_ADDED_SUCCESSFULLY + ".\n" + IMessage.SHARE_PICTURE);
		service.setFirstStageConversation(bot.getUserIdentifier(currentUpdate), FifaConversationFirstStage.START);
		try {
			bot.sendPhoto(currentUpdate.getMessage().getChat().getId().toString(),
					service.getMatchPicture(match, true));
		} catch (Exception e) {
			bot.debugMessage(currentUpdate.getMessage().getChat().getId().toString(),
					"Fifa:\nErrore nel creare la foto, sorry.");
		}
		return;
	}

	@AccessPolicy(ChatType.PRIVATE)
	public void deleteMatchRequest(Update update, FifaConversationSecondStage secondStageState) {
		currentUpdate = update;
		service.setFirstStageConversation(bot.getUserIdentifier(update), FifaConversationFirstStage.DELETE_MATCH);

		if (secondStageState == null) {
			beginDeleteMatchHandler();
			return;
		}

		switch (secondStageState) {
		case INSERT_MATCH_ID:
			insertMatchIdToDeleteHandler();
			break;
		default:
			break;
		}
	}

	private void beginDeleteMatchHandler() {
		List<FifaMatch> matches = service.getMatchesByCreator(bot.getUserIdentifier(currentUpdate), true);
		if (matches.isEmpty()) {
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.NO_MATCH_TO_DELETE);
			return;
		}

		service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
				FifaConversationSecondStage.INSERT_MATCH_ID);

		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.SELECT_ID_FROM_FOLLOWING);
		StringBuilder message = new StringBuilder();

		int i = 0;
		for (FifaMatch m : matches) {
			if (i++ < 20) {
				message.append(m.getRappresentation(true, false) + "\n\n");
			} else {
				i = 0;
				message.append(m.getRappresentation(true, false) + "\n\n");
				bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), message.toString());
				message = new StringBuilder();
			}
		}
		if (!message.toString().equals("")) {
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), message.toString());
		}
	}

	private void insertMatchIdToDeleteHandler() {
		String id = bot.getTextLowerCaseMessage(currentUpdate);
		id = id.replace(" ", "");
		if (!id.matches("[0-9]{1,4}") || !isMatchToDeleteValid(Long.parseLong(id))) {
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.ID_NON_VALID);
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(),
					IMessage.INSERT_MATCH_ID_TO_DELETE);
			return;
		}

		FifaMatch match = service.deleteMatch(Long.parseLong(id));
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.MATCH_DELETED_SUCCESSFULLY);

		if (match != null) {
			for (Player player : match.getTeam2().getPlayers()) {
				bot.sendMessage(player.getChatId().toString(),
						IMessage.USER + " " + bot.getUsername(currentUpdate) + " " + IMessage.DELETED_MATCH + ":\n"
								+ match.getRappresentation(false, false) + "\n" + IMessage.REGISTRATION_DATE + " "
								+ IEmoji.CALENDAR[0] + " :\n"
								+ new SimpleDateFormat("dd-MM-yyyy").format(match.getDateCreation()));
			}
		}

		service.setFirstStageConversation(bot.getUserIdentifier(currentUpdate), FifaConversationFirstStage.START);
		return;
	}

	private boolean isMatchToDeleteValid(Long id) {
		List<FifaMatch> matches = service.getMatchesByCreator(bot.getUserIdentifier(currentUpdate), true);
		if (!matches.stream().anyMatch(m -> m.getId().equals(id)))
			return false;
		return true;
	}
}
