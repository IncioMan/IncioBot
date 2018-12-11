package inciobot.bot_ci;

import java.math.BigInteger;

public class MatchCount {
	private Long count;
	private Double week, year;

	public Long getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count.longValue();
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

}
