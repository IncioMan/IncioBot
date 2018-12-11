package inciobot.fifabot_gui.views;

import org.springframework.web.client.RestTemplate;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;

import inciobot.bot_ci.FifaStatus;
import inciobot.fifabot_gui.MyUI;

@SpringView(name = OverView.VIEW_NAME)
public class OverView extends FifaView {
	public static final String VIEW_NAME = "Overview";
	private static final long serialVersionUID = 1L;
	private String beAddress;

	public OverView() {
		setSizeFull();
		setStyleName("overview-view");
		view = Views.OVERVIEW;

		beAddress = (MyUI.getCurrent()).getBackendAddress();
	}

	@Override
	protected void loadData() {
		removeAllComponents();
		addComponent(titleLayout);
		addComponent(waitingLayout);

		final UI currentUI = UI.getCurrent();
		new Thread(new Runnable() {

			@Override
			public void run() {
				currentUI.access(new Runnable() {

					@Override
					public void run() {
						getStatus();
						replaceComponent(waitingLayout, bodyLayout);
						currentUI.push();
					}

					private void getStatus() {
						bodyLayout.removeAllComponents();
						FifaStatus status = null;

						try {
							status = new RestTemplate().getForObject(beAddress + "fifa-api/status/", FifaStatus.class);
						} catch (Exception e) {
							e.printStackTrace();
							status = new FifaStatus("offline");
						}
						bodyLayout.addComponent(
								new ValueBlock("Status", FontAwesome.QUESTION_CIRCLE, status.getStatus()));
						bodyLayout.addComponent(
								new ValueBlock("Matches (today)", FontAwesome.LIST, status.getTodayMatchesSize()));
						bodyLayout.addComponent(new ValueBlock("Goals   (today)", FontAwesome.SOCCER_BALL_O,
								status.getTodayGoalsScored()));
						bodyLayout.addComponent(
								new ValueBlock("Matches (all)", FontAwesome.LIST, status.getGeneralMatchesSize() / 6));
						bodyLayout.addComponent(new ValueBlock("Goals   (all)", FontAwesome.SOCCER_BALL_O,
								status.getGeneralGoalsScored() / 6));
					}
				});
			}
		}).start();
	}

	@Override
	protected void buildBodyLayout(CssLayout bodyLayout) {
		// TODO Auto-generated method stub

	}
}
