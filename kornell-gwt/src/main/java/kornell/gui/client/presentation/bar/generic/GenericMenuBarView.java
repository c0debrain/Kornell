package kornell.gui.client.presentation.bar.generic;


import kornell.gui.client.presentation.PresentationUtils;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

//HTTP

public class GenericMenuBarView extends Composite implements MenuBarView {
	interface MyUiBinder extends UiBinder<Widget, GenericMenuBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private EventBus eventBus;
	
	public GenericMenuBarView(EventBus eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		PresentationUtils.invisibleOnVitrine(eventBus,this);
	}



	@UiField
	FlowPanel menuBar;


	@Override
	public void setPresenter(Presenter presenter) {
	}
	
	
}
