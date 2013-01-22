package kornell.gui.client;

import kornell.api.client.KornellClient;
import kornell.gui.client.presentation.GlobalActivityMapper;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.activity.AtividadePlace;
import kornell.gui.client.presentation.activity.AtividadePresenter;
import kornell.gui.client.presentation.activity.AtividadeView;
import kornell.gui.client.presentation.activity.generic.GenericAtividadeView;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.generic.GenericActivityBarView;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;
import kornell.gui.client.scorm.API_1484_11;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class GenericClientFactoryImpl implements ClientFactory {
	/* History Management */
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(
			eventBus);
	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);
	private final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
			historyMapper);

	/* Activity Managers */
	private ActivityManager globalActivityManager;
	private ActivityManager appActivityManager;

	private SimplePanel appPanel;

	/* REST API Client */
	private final String apiURL = "http://localhost:8080/api";
	private final KornellClient client = new KornellClient(apiURL);

	/* Views */
	private GenericHomeView genericHomeView;
	private AtividadePresenter activityPresenter;

	/* GUI */
	SimplePanel shell = new SimplePanel();
	private GenericMenuBarView menuBarView;
	private GenericActivityBarView activityBarView;
	private FlowPanel wrapper;
	private API_1484_11 scormAPI;

	public GenericClientFactoryImpl() {
	}

	private void initActivityManagers() {
		initGlobalActivityManager();
	}

	private void initGlobalActivityManager() {
		globalActivityManager = new ActivityManager(new GlobalActivityMapper(
				this), eventBus);
		globalActivityManager.setDisplay(shell);
	}

	private void initHistoryHandler() {
		historyHandler.register(placeController, eventBus, new VitrinePlace());
		historyHandler.handleCurrentHistory();
	}

	private void initGUI() {
		wrapper = new FlowPanel();
		wrapper.addStyleName("wrapper");
		wrapper.add(getMenuBarView());
		wrapper.add(shell);
		wrapper.add(getActivityBarView());
		shell.addStyleName("wrapper");
		RootPanel.get().add(wrapper);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				layoutMenus();
			}
		});
		
		eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				layoutMenus();
				
			}
		});

		layoutMenus();
	}

	public void layoutMenus() {
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				int menuH = menuBarView.getOffsetHeight();
				int activityH = activityBarView.getOffsetHeight();
				int wrapperH = wrapper.getOffsetHeight();

				String height = wrapperH - (menuH + activityH) + "px";
				shell.setHeight(height);
			}
		});
	}

	private ActivityBarView getActivityBarView() {
		if (activityBarView == null)
			activityBarView = new GenericActivityBarView(eventBus);
		return activityBarView;
	}

	private MenuBarView getMenuBarView() {
		if (menuBarView == null)
			menuBarView = new GenericMenuBarView(eventBus);
		return menuBarView;
	}

	@Override
	public ClientFactory startApp() {
		initGUI();
		initActivityManagers();
		initHistoryHandler();
		initException();
		initSCORM();
		return this;
	}

	private void initSCORM() {
		scormAPI = new API_1484_11(eventBus);		
	}

	private void initException() {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(Throwable e) {
				System.out.println("** UNCAUGHT **");
				e.printStackTrace();
			}
		});
	}

	@Override
	public HomeView getHomeView() {
		if (genericHomeView == null) {
			genericHomeView = new GenericHomeView(this, eventBus,
					placeController, historyHandler, client, appPanel);
		}
		return genericHomeView;
	}

	@Override
	public VitrineView getVitrineView() {
		return new GenericVitrineView(placeController, client);
	}

	@Override
	public WelcomeView getWelcomeView() {
		return new GenericWelcomeView(client, placeController);
	}

	@Override
	public AtividadeView getActivityView() {
		return new GenericAtividadeView(eventBus);
	}

	@Override
	public AtividadePresenter getActivityPresenter(AtividadePlace place) {
		if (activityPresenter == null) {
			AtividadeView activityView = getActivityView();
			activityPresenter = new AtividadePresenter(activityView, place);
		}
		return activityPresenter;
	}

}
