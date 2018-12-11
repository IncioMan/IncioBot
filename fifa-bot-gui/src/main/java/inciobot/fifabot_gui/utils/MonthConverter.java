package inciobot.fifabot_gui.utils;

public class MonthConverter {

	public static String getMonth(Double month) {
		if (month.equals(0.0d)) {
			return "January";
		}
		if (month.equals(1.0d)) {
			return "February";
		}
		if (month.equals(2.0d)) {
			return "March";
		}
		if (month.equals(3.0d)) {
			return "April";
		}
		if (month.equals(4.0d)) {
			return "May";
		}
		if (month.equals(5.0d)) {
			return "June";
		}
		if (month.equals(6.0d)) {
			return "July";
		}
		if (month.equals(7.0d)) {
			return "August";
		}
		if (month.equals(8.0d)) {
			return "September";
		}
		if (month.equals(9.0d)) {
			return "October";
		}
		if (month.equals(10.0d)) {
			return "November";
		}
		if (month.equals(11.0d)) {
			return "December";
		}
		return "No valid month";
	}

}
