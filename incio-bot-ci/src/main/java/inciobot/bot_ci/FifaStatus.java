package inciobot.bot_ci;

public class FifaStatus {
	private String status;
	private Integer todayMatchesSize;
	private Integer todayGoalsScored;
	private Integer generalMatchesSize;
	private Integer generalGoalsScored;

	public FifaStatus(String status) {
		super();
		this.status = status;
		todayGoalsScored = 0;
		todayMatchesSize = 0;
		generalGoalsScored = 0;
		generalMatchesSize = 0;
	}

	public FifaStatus() {
		// TODO Auto-generated constructor stub
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTodayMatchesSize() {
		return todayMatchesSize;
	}

	public void setTodayMatchesSize(Integer todayMatchesSize) {
		this.todayMatchesSize = todayMatchesSize;
	}

	public Integer getTodayGoalsScored() {
		return todayGoalsScored;
	}

	public void setTodayGoalsScored(Integer todayGoalsScored) {
		this.todayGoalsScored = todayGoalsScored;
	}

	public Integer getGeneralMatchesSize() {
		return generalMatchesSize;
	}

	public void setGeneralMatchesSize(Integer generalMatchesSize) {
		this.generalMatchesSize = generalMatchesSize;
	}

	public Integer getGeneralGoalsScored() {
		return generalGoalsScored;
	}

	public void setGeneralGoalsScored(Integer generalGoalsScored) {
		this.generalGoalsScored = generalGoalsScored;
	}
}
