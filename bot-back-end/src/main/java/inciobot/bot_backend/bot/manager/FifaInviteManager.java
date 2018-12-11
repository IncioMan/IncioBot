package inciobot.bot_backend.bot.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

import inciobot.bot_backend.annotations.AccessPolicy;
import inciobot.bot_backend.annotations.ValidUser;
import inciobot.bot_backend.annotations.AccessPolicy.ChatType;
import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.enums.FifaConversationSecondStage;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.InvitationToPlay;
import inciobot.bot_backend.model.fifa.Player;

@Component
public class FifaInviteManager extends AbstractFifaManager {

	private static final String DATE_FORMAT = "dd-MM-yyyy";
	private static final String HOUR_FORMAT = "HH:mm";

	@AccessPolicy(ChatType.PRIVATE)
	@ValidUser
	public void handleRequest(Update update) {
		currentUpdate = update;

		switch (bot.getTextLowerCaseMessage(currentUpdate)) {
		case ICommands.INVITE_TO_PLAY:
			inviteToPlayRequest(currentUpdate, null);
			break;
		default:
			break;
		}
	}

	@AccessPolicy(ChatType.PRIVATE)
	@ValidUser
	public void inviteToPlayRequest(Update update, FifaConversationSecondStage secondStageState) {
		currentUpdate = update;
		service.setFirstStageConversation(bot.getUserIdentifier(update), FifaConversationFirstStage.INVITE_TO_PLAY);

		if (secondStageState == null) {
			beginInvitehHandler();
			return;
		}

		switch (secondStageState) {
		case SELECT_OPPONENT:
			selectOpponentHandler();
			break;
		case INSERT_DATE:
			insertDatatHandler();
			break;
		case INSERT_HOUR:
			insertHourHandler();
			break;
		case INSERT_COMMENT:
			insertCommentHandler();
			break;
		default:
			break;
		}
	}

	private void beginInvitehHandler() {
		List<Player> players = service.getOtherActivePlayers(bot.getUserIdentifier(currentUpdate));

		ReplyKeyboardMarkup keyBoard = getKeyBoardFromPlayers(players);
		keyBoard.resizeKeyboard(true);
		keyBoard.oneTimeKeyboard(true);

		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.SELECT_INVITATION_RECEIVER,
				keyBoard);

		service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
				FifaConversationSecondStage.SELECT_OPPONENT);
		service.createNewInviteToPlay(bot.getUserIdentifier(currentUpdate));
	}

	private void selectOpponentHandler() {
		String receiverUsername = currentUpdate.getMessage().getText();
		if (isValidOpponent(receiverUsername)) {
			// avoid queuing invitations for a player that would make it
			// difficult to confirm all of them
			if (service.isUserInState(receiverUsername, FifaConversationFirstStage.INVITATION_TO_CONFIRM)) {
				bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(),
						IMessage.USER + " " + receiverUsername + " " + IMessage.RECEIVER_NOT_AVAILABLE);
			} else {

				bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(),
						IMessage.INSERT_DATE + DATE_FORMAT);
				service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
						FifaConversationSecondStage.INSERT_DATE);
				service.setReceiverIntoInvitation(bot.getUserIdentifier(currentUpdate), receiverUsername);
				return;
			}
		}
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.OPPONENT_NON_VALID);
		beginInvitehHandler();
	}

	private void insertDatatHandler() {
		Date date = null;
		String result = bot.getTextLowerCaseMessage(currentUpdate);
		result = result.replace(" ", "");

		if (!result.matches("(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-(20[0-9]{2})")) {
			invalidDateInserted();
			return;
		}

		try {
			date = new SimpleDateFormat(DATE_FORMAT).parse(result);
		} catch (ParseException e) {
			invalidDateInserted();
			return;
		}

		service.setInvitationDate(bot.getUserIdentifier(currentUpdate), date);

		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.INSERT_HOUR + HOUR_FORMAT);

		service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
				FifaConversationSecondStage.INSERT_HOUR);
		return;
	}

	private void invalidDateInserted() {
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.DATA_NOT_VALID);
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.INSERT_DATE + DATE_FORMAT);
	}

	private void insertHourHandler() {
		String result = bot.getTextLowerCaseMessage(currentUpdate);
		result = result.replace(" ", "");

		if (!result.matches("(0[0-9]|1[0-9]|2[0-4]):([0-5][0-9])")) {
			invalidHourInserted();
			return;
		}

		try {
			new SimpleDateFormat(HOUR_FORMAT).parse(result);
		} catch (ParseException e) {
			invalidHourInserted();
			return;
		}

		service.setInvitationHour(bot.getUserIdentifier(currentUpdate), result);

		String[] keyboard = { ICommands.NO_COMMENT };
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(),
				IMessage.ADD_COMMENT_OR_CLICK_BUTTON_INVITATION, keyboardMarkup);

		service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
				FifaConversationSecondStage.INSERT_COMMENT);
		return;
	}

	private void invalidHourInserted() {
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.HOUR_NOT_VALID);
		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.INSERT_HOUR + HOUR_FORMAT);

	}

	private void insertCommentHandler() {
		if (!bot.getTextLowerCaseMessage(currentUpdate).equals(ICommands.NO_COMMENT.toLowerCase())) {
			service.addInvitationComment(bot.getUserIdentifier(currentUpdate), currentUpdate.getMessage().getText());
		}
		InvitationToPlay invitationToPlay = service.setInvitationCompleted(bot.getUserIdentifier(currentUpdate));
		invitationCompleted(invitationToPlay);
	}

	private void invitationCompleted(InvitationToPlay invitationToPlay) {
		if (invitationToPlay == null)
			return;

		bot.sendMessage(bot.getChatId(currentUpdate), IMessage.INVITATION_SENT);

		bot.sendMessage(invitationToPlay.getReceiver().getChatId().toString(),
				IMessage.NEW_INVITATION_RECEIVED + "\n\n" + invitationToPlay.getRappresentation());
		bot.sendMessage(invitationToPlay.getReceiver().getChatId().toString(), IMessage.ACCEPT_OR_NOT,
				getYesOrNoKeyboard());

		service.setFirstStageConversation(invitationToPlay.getReceiver().getUser().getId(),
				FifaConversationFirstStage.INVITATION_TO_CONFIRM);
		service.setFirstStageConversation(bot.getUserIdentifier(currentUpdate), FifaConversationFirstStage.START);
	}

	@AccessPolicy(ChatType.PRIVATE)
	@ValidUser
	public void invitationResponseHandler(Update update, FifaConversationSecondStage secondStage) {
		String reply = "";
		currentUpdate = update;

		if (currentUpdate == null)
			return;

		if (bot.getUserIdentifier(currentUpdate) == null) {
			return;
		}

		reply = bot.getTextLowerCaseMessage(currentUpdate);
		if (!reply.equals(ICommands.YES.toLowerCase()) && !reply.equals(ICommands.NO.toLowerCase())) {
			bot.sendMessage(bot.getChatId(currentUpdate), IMessage.INVALID_INVITATION_ANSWER);
			return;
		}

		InvitationToPlay invitation = service.setResponseToInvitation(bot.getUserIdentifier(currentUpdate),
				bot.getTextLowerCaseMessage(currentUpdate));

		if (invitation != null) {
			bot.sendMessage(invitation.getCreator().getChatId().toString(),
					IMessage.USER + " " + invitation.getReceiver().getUser().getUsername() + " "
							+ IMessage.REPLIED_INVITATION + " \"" + reply + "\" " + IMessage.TO_INVITATION + "\n\n"
							+ invitation.getRappresentation());
		}

		if (reply.equals(ICommands.YES.toLowerCase())) {
			bot.sendMessage(bot.getChatId(currentUpdate), IMessage.INVALID_ACCEPTED);
		} else if (reply.equals(ICommands.NO.toLowerCase())) {
			bot.sendMessage(bot.getChatId(currentUpdate), IMessage.INVALID_REFUSED);
		}

		service.setFirstStageConversation(bot.getUserIdentifier(currentUpdate), FifaConversationFirstStage.START);
	}
}
