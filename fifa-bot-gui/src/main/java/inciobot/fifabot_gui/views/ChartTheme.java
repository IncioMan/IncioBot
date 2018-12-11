package inciobot.fifabot_gui.views;

import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.addon.charts.model.style.Theme;

public class ChartTheme extends Theme {
	private static final long serialVersionUID = 1L;

	public ChartTheme() {
		Color[] colors = new Color[5];
		colors[0] = new SolidColor("#FBC02D");
		colors[1] = new SolidColor("#FF4081");
		colors[2] = new SolidColor("#00C853");
		colors[3] = new SolidColor("#D500F9");
		colors[4] = new SolidColor("#F44336");

		setColors(colors);

		Style titleStyle = new Style();
		titleStyle.setColor(new SolidColor("black"));
		setTitle(titleStyle);
	}
}
