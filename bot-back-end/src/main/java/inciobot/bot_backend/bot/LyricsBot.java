package inciobot.bot_backend.bot;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.pengrad.telegrambot.request.SendMessage;

import inciobot.bot_backend.model.lyrics.JsonTrack;
import inciobot.bot_backend.utils.MusixMatchLyricsFactory;

/***
 * 
 * @author Alex
 *
 *         This bot is called by a IFTTT every time I save a song on Spotify It
 *         retrieves the lyrics from MusixMatch and sends it to me
 *
 */
@Controller
@RequestMapping(value = "/${lyrics.bot.token}/")
public class LyricsBot extends AbstractBotController {

	@Autowired
	private MusixMatchLyricsFactory lyricsFactory;

	@Value("${lyrics.bot.token}")
	protected void initializeBot(String botToken) {
		super.initializeBot(botToken);
	};

	@Value("${lyrics.my.chat.id}")
	protected void setMyChatId(String id) {
		super.setMyChatId(id);
	}

	@GetMapping("/getLyrics/")
	@ResponseBody
	private String getLyrics() {
		return "Try posting name(track_Name) and artist(artist_Name) of a song (JSON format)";
	}

	@PostMapping("/getLyrics/")
	@ResponseBody
	private String getLyrics(HttpServletRequest request) throws IOException {
		String body = getBody(request.getReader());
		Gson gson = new Gson();
		JsonTrack track = gson.fromJson(body.replace("_", ""), JsonTrack.class);
		bot.execute(new SendMessage(myChatId, lyricsFactory.getLyrics(track.getTrackName(), track.getArtistName())));

		return "";
	}
}
