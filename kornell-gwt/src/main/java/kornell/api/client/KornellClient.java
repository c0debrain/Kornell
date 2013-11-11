package kornell.api.client;

import kornell.core.entity.Institution;
import kornell.core.to.CourseTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.RegistrationsTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;

public class KornellClient extends RESTClient implements LogoutEventHandler {

	private KornellClient() {
		KornellClient.bindToWindow(this);
	}

	public void getCourses(Callback<CoursesTO> callback) {
		GET("/courses").sendRequest(null, callback);
	}

	// TODO: Is this safe?
	public void getUser(String username, Callback<UserInfoTO> cb) {
		GET("/user/" + username).sendRequest(null, cb);
	}

	public void checkUser(String username, String email, Callback<UserInfoTO> cb) {
		GET("/user/check/" + username + "/" + email).sendRequest(null, cb);
	}

	public void createUser(String data, Callback<UserInfoTO> cb) {
		PUT("/user/create/").sendRequest(data, cb);
	}

	public void sendWelcomeEmail(String userUUID, Callback<Void> cb) {
		GET("/email/welcome/" + userUUID).sendRequest(null, cb);
	}

	public void getCourseTO(String uuid, Callback<CourseTO> cb) {
		GET("/courses/" + uuid).sendRequest(null, cb);
	}

	public static KornellClient getInstance() {
		return new KornellClient();
	}

	public class RegistrationsClient {
		public void getUnsigned(Callback<RegistrationsTO> callback) {
			GET("/registrations").sendRequest("", callback);
		}
	}

	// TODO: extract those inner classes
	public class InstitutionClient {
		private String institutionName;

		public InstitutionClient(String institutionName) {
			this.institutionName = institutionName;
		}

		public void acceptTerms(Callback<Void> cb) {
			PUT("/institutions/" + institutionName).go(cb);
		}

		// TODO: remove this
		public void getInstitution(Callback<Institution> cb) {
			GET("/institutions/" + institutionName).sendRequest(null, cb);
		}
	}

	public RegistrationsClient registrations() {
		// TODO: Consider lifecycle
		return new RegistrationsClient();
	}

	public InstitutionClient institution(String uuid) {
		return new InstitutionClient(uuid);
	}

	public void placeChanged(final String token) {
		PUT("/user/placeChange").sendRequest(token, new Callback<Void>() {
			@Override
			public void ok(Void v) {
				GWT.log("Place changed to [" + token + "]");
			}
		});
	}

	public void notesUpdated(String courseUUID, String notes) {
		PUT("/enrollment/" + courseUUID + "/notesUpdated").sendRequest(notes,
				new Callback<Void>() {
					@Override
					public void ok(Void v) {
						GWT.log("notes updated");
					}
				});
	}

	@Override
	public void onLogout() {
		forgetCredentials();
	}

	private void forgetCredentials() {
		ClientProperties.remove("Authorization");
	}

	public CourseClient course(String courseUUID) {
		return new CourseClient(courseUUID);
	}

	static final EventsClient eventsClient = new EventsClient();

	public EventsClient events() {
		return eventsClient;
	}

	@SuppressWarnings("rawtypes")
	// TODO: Remove raw type
	public void check(String src, Callback callback) {
		HEAD(src).go(callback);
	}

	public String saySomething() {
		GWT.log("Bowties are cool");
		return "indeed they are";
	}

	public static native void bindToWindow(KornellClient kapi) /*-{
		$wnd.KAPI = {
			saySomething : function() {
				console
						.debug(kapi.@kornell.api.client.KornellClient::saySomething()());
			}
		}
	}-*/;

}
