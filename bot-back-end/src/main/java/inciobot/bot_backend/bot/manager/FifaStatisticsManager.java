package inciobot.bot_backend.bot.manager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

import inciobot.bot_backend.annotations.AccessPolicy;
import inciobot.bot_backend.annotations.AccessPolicy.ChatType;
import inciobot.bot_backend.annotations.ValidUser;
import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IEmoji;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.enums.FifaConversationSecondStage;
import inciobot.bot_backend.enums.TypeComparison;
import inciobot.bot_backend.enums.TypePlayerStats;
import inciobot.bot_backend.enums.TypeStatistic;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.ChallengeReport;
import inciobot.bot_backend.model.fifa.FifaMatch;
import inciobot.bot_backend.model.fifa.FifaStatisticRequest;
import inciobot.bot_backend.model.fifa.Player;
import inciobot.bot_backend.model.fifa.PlayerStats;
import inciobot.bot_backend.utils.fifa.FifaStatisticsGenerator;

@Component
public class FifaStatisticsManager extends AbstractFifaManager {

	@Autowired
	private FifaStatisticsGenerator fifaStatisticsGenerator;

	@ValidUser
	@AccessPolicy(ChatType.PRIVATE)
	public void handleRequest(Update update) {
		currentUpdate = update;

		switch (bot.getTextLowerCaseMessage(currentUpdate)) {
		case ICommands.GET_STATISTICS:
			getStatisticRequest(currentUpdate, null);
			break;
		default:
			break;
		}
	}

	@ValidUser
	@AccessPolicy(ChatType.PRIVATE)
	public void getStatisticRequest(Update update, FifaConversationSecondStage state) {
		currentUpdate = update;
		service.setFirstStageConversation(bot.getUserIdentifier(update), FifaConversationFirstStage.STATISTICS);

		if (state == null) {
			beginStatisticsHandler();
			return;
		}

		switch (state) {
		case SELECT_STAT_TYPE_FIRST_OPPONENT:
			firstOpponentType(currentUpdate.getMessage().getText());
			break;
		case SELECT_STAT_FIRST_OPPONENT:
			opponentSelectedHandler(currentUpdate.getMessage().getText(),
					FifaConversationSecondStage.SELECT_STAT_FIRST_OPPONENT);
			break;
		case SELECT_STATISTICS_COMPARISON:
			statisticsComparisonHandler();
			break;
		case SELECT_STAT_SECOND_OPPONENT:
			opponentSelectedHandler(currentUpdate.getMessage().getText(),
					FifaConversationSecondStage.SELECT_STAT_SECOND_OPPONENT);
			break;
		case SELECT_STATISTICS_TYPE:
			statisticsTypeHandler();
			break;
		// case STATISTICS_ALL:
		// allStatisticsHandler();
		// break;
		// case STATISTIC_SPECIFIC_USER:
		// specificUserStatisticHandler();
		// break;
		// case SELECT_OPPONENT:
		// opponentSelectedHandler(currentUpdate.getMessage().getText(), false);
		// break;
		default:
			break;
		}

	}

	private void statisticsTypeHandler() {
		String text = currentUpdate.getMessage().getText();
		switch (text) {
		case ICommands.ALL_MATCHES:
			service.setStatRequestType(bot.getUserIdentifier(currentUpdate), TypeStatistic.ALL_MATCHES);
			fifaStatsRequestCompleted();
			break;
		case ICommands.GRID_STATS:
			service.setStatRequestType(bot.getUserIdentifier(currentUpdate), TypeStatistic.GRID);
			fifaStatsRequestCompleted();
			break;
		default:
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.STATISTICS_TYPE_NO_VALID);
			selectStatisticType();
			break;
		}
	}

	private void fifaStatsRequestCompleted() {
		FifaStatisticRequest request = null;

		request = service.setStatRequestCompleted(bot.getUserIdentifier(currentUpdate));
		service.setFirstStageConversation(bot.getUserIdentifier(currentUpdate), FifaConversationFirstStage.START);

		if (request != null)
			processRequest(request);
	}

	private void processRequest(FifaStatisticRequest request) {

		if (request.getComparison().equals(TypeComparison.AGAINST_OPPONENT)) {
			String opponentUsername = request.getOpponent2().getUser().getUsername();
			Integer opponent1id = request.getOpponent1().getUser().getId();

			List<FifaMatch> matches = service.getAllMatches(opponent1id, opponentUsername, true);

			StringBuilder message = new StringBuilder();
			if (matches.isEmpty()) {
				message.append(IMessage.NO_MATCHES_WITH_OPPONENT + " " + opponentUsername);
			} else {
				message.append(request.getOpponent1().getUser().getUsername() + " VS " + opponentUsername);
				message.append("\n\n");

				ChallengeReport report = fifaStatisticsGenerator.getReportFromMatches(opponent1id, opponentUsername,
						matches);
				// message.append(IMessage.WON_MATCHES + ": " + report.getWon()
				// + " " + IEmoji.PARTY[0] + " " + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getWonPercentage()) + "
				// %)" + "\n");
				// message.append(IMessage.DRAW_MATCHES + ": " +
				// report.getDraw() + " " + IEmoji.DIVISION_SYMBOL + " "
				// + " (" + new
				// DecimalFormat("#0.00").format(report.getDrawPercentage()) + "
				// %)" + "\n");
				// message.append(IMessage.LOST_MATCHES + ": " +
				// report.getLost() + " " + IEmoji.ALMOST_CRYING_FACE + " "
				// + " (" + new
				// DecimalFormat("#0.00").format(report.getLostPercentage()) + "
				// %)" + "\n\n");

				message.append(IMessage.WON_MATCHES + ": " + " ("
						+ new DecimalFormat("#0.00").format(report.getWonPercentage()) + " %) " + IEmoji.PARTY[0]
						+ "\n");
				message.append(IMessage.DRAW_MATCHES + ": " + " ("
						+ new DecimalFormat("#0.00").format(report.getDrawPercentage()) + " %) "
						+ IEmoji.DIVISION_SYMBOL + "\n");
				message.append(IMessage.LOST_MATCHES + ": " + " ("
						+ new DecimalFormat("#0.00").format(report.getLostPercentage()) + " %) "
						+ IEmoji.ALMOST_CRYING_FACE + "\n\n");

				// message.append(IMessage.GOAL_SCORED + ": " +
				// report.getGoalsScored() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getGoalsScoredAvg()) + "
				// goal/partita)" + "\n");
				// message.append(IMessage.GOAL_AGAINST + ": " +
				// report.getGoalsAgainst() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getGoalsAgainstAvg()) +
				// " goal/partita)" + "\n");
				// message.append("\n" + IMessage.MATCH_NO_GOAL_AGAINST + ": " +
				// report.getMatchNoGoalAgainst() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getNoGoalAgainstPercentage())
				// + " %)" + "\n");
				// message.append(IMessage.MATCH_NO_SCORE + ": " +
				// report.getMatchNoScore() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getNoScoredPercentage())
				// + " %)" + "\n\n");
				// message.append(IEmoji.FOOTBALL + " " + IMessage.MATCHES +
				// ":\n\n");

				message.append(IMessage.GOAL_SCORED + ": ("
						+ new DecimalFormat("#0.00").format(report.getGoalsScoredAvg()) + " goal/partita)" + "\n");
				message.append(IMessage.GOAL_AGAINST + ": ("
						+ new DecimalFormat("#0.00").format(report.getGoalsAgainstAvg()) + " goal/partita)" + "\n");
				message.append("\n" + IMessage.MATCH_NO_GOAL_AGAINST + ": ("
						+ new DecimalFormat("#0.00").format(report.getNoGoalAgainstPercentage()) + " %)" + "\n");
				message.append(IMessage.MATCH_NO_SCORE + ": ("
						+ new DecimalFormat("#0.00").format(report.getNoScoredPercentage()) + " %)" + "\n\n");

				int i = 0;
				for (FifaMatch m : matches) {
					if (i++ < 20) {
						message.append(m.getRappresentation(false, false) + "\n\n");
					} else {
						i = 0;
						message.append(m.getRappresentation(false, false) + "\n\n");
						bot.sendMessage(bot.getChatId(currentUpdate), message.toString());
						message = new StringBuilder();
					}
				}
			}
			bot.sendMessage(bot.getChatId(currentUpdate), message.toString());
		}
		if (request.getComparison().equals(TypeComparison.GENERAL)) {
			String playerSurname = request.getOpponent1().getUser().getUsername();
			Long playerId = request.getOpponent1().getId();

			PlayerStats report = service.getPlayerStats(playerId, TypePlayerStats.GENERAL);

			StringBuilder message = new StringBuilder();
			if (report.getMatchesSize() == 0) {
				message.append(IMessage.NO_MATCHES_OF_USER + " " + playerSurname);
			} else {
				message.append(IMessage.STATISTICS_OF_USERS + " " + playerSurname);
				message.append("\n\n");

				// message.append(IMessage.WON_MATCHES + ": " + report.getWon()
				// + " " + IEmoji.PARTY[0] + " " + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getWonPercentage()) + "
				// %)" + "\n");
				// message.append(IMessage.DRAW_MATCHES + ": " +
				// report.getDraw() + " " + IEmoji.DIVISION_SYMBOL + " "
				// + " (" + new
				// DecimalFormat("#0.00").format(report.getDrawPercentage()) + "
				// %)" + "\n");
				// message.append(IMessage.LOST_MATCHES + ": " +
				// report.getLost() + " " + IEmoji.ALMOST_CRYING_FACE + " "
				// + " (" + new
				// DecimalFormat("#0.00").format(report.getLostPercentage()) + "
				// %)" + "\n\n");

				message.append(IMessage.WON_MATCHES + ": " + " ("
						+ new DecimalFormat("#0.00").format(report.getWonPercentage()) + " %) " + IEmoji.PARTY[0]
						+ "\n");
				message.append(IMessage.DRAW_MATCHES + ": " + " ("
						+ new DecimalFormat("#0.00").format(report.getDrawPercentage()) + " %) "
						+ IEmoji.DIVISION_SYMBOL + "\n");
				message.append(IMessage.LOST_MATCHES + ": " + " ("
						+ new DecimalFormat("#0.00").format(report.getLostPercentage()) + " %) "
						+ IEmoji.ALMOST_CRYING_FACE + "\n\n");

				// message.append(IMessage.GOAL_SCORED + ": " +
				// report.getGoalsScored() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getGoalsScoredAvg()) + "
				// goal/partita)" + "\n");
				// message.append(IMessage.GOAL_AGAINST + ": " +
				// report.getGoalsAgainst() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getGoalsAgainstAvg()) +
				// " goal/partita)" + "\n");
				// message.append("\n" + IMessage.MATCH_NO_GOAL_AGAINST + ": " +
				// report.getMatchNoGoalAgainst() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getNoGoalAgainstPercentage())
				// + " %)" + "\n");
				// message.append(IMessage.MATCH_NO_SCORE + ": " +
				// report.getMatchNoScore() + " ("
				// + new
				// DecimalFormat("#0.00").format(report.getNoScoredPercentage())
				// + " %)" + "\n\n");

				message.append(IMessage.GOAL_SCORED + ": ("
						+ new DecimalFormat("#0.00").format(report.getGoalsScoredAvg()) + " goal/partita)" + "\n");
				message.append(IMessage.GOAL_AGAINST + ": ("
						+ new DecimalFormat("#0.00").format(report.getGoalsAgainstAvg()) + " goal/partita)" + "\n");
				message.append("\n" + IMessage.MATCH_NO_GOAL_AGAINST + ": ("
						+ new DecimalFormat("#0.00").format(report.getNoGoalAgainstPercentage()) + " %)" + "\n");
				message.append(IMessage.MATCH_NO_SCORE + ": ("
						+ new DecimalFormat("#0.00").format(report.getNoScoredPercentage()) + " %)" + "\n\n");

				// message.append(IEmoji.FOOTBALL + " " + IMessage.MATCHES +
				// ":\n\n");
				// for (FifaMatch match : matches) {
				// message.append(match.getRappresentation(false, false));
				// message.append("\n\n");
				// }
			}
			bot.sendMessage(bot.getChatId(currentUpdate), message.toString());
		}
	}

	private void beginStatisticsHandler() {
		String[] keyBoard = { ICommands.MIE, ICommands.OTHER_USER };
		ReplyKeyboardMarkup replyKeyboardMarkup = getKeyboardFromArrayString(keyBoard, 1);
		replyKeyboardMarkup.resizeKeyboard(true);
		replyKeyboardMarkup.oneTimeKeyboard(true);

		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.WHOSE_STATS,
				replyKeyboardMarkup);

		service.createNewFifaStatisticRequest(bot.getUserIdentifier(currentUpdate));
		service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
				FifaConversationSecondStage.SELECT_STAT_TYPE_FIRST_OPPONENT);
	}

	private void firstOpponentType(String text) {
		switch (text) {
		case ICommands.MIE:
			selectComparisonType();
			service.setFirstOpponentStatisticReq(bot.getUserIdentifier(currentUpdate), bot.getUsername(currentUpdate));
			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.SELECT_STATISTICS_COMPARISON);
			break;
		case ICommands.OTHER_USER:
			specificUserOpponent();
			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.SELECT_STAT_FIRST_OPPONENT);
			break;
		default:
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.STATISTICS_TYPE_NO_VALID);
			beginStatisticsHandler();
			break;
		}
	}

	private void selectComparisonType() {
		String[] commands = { ICommands.GENERAL, ICommands.SPECIFIC_USER };
		ReplyKeyboardMarkup replyKeyboardMarkup = getKeyboardFromArrayString(commands, 1);
		replyKeyboardMarkup.oneTimeKeyboard(true);

		bot.sendMessage(bot.getChatId(currentUpdate), IMessage.AGAINST_WHO, replyKeyboardMarkup);
	}

	private void statisticsComparisonHandler() {
		String text = currentUpdate.getMessage().getText();
		switch (text) {
		case ICommands.GENERAL:
			service.setStatRequestComparisonType(bot.getUserIdentifier(currentUpdate), TypeComparison.GENERAL);
			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.SELECT_STATISTICS_TYPE);
			selectStatisticType();
			break;
		case ICommands.SPECIFIC_USER:
			service.setStatRequestComparisonType(bot.getUserIdentifier(currentUpdate), TypeComparison.AGAINST_OPPONENT);
			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.SELECT_STAT_SECOND_OPPONENT);
			specificUserOpponent();
			break;
		default:
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.STATISTICS_TYPE_NO_VALID);
			selectComparisonType();
			break;
		}
	}

	private void selectStatisticType() {
		String[] commands = { ICommands.ALL_MATCHES, ICommands.GRID_STATS };
		ReplyKeyboardMarkup replyKeyboardMarkup = getKeyboardFromArrayString(commands, 1);
		replyKeyboardMarkup.oneTimeKeyboard(true);

		bot.sendMessage(bot.getChatId(currentUpdate), IMessage.TYPE_STATS, replyKeyboardMarkup);
	}

	private void specificUserOpponent() {
		List<Player> players = service.getOtherActivePlayers(bot.getUserIdentifier(currentUpdate));
		ReplyKeyboardMarkup keyBoard = getKeyBoardFromPlayers(players);
		keyBoard.resizeKeyboard(true);
		keyBoard.oneTimeKeyboard(true);

		bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.SELECT_PLAYER, keyBoard);
	}

	// private void allStatisticsHandler() {
	// List<FifaMatch> matches =
	// service.getAllMatches(bot.getUserIdentifier(currentUpdate), true);
	//
	// StringBuilder message = new StringBuilder();
	// for (FifaMatch match : matches) {
	// message.append(match.getRappresentation(false, false));
	// message.append("\n\n");
	// }
	// bot.sendMessage(bot.getChatId(currentUpdate), message.toString());
	// }

	private void opponentSelectedHandler(String opponentUsername, FifaConversationSecondStage stage) {
		if (!isValidOpponent(opponentUsername)) {
			bot.sendMessage(currentUpdate.getMessage().getChat().getId().toString(), IMessage.USER_SELECTED_NO_VALID);
			specificUserOpponent();
			return;
		}

		if (stage.equals(FifaConversationSecondStage.SELECT_STAT_FIRST_OPPONENT)) {
			service.setFirstOpponentStatisticReq(bot.getUserIdentifier(currentUpdate), opponentUsername);
			selectComparisonType();
			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.SELECT_STATISTICS_COMPARISON);
		}
		if (stage.equals(FifaConversationSecondStage.SELECT_STAT_SECOND_OPPONENT)) {
			service.setSecondOpponentStatisticReq(bot.getUserIdentifier(currentUpdate), opponentUsername);
			selectStatisticType();
			service.setSecondStageConversation(bot.getUserIdentifier(currentUpdate),
					FifaConversationSecondStage.SELECT_STATISTICS_TYPE);
		}
	}

	@ValidUser
	public void handleStatisticGroupRequest(Update update) {
		// currentUpdate = update;
		//
		// String opponentUsername = update.getMessage().getText().replaceAll("
		// ", "");
		// opponentUsername =
		// opponentUsername.replaceAll(ICommands.GET_STATISTICS, "");
		// opponentSelectedHandler(opponentUsername, true);
	}

	public void sendWeeklyReports() {
		service.getActivePlayers().stream().forEach(t -> {
			Integer userIdentifier = t.getUser().getId();
			Calendar today = Calendar.getInstance();
			Calendar from = Calendar.getInstance();
			from.add(Calendar.DAY_OF_MONTH, -7);

			List<FifaMatch> weekMatches = service.getMatches(from.getTime(), today.getTime(), userIdentifier);

			Set<Player> opponents = new HashSet<>();
			Map<Player, List<FifaMatch>> reportMap = new HashMap<>();

			for (FifaMatch m : weekMatches) {
				for (Player p : m.getOpponents(userIdentifier)) {
					List<FifaMatch> matches = null;

					matches = reportMap.get(p);
					if (matches == null) {
						matches = new ArrayList<>();
						reportMap.put(p, matches);
					}

					matches.add(m);
					opponents.add(p);
				}
			}

			StringBuilder retVal = new StringBuilder();
			String fromDate = new SimpleDateFormat("dd/MM/YY").format(from.getTime());
			String toDate = new SimpleDateFormat("dd/MM/YY").format(today.getTime());
			retVal.append(IEmoji.CHAMPION[0] + "<b>" + IMessage.WEEKLY_REPORT + "</b>" + "\n(da " + fromDate + " a "
					+ toDate + ")\n\n");

			if (opponents.isEmpty()) {
				retVal.append(IMessage.NO_MATCH_THIS_WEEK);
			} else {
				ChallengeReport report = fifaStatisticsGenerator.getReportFromMatches(userIdentifier, weekMatches);
				retVal.append("<b>" + "Generali" + "</b>: <b>" + report.getWon() + "</b> (won), <b>" + report.getDraw()
						+ "</b> (draw), <b>" + report.getLost() + "</b> (lost)");
				retVal.append("\n\nContro:");
			}
			for (Player opponent : opponents) {
				ChallengeReport report = fifaStatisticsGenerator.getReportFromMatches(userIdentifier,
						reportMap.get(opponent));

				retVal.append("\n<b>" + opponent.getUser().getUsername() + "</b>: <b>" + report.getWon()
						+ "</b> (won), <b>" + report.getDraw() + "</b> (draw), <b>" + report.getLost() + "</b> (lost)");
			}
			System.out.println(retVal.toString());
			bot.sendMessage(t.getChatId().toString(), retVal.toString(), ParseMode.HTML);

			// bot.sendMessage("92383009", retVal.toString(), ParseMode.HTML);
		});
	}
}
