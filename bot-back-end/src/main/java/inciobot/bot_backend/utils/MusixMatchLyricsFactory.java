package inciobot.bot_backend.utils;

import javax.annotation.PostConstruct;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MusixMatchLyricsFactory {

	@Value("${musixmatch.token}")
	private String apiKey;
	private MusixMatch musixMatch;

	public MusixMatchLyricsFactory() {

	}

	@PostConstruct
	private void initialize() {
		musixMatch = new MusixMatch(apiKey);
	}

	public String getLyrics(String trackName, String artistName) {
		Track track = null;

		try {
			track = musixMatch.getMatchingTrack(trackName, artistName);
		} catch (MusixMatchException e) {
			// TODO Auto-generated catch block
			return artistName + " - " + trackName + "song not found";
		}

		TrackData data = track.getTrack();

		int trackID = data.getTrackId();

		Lyrics lyrics = null;
		try {
			lyrics = musixMatch.getLyrics(trackID);
		} catch (MusixMatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data.getArtistName().toUpperCase() + " - " + data.getTrackName().toUpperCase() + '\n' + '\n'
				+ lyrics.getLyricsBody() + "\n\n" + data.getTrackShareUrl();

	}

	public static void main(String[] args) {
		// MusixMatch musixMatch = new
		// MusixMatch("d65a12cfcca8746143665566ed671244");
		//
		// Track track = null;
		//
		// try {
		// track = musixMatch.getMatchingTrack("run boy run", "woodkid");
		// } catch (MusixMatchException e) {
		// // TODO Auto-generated catch block
		// // return artistName + " - " + trackName + "song not found";
		// }
		//
		// TrackData data = track.getTrack();
	}
}
