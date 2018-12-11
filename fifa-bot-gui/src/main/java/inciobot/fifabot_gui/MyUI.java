package inciobot.fifabot_gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

import inciobot.fifabot_gui.constants.IConstants;
import inciobot.fifabot_gui.views.FifaView;
import inciobot.fifabot_gui.views.OverView;
import inciobot.fifabot_gui.views.Views;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@SpringUI
@Theme("mytheme")
@Push(transport = Transport.WEBSOCKET)
@Viewport("width=device-width, initial-scale=1")
public class MyUI extends UI {

	private static final long serialVersionUID = 1L;

	@Value("${INCIOBOT_BACKEND_ADDRESS}")
	private String backendAddress;

	@Autowired
	private SpringViewProvider viewProvider;

	private CssLayout mainLayout;
	private Panel bodyLayout;
	private Navigator navigator;
	private Menu menu;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		System.out.println("Backend address: " + backendAddress);
		setContent(buildLayout());
		getPage().setTitle(IConstants.PAGE_TITLE);

		navigator = new Navigator(this, bodyLayout);
		navigator.addProvider(viewProvider);
		navigator.addViewChangeListener(new ViewChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				FifaView view = (FifaView) event.getNewView();
				menu.selectMenuButton(view.getView());

				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				// TODO Auto-generated method stub

			}
		});

		navigator.navigateTo(OverView.VIEW_NAME);
	}

	public Navigator getNavigator() {
		return navigator;
	}

	public void setNavigator(Navigator navigator) {
		this.navigator = navigator;
	}

	private Component buildLayout() {
		mainLayout = new CssLayout();
		mainLayout.setSizeFull();
		mainLayout.setStyleName("main-layout");
		Responsive.makeResponsive(mainLayout);

		menu = new Menu();
		mainLayout.addComponent(menu);
		mainLayout.addComponent(buildBodyLayout(Views.OVERVIEW));

		return mainLayout;
	}

	private Component buildBodyLayout(Views view) {
		bodyLayout = new Panel();
		bodyLayout.setWidthUndefined();
		Responsive.makeResponsive(bodyLayout);
		bodyLayout.setStyleName("body-layout");

		return bodyLayout;
	}

	public String getBackendAddress() {
		return backendAddress;
	}

	public static MyUI getCurrent() {
		return (MyUI) UI.getCurrent();
	}
}
