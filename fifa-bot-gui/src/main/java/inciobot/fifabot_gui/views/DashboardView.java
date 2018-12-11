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
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import inciobot.bot_ci.ChallengeReportDto;
import inciobot.bot_ci.MatchCountPerWeekPerUser;
import inciobot.bot_ci.TimeAggregation;
import inciobot.fifabot_gui.MyUI;
import inciobot.fifabot_gui.charts.MatchPerWeekChart;
import inciobot.fifabot_gui.constants.ICustomeStyles;
import inciobot.fifabot_gui.layouts.OpponentsWindow;
import inciobot.fifabot_gui.utils.MonthConverter;

@SpringView(name = DashboardView.VIEW_NAME)
public class DashboardView extends FifaView {
	protected static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "Dashboard";

	protected String beAddress;
	protected Button opponentsButton, backButton;
	protected OpponentsWindow opponentsWindow;
	protected SinglePlayerWindow singlePlayerWindow;
	private Button singlePlayer;

	public DashboardView() {
		view = Views.DASHBOARD;
		opponentsWindow = new OpponentsWindow();
		singlePlayerWindow = new SinglePlayerWindow();
		// ChartOptions.get().setTheme(new ChartTheme());
		beAddress = MyUI.getCurrent().getBackendAddress();

		addListeners();
	}

	protected void addListeners() {
	}

	/**
	 * Method to handle the clicking of the opponent button
	 */
	protected void eventOpponentClick() {
		Window window = new Window("Configura la sfida", opponentsWindow);

		MyUI.getCurrent().addWindow(window);
		window.addStyleName(ICustomeStyles.OPPONENTS_WINDOW);
		window.setClosable(true);
		window.center();

		opponentsWindow.getConfirmButton().addClickListener(l -> eventOpponentConfirm());

		opponentsWindow.setWindow(window);
	}

	/**
	 * Method to handle the clicking of back button from challenge layout
	 */
	protected void eventBackButtonClick() {
		backButton.setVisible(false);
		replaceComponent(bodyLayout, waitingLayout);

		bodyLayout.removeAllComponents();
		buildBodyLayout(bodyLayout);
	}

	/**
	 * Method to handle the closing of the opponent challenge form
	 */
	protected void eventOpponentConfirm() {
		String opp1 = (String) opponentsWindow.getOpponent1().getValue();
		String opp2 = (String) opponentsWindow.getOpponent2().getValue();
		TimeAggregation time = (TimeAggregation) opponentsWindow.getTimeAggregation().getValue();

		if (opp1 == null || opp2 == null || time == null) {
			return;
		}

		backButton.setVisible(true);
		bodyLayout.removeAllComponents();
		bodyLayout.addComponent(waitingLayout);

		MyUI currentUI = MyUI.getCurrent();
		new Thread(() -> {
			currentUI.access(() -> {
				bodyLayout.removeAllComponents();
				bodyLayout.addComponent(createOpponentButtonLayout());
				bodyLayout.addComponent(createChallengeTitle(opp1, opp2));
				bodyLayout.addComponents(createGeneralOpponentLayout(retrieveGeneralChallengeData(opp1, opp2)));
				bodyLayout.addComponents(createOpponentLayout(time, retrieveChallengeData(opp1, opp2, time)));
				bodyLayout.setHeightUndefined();
				replaceComponent(waitingLayout, bodyLayout);
				currentUI.push();
			});
		}).start();

	}

	private Component createGeneralOpponentLayout(ChallengeReportDto generalChallengeData) {
		DataSeries dataSeries = new DataSeries();

		DataSeriesItem item = new DataSeriesItem("Wins", generalChallengeData.getWonPercentage());
		dataSeries.add(item);
		item.setColor(new SolidColor("green"));
		item = new DataSeriesItem("Draws", generalChallengeData.getDrawPercentage());
		dataSeries.add(item);
		item.setColor(new SolidColor("orange"));
		item = new DataSeriesItem("Defeats", generalChallengeData.getLostPercentage());
		dataSeries.add(item);
		item.setColor(new SolidColor("red"));

		return createColumnChart("Generali", "100%", "400px", dataSeries,
				"this.point.name + ': '+ Math.round(this.y * 100) /100 +' %'");
	}

	private ChallengeReportDto retrieveGeneralChallengeData(String opp1, String opp2) {
		ChallengeReportDto rateResponse = new RestTemplate().getForObject(MyUI.getCurrent().getBackendAddress()
				+ "/fifa-api/statistics/opponents/generalstats/?opponent1=" + opp1 + "&opponent2=" + opp2,
				ChallengeReportDto.class);
		return rateResponse;
	}

	/**
	 * Creates the layout which will fill the bodylayout. This layout will show
	 * statistics about a challenge between two opponents
	 * 
	 * @param opp1
	 *            Username of first opponent
	 * @param opp2
	 *            Username of second opponent
	 * @param time
	 *            Time basis
	 * @param retrieveChallengeData
	 *            challenge results from backend
	 * @return
	 */
	protected Component[] createOpponentLayout(TimeAggregation time, List<ChallengeReportDto> retrieveChallengeData) {
		List<Component> components = new ArrayList<>();

		switch (time) {
		case MONTH:
			retrieveChallengeData = orderPerMonth(retrieveChallengeData);
			break;
		case WEEK:
			retrieveChallengeData = orderPerWeek(retrieveChallengeData);
			break;
		default:
			break;
		}

		components.addAll(createCharts(time, retrieveChallengeData));

		return components.toArray(new Component[0]);
	}

	protected List<ChallengeReportDto> orderPerWeek(List<ChallengeReportDto> retrieveChallengeData) {
		retrieveChallengeData.sort((o1, o2) -> {
			if (o1.getYear().equals(o2.getYear())) {
				return o1.getWeek().compareTo(o2.getWeek());
			} else {
				return o1.getYear().compareTo(o2.getYear());
			}
		});

		return retrieveChallengeData;
	}

	protected List<ChallengeReportDto> orderPerMonth(List<ChallengeReportDto> retrieveChallengeData) {
		retrieveChallengeData.sort((o1, o2) -> {
			if (o1.getYear().equals(o2.getYear())) {
				return o1.getMonth().compareTo(o2.getMonth());
			} else {
				return o1.getYear().compareTo(o2.getYear());
			}
		});

		return retrieveChallengeData;
	}

	/**
	 * Creates charts for different aspect of the challenge
	 * 
	 * @param time
	 *            Time basis
	 * @param retrieveChallengeData
	 *            the data
	 * @return list of all charts components
	 */
	protected List<Component> createCharts(TimeAggregation time, List<ChallengeReportDto> retrieveChallengeData) {
		List<Component> components = new ArrayList<>();

		DataSeries series = new DataSeries("Wins");
		retrieveChallengeData.stream().forEach(c -> {
			DataSeriesItem item = null;
			switch (time) {
			case MONTH:
				item = new DataSeriesItem(MonthConverter.getMonth(c.getMonth()) + " " + c.getYear().intValue(),
						c.getWonPercentage());
				break;
			case WEEK:
				item = new DataSeriesItem(c.getWeek() + "° week of" + " " + c.getYear().intValue(),
						c.getWonPercentage());
				break;
			default:
				break;
			}
			series.add(item);
		});

		components.add(createCharts("Wins", "100%", "400px", series, 0d, 100d, "green",
				"this.series.name + ' in ' + this.point.name +': '+ Math.round(this.y * 100) /100 +' %'"));

		DataSeries series2 = new DataSeries("Defeats");
		retrieveChallengeData.stream().forEach(c -> {
			DataSeriesItem item = null;
			switch (time) {
			case MONTH:
				item = new DataSeriesItem(MonthConverter.getMonth(c.getMonth()) + " " + c.getYear().intValue(),
						c.getLostPercentage());
				break;
			case WEEK:
				item = new DataSeriesItem(c.getWeek() + "° week of" + " " + c.getYear().intValue(),
						c.getLostPercentage());
				break;
			default:
				break;
			}
			series2.add(item);
		});

		components.add(createCharts("Defeats", "100%", "400px", series2, 0d, 100d, "red",
				"this.series.name + ' in ' + this.point.name +': '+ Math.round(this.y * 100) /100 +' %'"));

		DataSeries series3 = new DataSeries("Draws");
		retrieveChallengeData.stream().forEach(c -> {
			DataSeriesItem item = null;
			switch (time) {
			case MONTH:
				item = new DataSeriesItem(MonthConverter.getMonth(c.getMonth()) + " " + c.getYear().intValue(),
						c.getDrawPercentage());
				break;
			case WEEK:
				item = new DataSeriesItem(c.getWeek() + "° week of" + " " + c.getYear().intValue(),
						c.getDrawPercentage());
				break;
			default:
				break;
			}
			series3.add(item);
		});

		components.add(createCharts("Draws", "100%", "400px", series3, 0d, 100d, null,
				"this.series.name + ' in ' + this.point.name +': '+ Math.round(this.y * 100) /100 +' %'"));

		DataSeries series4 = new DataSeries("Goals against");
		retrieveChallengeData.stream().forEach(c -> {
			DataSeriesItem item = null;
			switch (time) {
			case MONTH:
				item = new DataSeriesItem(MonthConverter.getMonth(c.getMonth()) + " " + c.getYear().intValue(),
						c.getGoalsAgainstAvg());
				break;
			case WEEK:
				item = new DataSeriesItem(c.getWeek() + "° week of" + " " + c.getYear().intValue(),
						c.getGoalsAgainstAvg());
				break;
			default:
				break;
			}
			series4.add(item);
		});

		components.add(createCharts("Goals against", "100%", "400px", series4, 0d, null, "orange",
				"this.series.name + ' in ' + this.point.name +': '+ Math.round(this.y * 100) /100 + ' (on avarage per match)'"));

		DataSeries series5 = new DataSeries("Goals scored");
		retrieveChallengeData.stream().forEach(c -> {
			DataSeriesItem item = null;
			switch (time) {
			case MONTH:
				item = new DataSeriesItem(MonthConverter.getMonth(c.getMonth()) + " " + c.getYear().intValue(),
						c.getGoalsScoredAvg());
				break;
			case WEEK:
				item = new DataSeriesItem(c.getWeek() + "° week of" + " " + c.getYear().intValue(),
						c.getGoalsScoredAvg());
				break;
			default:
				break;
			}
			series5.add(item);
		});

		components.add(createCharts("Goals scored", "100%", "400px", series5, 0d, null, null,
				"this.series.name + ' in ' + this.point.name +': '+ Math.round(this.y * 100) /100 + ' (on avarage per match)'"));

		return components;
	}

	protected Component createCharts(String title, String width, String height, DataSeries series, Number min,
			Number max, String color, String tooltipText) {
		Chart chart = new Chart();

		chart.setHeight(height);
		chart.setWidth(width);

		Configuration configuration = chart.getConfiguration();
		configuration.getChart().setType(ChartType.SPLINE);
		// configuration.getChart().setMarginRight(130);
		configuration.getChart().setMarginBottom(100);

		configuration.getTitle().setText(title);

		YAxis yAxis = configuration.getyAxis();
		yAxis.setMin(min);
		yAxis.setMax(max);
		// yAxis.setTitle(new AxisTitle("Week"));
		yAxis.getTitle().setAlign(VerticalAlign.MIDDLE);

		XAxis xAxis = configuration.getxAxis();
		xAxis.setType(AxisType.CATEGORY);

		configuration.getxAxis().getLabels().setRotation(-3);

		Style style = new Style();
		style.setFontSize("8px");
		configuration.getxAxis().getLabels().setStyle(style);

		PlotOptionsSpline plotOptions = new PlotOptionsSpline();
		if (color != null) {
			plotOptions.setColor(new SolidColor(color));
		}
		plotOptions.getDataLabels().setEnabled(false);
		configuration.setPlotOptions(plotOptions);

		Tooltip tooltip = new Tooltip();
		tooltip.setFormatter(tooltipText);
		configuration.setTooltip(tooltip);

		configuration.getLegend().setEnabled(false);
		configuration.addSeries(series);
		chart.drawChart(configuration);

		return chart;
	}

	protected Component createChallengeTitle(String opp1, String opp2) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName("body-title-layout");

		title = new Label();
		title.setValue(opp1 + " VS " + opp2);
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		layout.addComponent(title);

		return layout;
	}

	/**
	 * Retrieve challege data from backend
	 * 
	 * @param opp1
	 *            Username of first opponent
	 * @param opp2
	 *            Username of second opponent
	 * @param time
	 *            Time basis
	 * @return the challenge data
	 */
	protected List<ChallengeReportDto> retrieveChallengeData(String opp1, String opp2, TimeAggregation time) {
		ResponseEntity<List<ChallengeReportDto>> rateResponse = new RestTemplate().exchange(
				beAddress + "/fifa-api/statistics/opponents/?opponent1=" + opp1 + "&opponent2=" + opp2
						+ "&timeAggregation=" + time,
				HttpMethod.GET, null, new ParameterizedTypeReference<List<ChallengeReportDto>>() {
				});
		List<ChallengeReportDto> counts = rateResponse.getBody();
		return counts;
	}

	@Override
	protected void buildBodyLayout(CssLayout bodyLayout) {
		bodyLayout.addComponent(createOpponentButtonLayout());

		MyUI currentUI = MyUI.getCurrent();
		new Thread(() -> {
			currentUI.access(() -> {
				// bodyLayout.addComponent(createColumnChart());
				// bodyLayout.addComponent(createLineChart());
				bodyLayout.addComponent(createLineChartResult());
				bodyLayout.addComponent(createLineChartGoals());
				replaceComponent(waitingLayout, bodyLayout);
				currentUI.push();
			});
		}).start();
	}

	protected Component createOpponentButtonLayout() {
		if (opponentsButton == null) {
			opponentsButton = new Button();
			opponentsButton.addStyleName(ICustomeStyles.FLAT_EUEI_BUTTON);
			opponentsButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			opponentsButton.setIcon(FontAwesome.GROUP);
			opponentsButton.addClickListener(l -> eventOpponentClick());
		}
		if (singlePlayer == null) {
			singlePlayer = new Button();
			singlePlayer.addStyleName(ICustomeStyles.FLAT_EUEI_BUTTON);
			singlePlayer.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			singlePlayer.setIcon(FontAwesome.USER);
			singlePlayer.addClickListener(l -> eventSinglePlayerClick());
		}
		if (backButton == null) {
			backButton = new Button();
			backButton.addStyleName(ICustomeStyles.FLAT_EUEI_BUTTON);
			backButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			backButton.setIcon(FontAwesome.ARROW_LEFT);
			backButton.setVisible(false);
			backButton.addClickListener(l -> eventBackButtonClick());
		}

		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(backButton);
		layout.setComponentAlignment(backButton, Alignment.MIDDLE_LEFT);
		layout.addComponent(singlePlayer);
		layout.setComponentAlignment(singlePlayer, Alignment.MIDDLE_RIGHT);
		layout.addComponent(opponentsButton);
		layout.setComponentAlignment(opponentsButton, Alignment.MIDDLE_RIGHT);
		layout.setExpandRatio(singlePlayer, 1.0f);
		layout.setWidth("100%");

		layout.addStyleName(ICustomeStyles.OPPONENTS_BUTTONS_LAYOUT);
		layout.setMargin(true);
		return layout;
	}

	private void eventSinglePlayerClick() {
		Window window = new Window("Configura la sfida", singlePlayerWindow);

		MyUI.getCurrent().addWindow(window);
		window.addStyleName(ICustomeStyles.OPPONENTS_WINDOW);
		window.setClosable(true);
		window.center();

		singlePlayerWindow.getConfirmButton().addClickListener(l -> eventSinglePlayerConfirm());

		singlePlayerWindow.setWindow(window);
	}

	private void eventSinglePlayerConfirm() {
		String opp1 = (String) singlePlayerWindow.getOpponent1().getValue();
		TimeAggregation time = (TimeAggregation) singlePlayerWindow.getTimeAggregation().getValue();

		if (opp1 == null || time == null) {
			return;
		}

		backButton.setVisible(true);
		bodyLayout.removeAllComponents();
		bodyLayout.addComponent(waitingLayout);

		MyUI currentUI = MyUI.getCurrent();
		new Thread(() -> {
			currentUI.access(() -> {
				bodyLayout.removeAllComponents();
				bodyLayout.addComponents(createSinglePlayerLayout(opp1, time, retrieveSinglePlayereData(opp1, time)));
				bodyLayout.setHeightUndefined();
				replaceComponent(waitingLayout, bodyLayout);
				currentUI.push();
			});
		}).start();

	}

	private Component[] createSinglePlayerLayout(String opp1, TimeAggregation time,
			List<ChallengeReportDto> retrieveSinglePlayereData) {
		List<Component> components = new ArrayList<>();

		switch (time) {
		case MONTH:
			retrieveSinglePlayereData = orderPerMonth(retrieveSinglePlayereData);
			break;
		case WEEK:
			retrieveSinglePlayereData = orderPerWeek(retrieveSinglePlayereData);
			break;
		default:
			break;
		}

		components.add(createOpponentButtonLayout());
		components.add(createChallengeTitle(opp1));
		components.addAll(createCharts(time, retrieveSinglePlayereData));

		return components.toArray(new Component[0]);
	}

	private Component createChallengeTitle(String opp1) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName("body-title-layout");

		title = new Label();
		title.setValue(opp1);
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		layout.addComponent(title);

		return layout;
	}

	private List<ChallengeReportDto> retrieveSinglePlayereData(String opp1, TimeAggregation time) {
		ResponseEntity<List<ChallengeReportDto>> rateResponse = new RestTemplate().exchange(
				MyUI.getCurrent().getBackendAddress() + "/fifa-api/statistics/player/?playerUsername=" + opp1
						+ "&timeAggregation=" + time,
				HttpMethod.GET, null, new ParameterizedTypeReference<List<ChallengeReportDto>>() {
				});
		List<ChallengeReportDto> counts = rateResponse.getBody();
		return counts;
	}

	protected Component createLineChartResult() {
		ResponseEntity<List<MatchCountPerWeekPerUser>> rateResponse = new RestTemplate().exchange(
				beAddress + "/fifa-api/statistics/matchdistribution/week-distr/result", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MatchCountPerWeekPerUser>>() {
				});
		List<MatchCountPerWeekPerUser> counts = rateResponse.getBody();

		return new MatchPerWeekChart(counts, "per giocatore - partite vinte/giocate");
	}

	protected Component createLineChartGoals() {
		ResponseEntity<List<MatchCountPerWeekPerUser>> rateResponse = new RestTemplate().exchange(
				beAddress + "/fifa-api/statistics/matchdistribution/week-distr/goals", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<MatchCountPerWeekPerUser>>() {
				});
		List<MatchCountPerWeekPerUser> counts = rateResponse.getBody();

		return new MatchPerWeekChart(counts, "per giocatore - Goals/partita");
	}

	protected Component createColumnChart(String title, String width, String height, DataSeries dataSeries,
			String formatter) {
		Chart chart = new Chart(ChartType.COLUMN);
		chart.setWidth(width);
		chart.setHeight(height);

		chart.setStyleName("dashboard-chart");

		Configuration conf = chart.getConfiguration();

		conf.getChart().setMarginBottom(100);
		conf.setTitle(title);

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
		tooltip.setFormatter(formatter);
		conf.setTooltip(tooltip);

		PlotOptionsColumn plot = new PlotOptionsColumn();
		plot.setPointPadding(0.2);
		plot.setBorderWidth(0);

		conf.setSeries(dataSeries);
		chart.drawChart(conf);
		return chart;
	}
}
