package inciobot.bot_backend.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardHide;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

import inciobot.bot_backend.model.Update;
import inciobot.bot_backend.scheduler.SchedulerManager;

public abstract class AbstractBotController {
	protected TelegramBot bot;

	protected TelegramBot debugBot;

	protected String myChatId;

	protected String girlfriendChatId;

	@Autowired
	protected SchedulerManager schedulerManager;

	protected void setMyChatId(String myChatId) {
		this.myChatId = myChatId;
	}

	protected void setGirlfriendChatId(String girlfriendChatId) {
		this.girlfriendChatId = girlfriendChatId;
	}

	public String getMyChatId() {
		return myChatId;
	}

	protected void initializeBot(String botToken) {
		bot = TelegramBotAdapter.build(botToken);
	}

	protected void initializeDebugBot(String botToken) {
		debugBot = TelegramBotAdapter.build(botToken);
	}

	protected String getBody(BufferedReader reader) throws IOException {
		String line = null;
		StringBuilder builder = new StringBuilder();

		while ((line = reader.readLine()) != null)
			builder.append(line);

		return builder.toString();
	}

	public String getTextLowerCaseMessage(Update update) {
		if (update == null)
			return "";
		return update.getMessage().getText().toLowerCase();
	}

	public String getTextMessage(Update update) {
		if (update == null)
			return "";
		return update.getMessage().getText();
	}

	public void sendMessage(String chatId, String message) {
		bot.execute(new SendMessage(chatId, message).replyMarkup(new ReplyKeyboardHide()));
	}

	public void sendMessage(String chatId, String message, ParseMode parseMode) {
		bot.execute(new SendMessage(chatId, message).replyMarkup(new ReplyKeyboardHide()).parseMode(parseMode));
	}

	public void sendMessage(String chatId, String message, ReplyKeyboardMarkup replyKeyboardMarkup) {
		bot.execute(new SendMessage(chatId.toString(), message).replyMarkup(replyKeyboardMarkup));
	}

	public String getUsername(Update update) {
		if (update == null)
			return "";
		return update.getMessage().getFrom().getUsername();
	}

	public Integer getUserIdentifier(Update update) {
		if (update == null)
			return -1;
		return update.getMessage().getFrom().getId();
	}

	public Integer getUserIdentifierInlineQuery(Update update) {
		if (update == null)
			return -1;
		return update.getInlinequery().from().getId();
	}

	public String getChatId(Update update) {
		if (update == null)
			return "";
		return update.getMessage().getChat().getId().toString();
	}

	public void sendAnswerInlineQuery(AnswerInlineQuery answerInlineQuery) {
		bot.execute(answerInlineQuery);
	}

	public void sendPhoto(String chatId, File file) {
		bot.execute(new SendPhoto(chatId, file));
	}

	public void debugMessage(String chatId, String text) {
		debugBot.execute(new SendMessage(chatId, text));
	}
}
