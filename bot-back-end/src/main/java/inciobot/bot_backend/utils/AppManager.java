package inciobot.bot_backend.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppManager {

	@Value("${app.url}")
	private String appUrl;

	@Value("${bot.token}")
	private String botToken;

	public AppManager() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	private void initialize() {
		// schedulerManager.addKeepAliveTask(this::keepAlive);
	}

	// private void keepAlive() {
	// System.out.println("Calling heroku to keep alive");
	//
	// URL obj;
	// HttpURLConnection con;
	// try {
	// obj = new URL(appUrl + "/" + botToken + "/");
	// con = (HttpURLConnection) obj.openConnection();
	// con.getResponseCode();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
