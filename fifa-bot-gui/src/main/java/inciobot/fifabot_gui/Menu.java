package inciobot.fifabot_gui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

import inciobot.fifabot_gui.views.ContactsView;
import inciobot.fifabot_gui.views.DashboardView;
import inciobot.fifabot_gui.views.OverView;
import inciobot.fifabot_gui.views.Views;

public class Menu extends CssLayout implements Component {

	private static final long serialVersionUID = 1L;
	private CssLayout buttonLayout;
	private Map<Views, MenuButton> buttonsMap;

	public Menu() {
		buttonsMap = new HashMap<>();
		Responsive.makeResponsive(this);

		HorizontalLayout logoLayout = new HorizontalLayout();
		Responsive.makeResponsive(logoLayout);
		logoLayout.addStyleName("logo-layout");
		Image logo = ImageFactory.getLogoImage();
		logoLayout.addComponent(logo);
		// logo.setVisible(false);

		Component buttonLayout = buildMenuButtonLayout();

		addComponent(logoLayout);
		addComponent(buttonLayout);
		setStyleName("menu-layout");
	}

	private Component buildMenuButtonLayout() {
		buttonLayout = new CssLayout();
		Responsive.makeResponsive(buttonLayout);

		for (Views view : Views.values()) {
			MenuButton menuButton = new MenuButton(view);
			menuButton.addClickListener(l -> {
				Navigator navigator = MyUI.getCurrent().getNavigator();
				MenuButton button = (MenuButton) l.getButton();
				switch (button.getView()) {
				case OVERVIEW:
					navigator.navigateTo(OverView.VIEW_NAME);
					break;
				case DASHBOARD:
					navigator.navigateTo(DashboardView.VIEW_NAME);
					break;
				case CONTACTS:
					navigator.navigateTo(ContactsView.VIEW_NAME);
					break;
				default:
					break;
				}
			});
			buttonsMap.put(view, menuButton);
			buttonLayout.addComponent(menuButton);
		}

		buttonLayout.setStyleName("menu-buttons-layout");
		return buttonLayout;
	}

	public void selectMenuButton(Views view) {
		MenuButton button = buttonsMap.get(view);

		for (Views v : buttonsMap.keySet()) {
			buttonsMap.get(v).removeStyleName("menu-button-selected");
		}
		if (button != null) {
			button.addStyleName("menu-button-selected");
		}
	}
}
