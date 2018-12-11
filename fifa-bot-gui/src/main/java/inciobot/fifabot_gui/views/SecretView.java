package inciobot.fifabot_gui.views;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.LayoutDirection;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import inciobot.bot_ci.ChallengeReportDto;
import inciobot.bot_ci.MatchCount;
import inciobot.bot_ci.TimeAggregation;
import inciobot.fifabot_gui.MyUI;
import inciobot.fifabot_gui.utils.MonthConverter;

@SpringView(name = SecretView.VIEW_NAME)
public class SecretView extends DashboardView {
	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "Developer";

	protected List<Component> createCharts(TimeAggregation time, List<ChallengeReportDto> retrieveChallengeData) {
		List<Component> components = new ArrayList<>();

		DataSeries series6 = new DataSeries("Matches played");
		retrieveChallengeData.stream().forEach(c -> {
			DataSeriesItem item = null;
			switch (time) {
			case MONTH:
				item = new DataSeriesItem(MonthConverter.getMonth(c.getMonth()) + " " + c.getYear().intValue(),
						c.getMatchesSize());
				break;
			case WEEK:
				item = new DataSeriesItem(c.getWeek() + "° week of" + " " + c.getYear().intValue(), c.getMatchesSize());
				break;
			default:
				break;
			}
			series6.add(item);
		});

		components.add(createCharts("Matches played", "100%", "400px", series6, 0d, null, "black",
				"this.series.name + ' in ' + this.point.name +': '+ this.y + ' matches'"));

		components.addAll(super.createCharts(time, retrieveChallengeData));
		return components;
	}

	@Override
	protected void buildBodyLayout(CssLayout bodyLayout) {
		bodyLayout.addComponent(createOpponentButtonLayout());

		MyUI currentUI = MyUI.getCurrent();
		new Thread(() -> {
			currentUI.access(() -> {
				bodyLayout.addComponent(createColumnChart());
				// bodyLayout.addComponent(createLineChart());
				bodyLayout.addComponent(createLineChartResult());
				bodyLayout.addComponent(createLineChartGoals());
				replaceComponent(waitingLayout, bodyLayout);
				currentUI.push();
			});
		}).start();
	}

	/**
	 * Creates the column chart for matches added per month
	 * 
	 * @return the chart component
	 */

	protected Component createColumnChart() {
		ResponseEntity<List<MatchCount>> rateResponse = new RestTemplate().exchange(
				beAddress + "/fifa-api/statistics/matchdistribution/", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MatchCount>>() {
				});
		List<MatchCount> counts = rateResponse.getBody();

		Chart chart = new Chart(ChartType.COLUMN);
		chart.setStyleName("dashboard-chart");

		Configuration conf = chart.getConfiguration();

		conf.setTitle("Distribuzione delle partite");
		conf.setSubTitle("periodo: settimanale");

		XAxis x = new XAxis();
		x.setType(AxisType.CATEGORY);
		conf.addxAxis(x);

		YAxis y = new YAxis();
		y.setMin(0);
		y.setTitle("");
		conf.addyAxis(y);

		Legend legend = new Legend();
		legend.setLayout(LayoutDirection.VERTICAL);
		legend.setBackgroundColor(new SolidColor("#FFFFFF"));
		legend.setAlign(HorizontalAlign.LEFT);
		legend.setVerticalAlign(VerticalAlign.TOP);
		legend.setX(100);
		legend.setY(70);
		legend.setFloating(true);
		legend.setShadow(true);
		conf.getLegend().setEnabled(false);

		Tooltip tooltip = new Tooltip();
		tooltip.setFormatter("Math.round(this.y * 100) /100 + ' partite registrate nella ' + this.point.name");
		conf.setTooltip(tooltip);

		PlotOptionsColumn plot = new PlotOptionsColumn();
		plot.setPointPadding(0.2);
		plot.setBorderWidth(0);

		DataSeries series = new DataSeries();
		series.setName("Partite registrate");
		for (MatchCount m : counts) {
			DataSeriesItem item = new DataSeriesItem(m.getWeek().intValue() + "°sett " + m.getYear().intValue(),
					m.getCount());
			item.setColor(new SolidColor("#fbc02d"));
			series.add(item);
		}
		conf.setSeries(series);
		chart.drawChart(conf);
		return chart;
	}
}
