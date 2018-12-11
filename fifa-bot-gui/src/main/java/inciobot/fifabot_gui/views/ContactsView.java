package inciobot.fifabot_gui.views;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CssLayout;

@SpringView(name = ContactsView.VIEW_NAME)
public class ContactsView extends FifaView {
	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "Contacts";

	public ContactsView() {
		view = Views.CONTACTS;
	}

	@Override
	protected void buildBodyLayout(CssLayout bodyLayout) {
		bodyLayout.addComponent(
				new ValueLink("Telegram:", FontAwesome.MOBILE_PHONE, "@FifaEueiBot", "http://telegram.me/EueiFifaBot"));
		bodyLayout.addComponent(
				new ValueLink("Email:", FontAwesome.ENVELOPE, "write-me@fifabot", "mailto:alex.incerti@euei.it"));
		replaceComponent(waitingLayout, bodyLayout);
	}
}
