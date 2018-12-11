package inciobot.bot_backend.bot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pengrad.telegrambot.request.SendMessage;

import inciobot.bot_backend.constants.IEmoji;
import inciobot.bot_backend.model.Refueling;
import inciobot.bot_backend.services.IService;
import inciobot.bot_backend.services.IStatsService;
import inciobot.bot_ci.MonthCount;

@Controller
@RequestMapping(value = "/fuel")
public class FuelBot extends AbstractBotController {

	@Value("${fuel.bot.token}")
	protected void initializeBot(String botToken) {
		super.initializeBot(botToken);
	};

	@Value("${my.chat.id}")
	protected void setMyChatId(String id) {
		super.setMyChatId(id);
	}

	@Value("${debug.bot.token}")
	protected void initializeDebugBot(String botToken) {
		super.initializeDebugBot(botToken);
	};

	@Autowired
	private IService service;
	@Autowired
	private IStatsService statsService;

	@CrossOrigin(allowedHeaders = "Content-Type")
	@PostMapping(value = "/20112017/")
	public void addRefueling(@RequestBody Refueling refueling) {
		debugBot.execute(new SendMessage(myChatId, "FUEL " + IEmoji.BLUE_CAR + "\n\n" + refueling.toString()));
		service.addRefueling(refueling);
	}

	@CrossOrigin
	@GetMapping(value = "/month/price/")
	@ResponseBody
	public List<MonthCount> pricePerMonth() {
		List<MonthCount> pricePerMonth = statsService.getPricePerMonth();
		return pricePerMonth;
	}

	@CrossOrigin
	@GetMapping(value = "/month/distance/")
	@ResponseBody
	public List<MonthCount> distancePerMonth() {
		return statsService.getDistancePerMonth();
	}

	@CrossOrigin
	@GetMapping(value = "/month/distance-quantity/")
	@ResponseBody
	public List<MonthCount> avgPerMonth() {
		return statsService.getAvgDistQuantityPerMonth();
	}
}
