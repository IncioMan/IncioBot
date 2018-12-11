package inciobot.bot_ci;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Filter {
	private Date fromDate, toDate;
	private List<String> playersUsername;
	private List<MatchResult> results;

	public Filter() {
		playersUsername = new ArrayList<>();
		results = new ArrayList<>();
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<String> getPlayersUsername() {
		return playersUsername;
	}

	public void setPlayersUsername(List<String> playersUsername) {
		this.playersUsername = playersUsername;
	}

	public List<MatchResult> getResults() {
		return results;
	}

	public void setResults(List<MatchResult> results) {
		this.results = results;
	}

	public void addPlayerUsername(String username) {
		playersUsername.add(username);
	}

	public void addMatchResult(MatchResult result) {
		results.add(result);
	}

}
