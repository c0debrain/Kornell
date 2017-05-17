package kornell.gui.client.presentation.admin.courseversion.courseversions;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.forms.FormHelper;

public class AdminCourseVersionsPresenter implements AdminCourseVersionsView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseVersionsPresenter.class.getName());
	private AdminCourseVersionsView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeCtrl;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private CourseVersionsTO courseVersionsTO;
	private String pageSize = "20";
	private String pageNumber = "1";
	private String searchTerm = "";
	private String orderBy;
	private String asc;
	private EventBus bus;


	public AdminCourseVersionsPresenter(KornellSession session, EventBus bus,
			PlaceController placeCtrl, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeCtrl = placeCtrl;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		init();
	}

	private void init() {
		if (session.isInstitutionAdmin()) {			
			String orderByProperty = ClientProperties.get(getClientPropertyName("orderBy"));
			String ascProperty = ClientProperties.get(getClientPropertyName("asc"));
			this.orderBy = orderByProperty != null ? orderByProperty : "cv.name";
			this.asc = ascProperty != null ? ascProperty : "true";
			
			view = getView();
			view.setPresenter(this);
			bus.fireEvent(new ShowPacifierEvent(true));
			getCourseVersions();
      
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeCtrl.goTo(defaultPlace);
		}
	}

	private void getCourseVersions() {
		session.courseVersions().get(pageSize, pageNumber, searchTerm, orderBy, asc, new Callback<CourseVersionsTO>() {
  			@Override
  			public void ok(CourseVersionsTO to) {
  				courseVersionsTO = to;
  				view.setCourseVersions(courseVersionsTO.getCourseVersionTOs(), to.getCount(), to.getSearchCount());
  				bus.fireEvent(new ShowPacifierEvent(false));
  			}
  		});
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCourseVersionsView getView() {
		return viewFactory.getAdminCourseVersionsView();
	}

	@Override
	public String getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String getPageNumber() {
		return pageNumber;
	}

	@Override
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public String getSearchTerm() {
		return searchTerm;
	}

	@Override
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;	
	}

	@Override
	public void updateData() {
		getCourseVersions();
	}

	@Override
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public void setAsc(String asc) {
		this.asc = asc;
	}

	@Override
	public String getOrderBy() {
		return orderBy;
	}

	@Override
	public String getAsc() {
		return asc;
	}

	@Override
	public String getClientPropertyName(String property){
		return session.getAdminHomePropertyPrefix() +
				placeCtrl.getWhere().toString() + ClientProperties.SEPARATOR +
				property;
	}
	
}