package inciobot.bot_backend.model.lyrics;

public class JsonTrack {
	private String trackName;
	private String artistName;

	public JsonTrack() {
		super();
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

}
