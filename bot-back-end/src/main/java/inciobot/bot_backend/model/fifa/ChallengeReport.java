package inciobot.bot_backend.model.fifa;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;

@MappedSuperclass
public class ChallengeReport {
	protected Integer matchesSize;

	protected Integer won;
	protected Integer draw;
	protected Integer lost;

	protected Integer goalsScored;
	protected Integer goalsAgainst;

	protected Integer matchNoScore;
	protected Integer matchNoGoalAgainst;

	protected Double wonPercentage;
	protected Double drawPercentage;
	protected Double lostPercentage;
	protected Double noGoalAgainstPercentage;
	protected Double noScoredPercentage;
	protected Double goalsScoredAvg;
	protected Double goalsAgainstAvg;

	protected Double month;
	protected Double year;
	protected Double week;

	public ChallengeReport() {
		won = 0;
		draw = 0;
		lost = 0;
		goalsAgainst = 0;
		goalsScored = 0;
		matchNoGoalAgainst = 0;
		matchNoScore = 0;
		matchesSize = 0;

		wonPercentage = 0.0;
		drawPercentage = 0.0;
		lostPercentage = 0.0;
		noGoalAgainstPercentage = 0.0;
		noScoredPercentage = 0.0;
		goalsScoredAvg = 0.0;
		goalsAgainstAvg = 0.0;
	}

	@PostLoad
	public void onLoad() {
		if (matchesSize != 0) {
			wonPercentage = (won * 100.0) / matchesSize;
			drawPercentage = (draw * 100.0) / matchesSize;
			lostPercentage = (lost * 100.0) / matchesSize;
			noGoalAgainstPercentage = (matchNoGoalAgainst * 100.0) / matchesSize;
			noScoredPercentage = (matchNoScore * 100.0) / matchesSize;
			goalsScoredAvg = (double) goalsScored / matchesSize;
			goalsAgainstAvg = (double) goalsAgainst / matchesSize;
		}
	}

	public Integer getWon() {
		return won;
	}

	public void setWon(Integer won) {
		this.won = won;
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Integer getLost() {
		return lost;
	}

	public void setLost(Integer lost) {
		this.lost = lost;
	}

	public void increaseWon() {
		won++;
	}

	public void increaseDraw() {
		draw++;
	}

	public void increaseLost() {
		lost++;
	}

	public Integer getGoalsScored() {
		return goalsScored;
	}

	public void setGoalsScored(Integer goalsScored) {
		this.goalsScored = goalsScored;
	}

	public Integer getGoalsAgainst() {
		return goalsAgainst;
	}

	public void setGoalsAgainst(Integer goalsAgainst) {
		this.goalsAgainst = goalsAgainst;
	}

	public Integer getMatchNoScore() {
		return matchNoScore;
	}

	public void setMatchNoScore(Integer matchNoScore) {
		this.matchNoScore = matchNoScore;
	}

	public Integer getMatchNoGoalAgainst() {
		return matchNoGoalAgainst;
	}

	public void setMatchNoGoalAgainst(Integer matchNoGoalAgainst) {
		this.matchNoGoalAgainst = matchNoGoalAgainst;
	}

	public void increaseGoalAgainst(Integer goals) {
		goalsAgainst += goals;
		if (goals == 0) {
			matchNoGoalAgainst++;
		}
	}

	public void increaseGoalScored(Integer goals) {
		goalsScored += goals;
		if (goals == 0) {
			matchNoScore++;
		}
	}

	public void increaseMatchNoGoalAgainst() {
		matchNoGoalAgainst++;
	}

	public void increaseMatchNoScore() {
		matchNoScore++;
	}

	public Integer getMatchesSize() {
		return matchesSize;
	}

	public void setMatchesSize(Integer matchesSize) {
		this.matchesSize = matchesSize;
	}

	// @Transient
	public Double getWonPercentage() {
		return wonPercentage;
	}

	public void setWonPercentage(Double wonPercentage) {
		this.wonPercentage = wonPercentage;
	}

	// @Transient
	public Double getDrawPercentage() {
		return drawPercentage;
	}

	// @Transient
	public void setDrawPercentage(Double drawPercentage) {
		this.drawPercentage = drawPercentage;
	}

	// @Transient
	public Double getLostPercentage() {
		return lostPercentage;
	}

	// @Transient
	public void setLostPercentage(Double lostPercentage) {
		this.lostPercentage = lostPercentage;
	}

	// @Transient
	public Double getNoGoalAgainstPercentage() {
		return noGoalAgainstPercentage;
	}

	// @Transient
	public void setNoGoalAgainstPercentage(Double noGoalAgainstPercentage) {
		this.noGoalAgainstPercentage = noGoalAgainstPercentage;
	}

	// @Transient
	public Double getNoScoredPercentage() {
		return noScoredPercentage;
	}

	// @Transient
	public void setNoScoredPercentage(Double noScoredPercentage) {
		this.noScoredPercentage = noScoredPercentage;
	}

	// @Transient
	public Double getGoalsScoredAvg() {
		return goalsScoredAvg;
	}

	// @Transient
	public void setGoalsScoredAvg(Double goalsScoredAvg) {
		this.goalsScoredAvg = goalsScoredAvg;
	}

	// @Transient
	public Double getGoalsAgainstAvg() {
		return goalsAgainstAvg;
	}

	// @Transient
	public void setGoalsAgainstAvg(Double goalsAgainstAvg) {
		this.goalsAgainstAvg = goalsAgainstAvg;
	}

	public void decreaseWon() {
		if (won != 0)
			won--;
	}

	public void decreaseGoalAgainst(Integer goalsAgainst) {
		this.goalsAgainst -= goalsAgainst;
		if (this.goalsAgainst < 0)
			this.goalsAgainst = 0;
	}

	public void decreaseGoalScored(Integer goalsScored) {
		this.goalsScored -= goalsScored;
		if (this.goalsScored < 0)
			this.goalsScored = 0;
	}

	public void decreaseLost() {
		if (lost != 0)
			lost--;
	}

	public void decreaseDraw() {
		if (draw != 0)
			draw--;
	}

	public void decreaseMatchSize() {
		matchesSize--;
		if (matchesSize < 0)
			matchesSize = 0;

	}

	public void increaseMatchSize() {
		matchesSize++;
	}

	public Double getMonth() {
		return month;
	}

	public void setMonth(Double month) {
		this.month = month;
	}

	public Double getYear() {
		return year;
	}

	public void setYear(Double year) {
		this.year = year;
	}

	public Double getWeek() {
		return week;
	}

	public void setWeek(Double week) {
		this.week = week;
	}

}
