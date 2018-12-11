package inciobot.fifabot_gui.views;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

public abstract class FifaView extends CssLayout implements View {
	private static final long serialVersionUID = 1L;
	protected Views view;
	protected CssLayout bodyLayout;
	protected Component waitingLayout;
	protected HorizontalLayout titleLayout;
	protected Label title;

	public FifaView() {
	}

	@PostConstruct
	private void init() {
		buildLayout();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		loadData();
	}

	protected void loadData() {
		Notification notification = new Notification("Gli elementi grigi nella legenda!",
				"Sono disattivati, cliccali per visualizzarli sul grafico", Notification.Type.ASSISTIVE_NOTIFICATION);
		notification.setPosition(Position.BOTTOM_RIGHT);
		notification.show(UI.getCurrent().getPage());
	}

	private void buildLayout() {
		addStyleName("body-layout-css");

		titleLayout = buildTitleLayout(view.getValue(), view.getIcon());

		waitingLayout = buildWaitingLayout();

		bodyLayout = new CssLayout();
		bodyLayout.addStyleName("view-body");

		addComponents(titleLayout, waitingLayout);

		buildBodyLayout(bodyLayout);
	}

	protected abstract void buildBodyLayout(CssLayout bodyLayout);

	private Component buildWaitingLayout() {
		CssLayout cssLayout = new CssLayout();
		cssLayout.addStyleName("view-body");
		cssLayout.addStyleName("waiting-layout");
		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);

		cssLayout.setSizeFull();
		cssLayout.addComponent(progressBar);
		return cssLayout;
	}

	private HorizontalLayout buildTitleLayout(String titleValue, FontAwesome icon) {
		titleLayout = new HorizontalLayout();
		titleLayout.addStyleName("body-title-layout");

		title = new Label();
		title.setContentMode(ContentMode.HTML);
		title.setValue(icon.getHtml() + " " + titleValue);
		titleLayout.addComponent(title);

		return titleLayout;
	}

	public Views getView() {
		return view;
	}

}
