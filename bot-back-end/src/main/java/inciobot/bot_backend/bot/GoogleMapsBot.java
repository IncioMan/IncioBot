package inciobot.bot_backend.bot;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import inciobot.bot_backend.constants.ICommands;
import inciobot.bot_backend.constants.IEmoji;
import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.model.Update;

/***
 * 
 * @author Alex
 *
 *         This bot allowed me to retrieve automatically information on the
 *         traffic between my work and home (like Google Maps does). It did also
 *         send me this information automatically at the end of my work day,
 *         when I was going to leave my workplace (from 19, every 30 minutes)
 *
 */
@Controller
@RequestMapping(value = "/${google.maps.bot.token}/")
public class GoogleMapsBot extends AbstractBotController {

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;

	@Value("${grande.emilia.position}")
	private String grandeEmiliaPosition;

	@Value("${marzaglia.position}")
	private String marzagliaPosition;

	@Value("${work.position}")
	private String workPosition;

	@Value("${freeway.position}")
	private String freewayPosition;

	@Value("${freeway.exit.position}")
	private String freewayExitPosition;

	@Value("${home.position}")
	private String homePosition;

	private Runnable scheduleTask;

	@Value("${google.maps.bot.token}")
	protected void initializeBot(String botToken) {
		super.initializeBot(botToken);
	};

	@Value("${my.chat.id}")
	protected void setMyChatId(String id) {
		super.setMyChatId(id);
	}

	@PostConstruct
	private void initialize() {
		scheduleTask = this::scheduledTask;
		schedulerManager.addTaskToOftenExecute(scheduleTask);
	}

	private void scheduledTask() {
		sendDurationGrandeEmiliaMarzaglia(myChatId);
		sendDurationRoutesWorkHome(myChatId);
	}

	private void sendDurationRoutesWorkHome(String chatId) {
		GeoApiContext context = new GeoApiContext().setApiKey(googleMapsApiKey);
		DirectionsApiRequest request = DirectionsApi.getDirections(context, workPosition, homePosition);
		request.departureTime(new Instant());

		try {
			int total = 0;
			DirectionsResult result = request.await();
			for (DirectionsLeg leg : result.routes[0].legs) {
				total += Integer.parseInt(leg.durationInTraffic.toString().replace(" mins", ""));
			}
			bot.execute(new SendMessage(chatId,
					IMessage.DURATION_WRK_HOME_VIA_EMILIA + ": " + total + " " + IMessage.MINUTES));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			int total = 0;

			request = DirectionsApi.getDirections(context, workPosition, freewayPosition);
			request.departureTime(new Instant());

			DirectionsResult result = request.await();
			for (DirectionsLeg leg : result.routes[0].legs) {
				total += Integer.parseInt(leg.durationInTraffic.toString().replace(" mins", ""));
			}

			request = DirectionsApi.getDirections(context, freewayPosition, freewayExitPosition);
			request.departureTime(new Instant());

			result = request.await();
			for (DirectionsLeg leg : result.routes[0].legs) {
				total += Integer.parseInt(leg.durationInTraffic.toString().replace(" mins", ""));
			}

			request = DirectionsApi.getDirections(context, freewayExitPosition, homePosition);
			request.departureTime(new Instant());

			result = request.await();
			for (DirectionsLeg leg : result.routes[0].legs) {
				total += Integer.parseInt(leg.durationInTraffic.toString().replace(" mins", ""));
			}

			bot.execute(new SendMessage(chatId,
					IMessage.DURATION_WRK_HOME_FREEWAY + ": " + (total - 2) + " " + IMessage.MINUTES));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendDurationGrandeEmiliaMarzaglia(String chatId) {
		GeoApiContext context = new GeoApiContext().setApiKey(googleMapsApiKey);
		DirectionsApiRequest request = DirectionsApi.getDirections(context, grandeEmiliaPosition, marzagliaPosition);
		request.departureTime(new Instant());

		try {
			DirectionsResult result = request.await();
			Integer durationInTraffic = Integer
					.parseInt(result.routes[0].legs[0].durationInTraffic.humanReadable.replace(" mins", ""));
			StringBuilder builder = new StringBuilder();
			builder.append(IMessage.DURATION_GE_MA + ":\n " + durationInTraffic + " " + IMessage.MINUTES);
			builder.append(" (");
			if (durationInTraffic < 9) {
				builder.append(IEmoji.GREEN_CHECK);
			}
			if (8 < durationInTraffic && durationInTraffic < 13) {
				builder.append(IEmoji.BLUE_BALL);
			}
			if (durationInTraffic > 12) {
				builder.append(IEmoji.RED_BALL);
			}
			builder.append(")");
			bot.execute(new SendMessage(chatId, builder.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PostMapping
	@ResponseBody
	public String telegram(HttpServletRequest request) throws IOException {
		String body = getBody(request.getReader());

		Gson gson = new Gson();
		Update update = gson.fromJson(body.replace("_", ""), Update.class);

		if (update.getMessage().getText().toLowerCase().equals("/start")) {
			String[] keyboard = { ICommands.TAKE_ME_HOME, ICommands.LAST_STEP };
			ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboard);
			bot.execute(new SendMessage(update.getMessage().getChat().getId(), IMessage.GOOGLE_MAPS_BOT_WELCOME)
					.replyMarkup(replyKeyboardMarkup));
		}

		if (update.getMessage().getText().toLowerCase().equals(ICommands.TAKE_ME_HOME.toLowerCase())) {
			sendDurationRoutesWorkHome(update.getMessage().getFrom().getId().toString());
		}

		if (update.getMessage().getText().toLowerCase().equals(ICommands.LAST_STEP.toLowerCase())) {
			sendDurationGrandeEmiliaMarzaglia(update.getMessage().getFrom().getId().toString());
		}

		if (!myChatId.equals(update.getMessage().getChat().getId().toString())) {
			return "";
		}

		if (update.getMessage().getText().toLowerCase().equals("stop")) {
			bot.execute(new SendMessage(myChatId, IMessage.STOP_DIRECTIONS));
			schedulerManager.removeTaskToOftenExecute(scheduleTask);
		}

		if (update.getMessage().getText().toLowerCase().equals("start")) {
			bot.execute(new SendMessage(myChatId, IMessage.START_DIRECTIONS));
			schedulerManager.addTaskToOftenExecute(scheduleTask);
		}

		return "";
	}
}
