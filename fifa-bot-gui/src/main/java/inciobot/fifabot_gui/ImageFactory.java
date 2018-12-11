package inciobot.fifabot_gui;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;

public class ImageFactory {

	public final static ThemeResource FIFA_EUEI_LOGO = new ThemeResource("img/fifaeuei.jpg");

	public static Image getLogoImage() {
		Image image = new Image("", FIFA_EUEI_LOGO);
		image.setStyleName("logo-title");
		return image;
	}

}
