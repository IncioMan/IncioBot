package inciobot.fifabot_gui.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.LayoutDirection;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.ui.Component;

import inciobot.bot_ci.MatchCountPerWeekPerUser;

public class MatchPlayedPerWeekChart extends Chart implements Component {
	private static final long serialVersionUID = 1L;
	private List<MatchCountPerWeekPerUser> counts;

	public MatchPlayedPerWeekChart(List<MatchCountPerWeekPerUser> counts) {
		addStyleName("chart");
		this.counts = counts;

		buildChart();
	}

	private void buildChart() {
		MatchCountPerWeekPerUser begin = new MatchCountPerWeekPerUser();
		MatchCountPerWeekPerUser finish = new MatchCountPerWeekPerUser();

		getBeginFinish(counts, begin, finish);

		setHeight("450px");
		setWidth("100%");

		Configuration configuration = getConfiguration();
		configuration.getChart().setType(ChartType.LINE);
		configuration.getChart().setMarginRight(130);
		configuration.getChart().setMarginBottom(25);

		configuration.getTitle().setText("Partite registrate settimanalmente");
		configuration.getSubTitle().setText("per giocatore");

		setCategories(configuration, begin, finish);

		YAxis yAxis = configuration.getyAxis();
		yAxis.setMin(-5d);
		yAxis.setTitle(new AxisTitle("Week"));
		yAxis.getTitle().setAlign(VerticalAlign.MIDDLE);

		Style style = new Style();
		style.setFontSize("8px");
		configuration.getxAxis().getLabels().setStyle(style);

		PlotOptionsLine plotOptions = new PlotOptionsLine();
		plotOptions.getDataLabels().setEnabled(true);
		configuration.setPlotOptions(plotOptions);

		Legend legend = configuration.getLegend();
		legend.setLayout(LayoutDirection.VERTICAL);
		legend.setAlign(HorizontalAlign.RIGHT);
		legend.setVerticalAlign(VerticalAlign.TOP);
		legend.setX(-10d);
		legend.setY(100d);
		legend.setBorderWidth(0);

		List<ListSeries> series = createSeries(counts, begin, finish);

		series.stream().forEach(s -> {
			configuration.addSeries(s);
		});
		drawChart(configuration);
	}

	private List<ListSeries> createSeries(List<MatchCountPerWeekPerUser> counts, MatchCountPerWeekPerUser begin,
			MatchCountPerWeekPerUser finish) {
		Map<String, List<MatchCountPerWeekPerUser>> users = getUsers(counts);
		List<ListSeries> retval = new ArrayList<>();

		for (String user : users.keySet()) {
			ListSeries series = new ListSeries(user);
			Double week = begin.getWeek();
			Double year = begin.getYear();

			List<Number> numbers = new ArrayList<>();

			boolean found = false;
			while (!(week.equals(finish.getWeek()) && year.equals(finish.getYear()))) {
				found = false;
				for (MatchCountPerWeekPerUser m : users.get(user)) {
					if (m.getWeek().equals(week) && m.getYear().equals(year)) {
						numbers.add(m.getCount());
						found = true;
					}
				}
				if (!found) {
					numbers.add(0);
				}

				if (++week == 54) {
					week = 1.0;
					year++;
				}
			}

			for (MatchCountPerWeekPerUser m : users.get(user)) {
				found = false;
				if (m.getWeek().equals(week) && m.getYear().equals(year)) {
					numbers.add(m.getCount());
					found = true;
				}
			}
			if (!found) {
				numbers.add(0);
			}

			series.setData(numbers);
			retval.add(series);
		}

		return retval;
	}

	private Map<String, List<MatchCountPerWeekPerUser>> getUsers(List<MatchCountPerWeekPerUser> counts) {
		Map<String, List<MatchCountPerWeekPerUser>> users = new HashMap<>();
		counts.stream().forEach(c -> {
			if (users.get(c.getUsername()) == null) {
				users.put(c.getUsername(), new ArrayList<MatchCountPerWeekPerUser>());
			}
			List<MatchCountPerWeekPerUser> cts = users.get(c.getUsername());
			cts.add(c);
		});

		return users;
	}

	private void setCategories(Configuration configuration, MatchCountPerWeekPerUser begin,
			MatchCountPerWeekPerUser finish) {
		Double week = begin.getWeek();
		Double year = begin.getYear();

		List<String> categories = new ArrayList<>();

		while (!(week.equals(finish.getWeek()) && year.equals(finish.getYear()))) {
			categories.add(week.intValue() + "° week " + year.intValue());
			if (++week == 54) {
				week = 1.0;
				year++;
			}
		}
		categories.add(week.intValue() + "° week " + year.intValue());

		configuration.getxAxis().setCategories(categories.toArray(new String[0]));
	}

	private void getBeginFinish(List<MatchCountPerWeekPerUser> counts, MatchCountPerWeekPerUser begin,
			MatchCountPerWeekPerUser finish) {
		int i = 0;
		for (MatchCountPerWeekPerUser m : counts) {
			if (i++ == 0) {
				begin.setWeek(m.getWeek());
				begin.setYear(m.getYear());
				finish.setWeek(m.getWeek());
				finish.setYear(m.getYear());
			} else {
				if (m.getWeek() < begin.getWeek() && m.getYear() <= begin.getYear()) {
					begin.setWeek(m.getWeek());
					begin.setYear(m.getYear());
				}
				if (m.getWeek() > finish.getWeek() && m.getYear() >= finish.getYear()) {
					finish.setWeek(m.getWeek());
					finish.setYear(m.getYear());
				}
			}
		}
	}

}
