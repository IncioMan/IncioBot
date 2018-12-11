package inciobot.fifabot_gui.views;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

public class ValueLink extends HorizontalLayout {

	private static final long serialVersionUID = 1L;
	private String title;
	private FontAwesome icon;
	private Object value;
	private String linkAddress;

	public ValueLink(String title, FontAwesome icon, Object value, String linkAddress) {
		this.icon = icon;
		this.title = title;
		this.value = value;
		this.linkAddress = linkAddress;

		buildLayout();
	}

	private void buildLayout() {
		setSpacing(true);
		addStyleName("value-block");

		Label titleLabel = new Label(icon.getHtml() + " " + title);
		titleLabel.setContentMode(ContentMode.HTML);
		titleLabel.addStyleName("value-title");
		titleLabel.setSizeUndefined();

		Link valueLink = new Link(value.toString(), new ExternalResource(linkAddress));
		valueLink.addStyleName("value-link");
		valueLink.addStyleName("value-label");
		valueLink.setSizeUndefined();

		addComponents(titleLabel, valueLink);
		setExpandRatio(titleLabel, 1.0f);
	}
}
