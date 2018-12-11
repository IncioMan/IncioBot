package inciobot.bot_ci;

import java.math.BigInteger;

public class MatchCountPerWeekPerUser {
	private Double week, year;
	private Long count;
	private Double countAvg;
	private String username;

	public MatchCountPerWeekPerUser() {
		week = 0.0;
		year = 0.0;
		count = Long.parseLong("0");
		countAvg = 0.0;
		username = "";
	}

	public Double getWeek() {
		return week;
	}

	public void setWeek(Double week) {
		this.week = week;
	}

	public Double getYear() {
		return year;
	}

	public void setYear(Double year) {
		this.year = year;
	}

	// public Long getCount() {
	// return count;
	// }
	//
	// public void setCount(BigInteger count) {
	// this.count = count.longValue();
	// }
	//
	// public void setCount(Long count) {
	// this.count = count;
	// }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void addCount(Long count2) {
		count += count2;
	}

	public Double getCountAvg() {
		return countAvg;
	}

	public void setCountAvg(Double countAvg) {
		this.countAvg = countAvg;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count.longValue();
	}

}
