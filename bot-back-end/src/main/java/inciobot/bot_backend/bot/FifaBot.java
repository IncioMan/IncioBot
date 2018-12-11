package inciobot.bot_backend.bot;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.request.SendMessage;

import inciobot.bot_backend.bot.manager.FifaDebugManager;
import inciobot.bot_backend.bot.manager.FifaInlineManager;
import inciobot.bot_backend.bot.manager.FifaInviteManager;
import inciobot.bot_backend.bot.manager.FifaMatchesManager;
import inciobot.bot_backend.bot.manager.FifaStatisticsManager;
import inciobot.bot_backend.bot.manager.RegistrationFifaManager;
import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IEmoji;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.enums.FifaConversationFirstStage;
import inciobot.bot_backend.exceptions.GroupChatPolicyViolatedException;
import inciobot.bot_backend.exceptions.NoValidUserException;
import inciobot.bot_backend.exceptions.PrivateChatPolicyViolatedException;
import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.model.fifa.FifaConversationState;
import inciobot.bot_backend.services.IService;

@Controller
@RequestMapping(value = "/${fifa.bot.token}/")
public class FifaBot extends AbstractBotController {
	public static String BOT_NAME = "@EueiFifaBot";

	@Value("${fifa.bot.token}")
	protected void initializeBot(String botToken) {
		super.initializeBot(botToken);
	};

	@Value("${debug.bot.token}")
	protected void initializeDebugBot(String botToken) {
		super.initializeDebugBot(botToken);
	};

	@Value("${my.chat.id}")
	protected void setMyChatId(String id) {
		super.setMyChatId(id);
	}

	@Autowired
	private RegistrationFifaManager registrationFifaManager;
	@Autowired
	private FifaMatchesManager fifaMatchesManager;
	@Autowired
	private FifaDebugManager debugManager;
	@Autowired
	private FifaStatisticsManager fifaStatisticsManager;
	@Autowired
	private FifaInlineManager fifaInlineManager;

	@Autowired
	private FifaInviteManager fifaInviteManager;

	@Autowired
	private IService service;

	private Runnable weeklyUpdate;

	@PostConstruct
	private void initialize() {
		registrationFifaManager.setBot(this);
		fifaMatchesManager.setBot(this);
		debugManager.setBot(this);
		fifaStatisticsManager.setBot(this);
		fifaInlineManager.setBot(this);
		fifaInviteManager.setBot(this);

		weeklyUpdate = this::weeklyUpdate;
		schedulerManager.addTaskToWeekly(weeklyUpdate);
	}

	@PostMapping
	@ResponseBody
	public String telegram(HttpServletRequest request) throws IOException {
		Update update = null;
		FifaConversationState conversationState = null;

		try {
			String body = getBody(request.getReader());

			debugBot.execute(new SendMessage(myChatId, "FIFA " + IEmoji.FOOTBALL + "\n\n" + body));

			Gson gson = new Gson();
			update = gson.fromJson(body.replace("_", ""), Update.class);

			if (update == null || update.getMessage() == null || update.getMessage().getText() == null) {
				bot.execute(new SendMessage(myChatId, body.toString()));
				return "";
			}

			conversationState = service.getConversationState(getUserIdentifier(update));

			// remove @.. to read mess from groups
			if (update.getMessage().getText().contains(BOT_NAME))
				update.getMessage().setText(update.getMessage().getText().replaceAll(BOT_NAME, ""));

			// debug feature
			if (update.getMessage().getChat().getId().toString().equals(myChatId)) {
				if (debugManager.handleRequest(update))
					return "";
			}

			// Has to reply section
			if (conversationState != null) {
				if (conversationState.getFirstStage().equals(FifaConversationFirstStage.INVITATION_TO_CONFIRM)) {
					fifaInviteManager.invitationResponseHandler(update, conversationState.getSecondStage());
					return "";
				}
			}

			// INLINE
			if (update.getInlinequery() != null) {
				fifaInlineManager.handleRequest(update);
				return "";
			}

			if (update.getMessage() == null || update.getMessage().getText() == null) {
				return "";
			}

			if (getTextLowerCaseMessage(update).contains(ICommands.START)) {
				service.setFirstStageConversation(this.getUserIdentifier(update), FifaConversationFirstStage.START);
				sendMessage(update.getMessage().getChat().getId().toString(), IMessage.FIFA_WELCOME);
				return "";
			}

			if (getTextLowerCaseMessage(update).equals(ICommands.REGISTER)
					|| getTextLowerCaseMessage(update).equals(ICommands.DELETE_REGISTRATION)
					|| getTextLowerCaseMessage(update).equals(ICommands.UPDATE_USERNAME)) {
				registrationFifaManager.registrationRequest(update);
				return "";
			}

			if (getTextLowerCaseMessage(update).equals(ICommands.ADD_MATCH)
					|| getTextLowerCaseMessage(update).equals(ICommands.DELETE_MATCH)) {
				fifaMatchesManager.handleRequest(update);
				return "";
			}

			if (getTextLowerCaseMessage(update).equals(ICommands.GET_STATISTICS)) {
				fifaStatisticsManager.handleRequest(update);
				return "";
			}
			if (getTextLowerCaseMessage(update).contains(ICommands.GET_STATISTICS)
					&& !update.getMessage().getChat().getType().equals(Type.Private)) {
				fifaStatisticsManager.handleStatisticGroupRequest(update);
				return "";
			}

			if (getTextLowerCaseMessage(update).equals(ICommands.INVITE_TO_PLAY)) {
				fifaInviteManager.handleRequest(update);
				return "";
			}

			if (getTextLowerCaseMessage(update).equals(ICommands.GET_MATCH_PIC)) {
				bot.execute(new SendMessage(getChatId(update), IMessage.NOT_YET_IMPLEMENTED));
				return "";
			}

			if (conversationState != null) {
				switch (conversationState.getFirstStage()) {
				case REGISTER:
					registrationFifaManager.registrationRequest(update);
					break;
				case ADDMATCH:
					fifaMatchesManager.addMatchRequest(update, conversationState.getSecondStage());
					break;
				case DELETE_MATCH:
					fifaMatchesManager.deleteMatchRequest(update, conversationState.getSecondStage());
					break;
				case STATISTICS:
					fifaStatisticsManager.getStatisticRequest(update, conversationState.getSecondStage());
					break;
				case INVITE_TO_PLAY:
					fifaInviteManager.inviteToPlayRequest(update, conversationState.getSecondStage());
					break;
				default:
					break;
				}
			}

		} catch (Exception e) {
			if (e.getCause() instanceof PrivateChatPolicyViolatedException) {
				bot.execute(new SendMessage(getChatId(update), IMessage.ACTION_ONLY_PRIVATE_CHAT));
			}
			if (e.getCause() instanceof GroupChatPolicyViolatedException) {

			}
			if (e.getCause() instanceof NoValidUserException) {
				bot.execute(new SendMessage(getChatId(update), IMessage.NEED_REGISTRATION));
			}
			debugBot.execute(new SendMessage(myChatId,
					"FIFA " + IEmoji.FOOTBALL + "\n" + "EXCEPTION GENERATED:\n\n" + e.getStackTrace()[0].toString()
							+ "\n" + e.getStackTrace()[1].toString() + "\n" + e.getStackTrace()[2].toString()));
			e.printStackTrace();
		}

		return "";
	}

	private void weeklyUpdate() {
		fifaStatisticsManager.sendWeeklyReports();
	}
}
