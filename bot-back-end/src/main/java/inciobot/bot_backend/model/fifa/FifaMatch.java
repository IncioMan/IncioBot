package inciobot.bot_backend.model.fifa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import inciobot.bot_backend.constants.ITableNames;
import inciobot.bot_backend.utils.fifa.MonthAndYear;
import inciobot.bot_backend.utils.fifa.WeekAndYear;
import inciobot.bot_ci.MatchResult;

@Entity
@Table(name = ITableNames.FIFA_MATCH)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FifaMatch extends AbstractToComplete {
	private TeamPerformance team1;
	private TeamPerformance team2;
	private Long id;
	private boolean confirmedByOpponent;
	private String comment;

	public FifaMatch() {
		super();
		team1 = new TeamPerformance();
		team2 = new TeamPerformance();
		confirmedByOpponent = false;
		comment = " ";
	}

	@Id
	@GeneratedValue
	@Column(name = "fifa_match_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public TeamPerformance getTeam1() {
		return team1;
	}

	public void setTeam1(TeamPerformance team1) {
		this.team1 = team1;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public TeamPerformance getTeam2() {
		return team2;
	}

	public void setTeam2(TeamPerformance team2) {
		this.team2 = team2;
	}

	public boolean isConfirmedByOpponent() {
		return confirmedByOpponent;
	}

	public void setConfirmedByOpponent(boolean confirmedByOpponent) {
		this.confirmedByOpponent = confirmedByOpponent;
	}

	@Column(name = "creator_comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Transient
	public String getRappresentation(boolean includeId, boolean debug) {
		StringBuilder retVal = new StringBuilder();
		StringBuilder result = new StringBuilder();
		if (includeId) {
			retVal.append("(ID: " + id + ") ");
		}
		if (team1 != null) {
			retVal.append(getTeamRappresentation(team1));
			result.append(team1.getGoals() + "-");
		}
		retVal.append(" - ");
		if (team2 != null) {
			retVal.append(getTeamRappresentation(team2));
			result.append(team2.getGoals());
		}
		retVal.append("\nRisultato: ");
		retVal.append(result.toString());

		if (dateCreation != null)
			retVal.append("\nIn data: " + new SimpleDateFormat("dd-MM-YYYY").format(dateCreation));

		if (debug) {
			retVal.append(" - date: " + dateCreation);
			retVal.append(" - completed: " + completed);
		}
		if (comment != null && !comment.equals(" ")) {
			retVal.append("\nCommento di " + creator.getUser().getUsername() + ": \"" + comment + "\"");
		}
		return retVal.toString();
	}

	@Transient
	private String getTeamRappresentation(TeamPerformance team) {
		StringBuilder retVal = new StringBuilder();
		if (team.getPlayers() != null) {
			int i = 0;
			for (Player player : team.getPlayers()) {
				if (i++ != 0)
					retVal.append(", ");
				retVal.append(player.getUser().getUsername());
			}
		}
		return retVal.toString();
	}

	public String getRappresentationResultCenter(boolean includeId, boolean debug, boolean includeComment) {
		StringBuilder retVal = new StringBuilder();
		if (includeId) {
			retVal.append("(ID: " + id + ") ");
		}
		if (team1 != null) {
			retVal.append(getTeamRappresentation(team1) + " ");
			retVal.append(team1.getGoals());
		}
		retVal.append(" - ");
		if (team2 != null) {
			retVal.append(team2.getGoals() + " ");
			retVal.append(getTeamRappresentation(team2));
		}

		if (debug) {
			retVal.append(" - date: " + dateCreation);
			retVal.append(" - completed: " + completed);
		}
		if (includeComment)
			if (comment != null && !comment.equals(" ")) {
				retVal.append("\nCommento di " + creator.getUser().getUsername() + ": \"" + comment + "\"");
			}
		return retVal.toString();
	}

	@Transient
	public List<Player> getOpponents(Integer userIdentifier) {
		List<Player> players = new ArrayList<>();
		players.addAll(team1.getPlayers());
		players.addAll(team2.getPlayers());

		players = players.stream().filter(p -> !userIdentifier.equals(p.getUser().getId()))
				.collect(Collectors.toList());
		return players;
	}

	@Transient
	public WeekAndYear getWeekDateCreation() {
		WeekAndYear weekAndYear = new WeekAndYear();
		System.out.println(new SimpleDateFormat("dd-MM-YY").format(dateCreation));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateCreation);
		weekAndYear.setWeek(Double.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) + ""));
		weekAndYear.setYear(Double.valueOf(calendar.get(Calendar.YEAR) + ""));

		return weekAndYear;
	}

	@Transient
	public MonthAndYear getMonthDateCreation() {
		MonthAndYear monthAndYear = new MonthAndYear();
		System.out.println(new SimpleDateFormat("dd-MM-YY").format(dateCreation));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateCreation);
		monthAndYear.setMonth(Double.valueOf(calendar.get(Calendar.MONTH) + ""));
		monthAndYear.setYear(Double.valueOf(calendar.get(Calendar.YEAR) + ""));

		return monthAndYear;
	}

	@Transient
	public MatchResult resultFor(Player player) {
		if (team1.getPlayers() == null || team2.getPlayers() == null)
			return MatchResult.DRAW;

		if (team1.getPlayers().contains(player)) {
			if (team1.getGoals() > team2.getGoals())
				return MatchResult.WON;
			if (team1.getGoals() == team2.getGoals())
				return MatchResult.DRAW;
			if (team1.getGoals() < team2.getGoals())
				return MatchResult.LOST;
		}
		if (team2.getPlayers().contains(player)) {
			if (team2.getGoals() > team1.getGoals())
				return MatchResult.WON;
			if (team2.getGoals() == team1.getGoals())
				return MatchResult.DRAW;
			if (team2.getGoals() < team1.getGoals())
				return MatchResult.LOST;
		}

		return MatchResult.DRAW;
	}

	@Transient
	public FifaMatch getThis() {
		return this;
	}

	@Transient
	public Integer getGoals(Player player) {
		if (team1.getPlayers() == null || team2.getPlayers() == null)
			return 0;
		if (team1.getPlayers().contains(player))
			return team1.getGoals();

		if (team2.getPlayers().contains(player))
			return team2.getGoals();
		return 0;
	}

	@Transient
	public String getOpponentsRappresentation() {
		StringBuilder retVal = new StringBuilder();
		if (team1 != null) {
			retVal.append(getTeamRappresentation(team1));
		}
		retVal.append(" - ");
		if (team2 != null) {
			retVal.append(getTeamRappresentation(team2));
		}
		return retVal.toString();
	}

	@Transient
	public String getResult() {
		StringBuilder retVal = new StringBuilder();
		if (team1 != null) {
			retVal.append(team1.getGoals());
		}
		retVal.append(" - ");
		if (team2 != null) {
			retVal.append(team2.getGoals());
		}
		return retVal.toString();
	}

}
