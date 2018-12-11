package inciobot.fifabot_gui.views;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ValueBlock extends HorizontalLayout {

	private static final long serialVersionUID = 1L;
	private String title;
	private FontAwesome icon;
	private Object value;

	public ValueBlock(String title, FontAwesome icon, Object value) {
		this.icon = icon;
		this.title = title;
		this.value = value;

		buildLayout();
	}

	private void buildLayout() {
		setSpacing(true);
		addStyleName("value-block");

		Label titleLabel = new Label(icon.getHtml() + " " + title);
		titleLabel.setContentMode(ContentMode.HTML);
		titleLabel.addStyleName("value-title");
		titleLabel.setSizeUndefined();

		Label valueLabel = new Label(value.toString());
		valueLabel.addStyleName("value-label");
		valueLabel.setSizeUndefined();

		addComponents(titleLabel, valueLabel);
		setExpandRatio(titleLabel, 1.0f);
		setExpandRatio(valueLabel, 3.0f);
	}
}
