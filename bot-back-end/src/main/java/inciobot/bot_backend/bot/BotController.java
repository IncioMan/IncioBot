package inciobot.bot_backend.bot;

import java.io.IOException;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.constants.IStickers;
import inciobot.bot_backend.model.Update;

@Controller
@RequestMapping(value = "/${bot.token}/")
public class BotController extends AbstractBotController {

	@Value("${bot.token}")
	private String botToken;
	private Runnable pillTask;

	@PostConstruct
	private void initialize() {
		// schedulerManager.addTaskToExecute(this::scheduledTask);
		bot.execute(new SendMessage(myChatId, IMessage.MESSAGE_TO_ME));
		schedulerManager.addPillTasks(pillTask);
	}

	@Value("${bot.token}")
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

	@Value("${girlfriend.chat.id}")
	protected void setGirlfriendChatId(String id) {
		super.setGirlfriendChatId(id);
	}

	@SuppressWarnings("unused")
	private void scheduledTask() {
		// used for scheduled messages
	}

	@PostMapping
	@ResponseBody
	public String telegram(HttpServletRequest request) throws IOException {
		String body = getBody(request.getReader());

		Gson gson = new Gson();
		Update update = gson.fromJson(body.replace("_", ""), Update.class);

		if (update == null || update.getMessage() == null || update.getMessage().getText() == null) {
			bot.execute(new SendMessage(myChatId, body.toString()));
			return "";
		}

		debugBot.execute(new SendMessage(myChatId, "IncioBot:\n\n" + body.toString()));

		if ((getTextLowerCaseMessage(update).replaceAll(" ", "").contains("incio")
				|| getTextLowerCaseMessage(update).replaceAll(" ", "").contains("alex"))
				&& getTextLowerCaseMessage(update).replaceAll(" ", "").contains("bot")) {
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.TALKING_ABOUT_ME));
			bot.execute(new SendSticker(update.getMessage().getChat().getId(),
					IStickers.COOL[new Random().nextInt(IStickers.COOL.length)]));
			return "";
		}

		if (getTextLowerCaseMessage(update).contains("gmt your computer crashed crash")) {
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.WHAT_IS_THIS));
			return "";
		}

		if (getTextLowerCaseMessage(update).contains("fifa") || getTextLowerCaseMessage(update).contains("playstation")
				|| getTextLowerCaseMessage(update).contains("torneo")) {
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.FIFA_TOURNAMENT));
			return "";
		}

		if (getTextLowerCaseMessage(update).contains("mr robot")) {
			bot.execute(new SendSticker(update.getMessage().getChat().getId(), IStickers.MOVIE));
			return "";
		}
		if (getTextLowerCaseMessage(update).contains("foto")) {
			bot.execute(new SendSticker(update.getMessage().getChat().getId(), IStickers.PHOTO));
			return "";
		}
		if (getTextLowerCaseMessage(update).contains("programm") || getTextLowerCaseMessage(update).contains("pc")
				|| getTextLowerCaseMessage(update).contains("computer")) {
			bot.execute(new SendSticker(update.getMessage().getChat().getId(), IStickers.PC));
			return "";
		}
		if (getTextLowerCaseMessage(update).contains("ti amo")
				&& "AnnaChiaraz".equals(update.getMessage().getFrom().getUsername())) {
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.LOVE_ANSWER));
			return "";
		}
		if (getTextLowerCaseMessage(update).contains("palestra")) {
			bot.execute(new SendSticker(update.getMessage().getChat().getId(), IStickers.GYM));
			return "";
		}
		if (getTextLowerCaseMessage(update).contains("lavabo")) {
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.LAVABO));
			return "";
		}

		if (getTextLowerCaseMessage(update).replaceAll(" ", "").contains("lpost")) {
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.POST_SUCKS));
			bot.execute(new SendSticker(update.getMessage().getChat().getId(), IStickers.HORRIBLE[0]));
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.ILGIOMALE_RULES));
			bot.execute(new SendSticker(update.getMessage().getChat().getId(),
					IStickers.THUMBS_UP[new Random().nextInt(IStickers.THUMBS_UP.length)]));
			return "";
		}

		// Owner's commands
		if ("AlexIncio".equals(update.getMessage().getFrom().getUsername())) {

			if (getTextLowerCaseMessage(update).contains("ti spengo eh")) {
				bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.APOLOGIZE_OWNER));
				bot.execute(new SendSticker(update.getMessage().getChat().getId(), IStickers.SAD));
				return "";
			}
		}
		return "ciao";
	}

	@GetMapping("/send-to-me/{msg}")
	@ResponseBody
	private String telegram(@PathVariable("msg") String text, Model model) {
		bot.execute(new SendMessage(myChatId, text));
		return "";
	}

	@GetMapping("/")
	@ResponseBody
	private String retry() throws IOException {
		return "post, thanks";
	}

	@RequestMapping(value = "/")
	@ResponseBody
	String welcomePage() {
		return "no token, no page";
	}
}
