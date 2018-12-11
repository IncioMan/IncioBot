package inciobot.fifabot_gui;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import inciobot.fifabot_gui.views.Views;

public class MenuButton extends Button {

	private static final long serialVersionUID = 1L;
	private Views view;

	public MenuButton(Views view) {
		super(view.getValue(), view.getIcon());
		this.setView(view);
		addStyleName("button-menu-layout");
		addStyleName(ValoTheme.BUTTON_BORDERLESS);
	}

	public Views getView() {
		return view;
	}

	public void setView(Views view) {
		this.view = view;
	}

}
