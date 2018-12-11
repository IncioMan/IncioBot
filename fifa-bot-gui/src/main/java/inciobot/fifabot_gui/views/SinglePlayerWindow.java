package inciobot.fifabot_gui.views;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import inciobot.bot_ci.TimeAggregation;
import inciobot.fifabot_gui.MyUI;
import inciobot.fifabot_gui.constants.ICustomeStyles;

public class SinglePlayerWindow extends VerticalLayout {
	protected static final long serialVersionUID = 2599564452550145602L;
	protected Component waitingLayout;
	protected VerticalLayout bodyLayout;
	protected ComboBox timeAggregation;
	protected ComboBox opponent1;
	protected List<String> usernames;
	protected Button cancelButton;
	protected Button confirmButton;
	protected Window window;
	protected VerticalLayout playersLayout;

	public SinglePlayerWindow() {
		createLayout();
		addListeners();
		setSizeFull();
	}

	protected void addListeners() {
		confirmButton.addClickListener(l -> eventConfirmEvent());
		cancelButton.addClickListener(l -> eventCancelEvent());
	}

	protected void eventConfirmEvent() {
		close();
	}

	protected void eventCancelEvent() {
		close();
	}

	protected void close() {
		window.close();
	}

	protected void createLayout() {
		waitingLayout = buildWaitingLayout();
		bodyLayout = buildBodyLayout();
		addComponent(waitingLayout);

		MyUI currentUI = MyUI.getCurrent();
		new Thread(() -> {
			currentUI.access(() -> {
				try {
					retrieveUsernames();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				populateComponents();
				replaceComponent(waitingLayout, bodyLayout);
				currentUI.push();
			});
		}).start();
	}

	protected void populateComponents() {
		opponent1.removeAllItems();

		if (usernames == null)
			return;

		usernames.stream().forEach(u -> {
			opponent1.addItem(u);
		});
	}

	protected void retrieveUsernames() {
		ResponseEntity<List<String>> rateResponse = new RestTemplate().exchange(
				MyUI.getCurrent().getBackendAddress() + "/fifa-api/users/active/usernames", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		usernames = rateResponse.getBody();
	}

	protected VerticalLayout buildBodyLayout() {
		bodyLayout = new VerticalLayout();

		playersLayout = new VerticalLayout();

		opponent1 = new ComboBox("Player 1");
		opponent1.setNullSelectionAllowed(false);

		playersLayout.setSpacing(true);
		playersLayout.setSizeFull();
		playersLayout.addComponent(opponent1);

		timeAggregation = new ComboBox("Time basis");
		for (TimeAggregation time : TimeAggregation.values()) {
			timeAggregation.addItem(time);
			timeAggregation.setItemCaption(time, time.toString().toLowerCase());
		}

		timeAggregation.setNullSelectionAllowed(false);

		bodyLayout.addComponents(playersLayout, timeAggregation, createButtonsLayout());
		bodyLayout.setExpandRatio(playersLayout, 1.0f);

		bodyLayout.setSpacing(true);
		bodyLayout.setMargin(true);
		bodyLayout.setSizeFull();

		return bodyLayout;
	}

	protected Component createButtonsLayout() {
		HorizontalLayout layout = new HorizontalLayout();

		cancelButton = new Button("Cancel");
		confirmButton = new Button("Confirm");
		confirmButton.addStyleName(ICustomeStyles.EUEI_BUTTON);

		layout.addComponents(cancelButton, confirmButton);
		layout.setComponentAlignment(cancelButton, Alignment.MIDDLE_RIGHT);
		layout.setComponentAlignment(confirmButton, Alignment.MIDDLE_RIGHT);

		layout.setWidth("100%");
		layout.setSpacing(true);
		layout.setMargin(true);

		return layout;

	}

	protected Component buildWaitingLayout() {
		VerticalLayout cssLayout = new VerticalLayout();
		cssLayout.addStyleName("view-body");
		cssLayout.addStyleName("waiting-layout");
		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);

		cssLayout.setSizeFull();
		cssLayout.addComponent(progressBar);
		cssLayout.setComponentAlignment(progressBar, Alignment.MIDDLE_CENTER);
		return cssLayout;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public Button getConfirmButton() {
		return confirmButton;
	}

	public ComboBox getOpponent1() {
		return opponent1;
	}

	public ComboBox getTimeAggregation() {
		return timeAggregation;
	}

}
