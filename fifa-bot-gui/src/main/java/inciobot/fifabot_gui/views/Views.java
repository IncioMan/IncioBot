package inciobot.fifabot_gui.views;

import com.vaadin.server.FontAwesome;

public enum Views {

	OVERVIEW("Overview", FontAwesome.SEARCH), DASHBOARD("Dashboard", FontAwesome.BAR_CHART), CONTACTS("Contacts",
			FontAwesome.ENVELOPE);

	private Views(String value, FontAwesome icon) {
		this.value = value;
		this.icon = icon;
	}

	private final String value;
	private final FontAwesome icon;

	public String getValue() {
		return value;
	}

	public FontAwesome getIcon() {
		return icon;
	}
}
