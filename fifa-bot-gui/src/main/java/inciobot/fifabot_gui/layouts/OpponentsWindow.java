package inciobot.fifabot_gui.layouts;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import inciobot.fifabot_gui.views.SinglePlayerWindow;

public class OpponentsWindow extends SinglePlayerWindow {

	protected static final long serialVersionUID = 2599564452550145602L;
	protected ComboBox opponent2;

	public OpponentsWindow() {
		super();
	}

	protected void addListeners() {
		super.addListeners();
		opponent1.addValueChangeListener(l -> eventFirstItemChange());
		opponent2.addValueChangeListener(l -> eventSecondItemChange());
	}

	protected void eventSecondItemChange() {
		String value = (String) opponent2.getValue();

		if (usernames == null)
			return;

		usernames.stream().forEach(u -> {
			opponent1.addItem(u);
		});

		opponent1.removeItem(value);
	}

	protected void eventFirstItemChange() {
		String value = (String) opponent1.getValue();

		if (usernames == null)
			return;

		usernames.stream().forEach(u -> {
			opponent2.addItem(u);
		});

		opponent2.removeItem(value);
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

	protected void populateComponents() {
		super.populateComponents();
		opponent2.removeAllItems();

		if (usernames == null)
			return;

		usernames.stream().forEach(u -> {
			opponent2.addItem(u);
		});
	}

	protected VerticalLayout buildBodyLayout() {
		super.buildBodyLayout();
		opponent2 = new ComboBox("Player 2");
		opponent2.setNullSelectionAllowed(false);

		playersLayout.addComponent(opponent2);

		return bodyLayout;
	}

	public ComboBox getOpponent2() {
		return opponent2;
	}

	public ComboBox getOpponent1() {
		return opponent1;
	}
}
