package kornell.api.client;

import java.util.List;

import kornell.core.entity.Registration;
import kornell.core.entity.Role;
import kornell.core.entity.RoleType;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;

public class UserSession extends KornellClient {
	private static final String SEPARATOR = ".";
	private static final String PREFIX = "Kornell.v1.UserSession";
	private static UserSession current;

	private String personUUID;

	private UserInfoTO currentUser;

	@Deprecated
	public static UserSession setCurrentPerson(String personUUID,
			String institutionUUID) {
		current.personUUID = personUUID;
		return current;
	}


	public static void current(final Callback<UserSession> callback) {
		if(current == null){			
			current = UserSession.restore();
			GWT.log("Restoring User Session "+current.toString());
			current.getCurrentUser(new Callback<UserInfoTO>() {				
				@Override
				public void ok(UserInfoTO userInfo) {
					GWT.log("Welcome Back "+userInfo.getPerson().getFullName());
					current.setCurrentUser(userInfo);
					callback.ok(current);
				}		
				@Override
				public void unauthorized() {
					current.setCurrentUser(null);
					callback.ok(current);
				}
			});
		}else {
			GWT.log("Reusing Session from memory "+current.toString());
			callback.ok(current);
		}
	}

	private static UserSession restore() {
		GWT.log("\\0/ New User Session! \\0/");
		return new UserSession();
	}

	private UserSession() {
	}

	public String getItem(String key) {
		return ClientProperties.get(prefixed(key));
	}

	public void setItem(String key, String value) {
		ClientProperties.set(prefixed(key), value);
	}

	private String prefixed(String key) {
		return PREFIX + SEPARATOR + currentUser.getPerson().getUUID() + SEPARATOR + key;
	}

	@Deprecated
	public void getCurrentUser(final Callback<UserInfoTO> cb) {
		Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				GWT.log("Fetched user ["+user.getPerson().getUUID()+"]");
				current.setCurrentUser(user);
				cb.ok(user);
			};

			@Override
			protected void unauthorized() {
				cb.unauthorized();
			}

			@Override
			protected void forbidden() {
				cb.forbidden();
			}
		};
		if (currentUser == null)
			GET("/user").sendRequest(null, wrapper);
		else
			cb.ok(currentUser);
	}

	public void setCurrentUser(UserInfoTO user) {
		this.currentUser = user;
	};

	public void login(String username, String password, String confirmation,
			final Callback<UserInfoTO> callback) {
		final String auth = "Basic "
				+ ClientProperties.base64Encode(username + ":" + password);

		Callback<UserInfoTO> wrapper = new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				setCurrentUser(user);
				// TODO: https://github.com/Craftware/Kornell/issues/7
				ClientProperties.set("Authorization", auth);
				ClientProperties.set("X-KNL-A", auth);
				callback.ok(user);
			}

			@Override
			protected void unauthorized() {
				callback.unauthorized();
			}
		};
		confirmation = "".equals(confirmation) ? "NONE" : confirmation;
		GET("/user/login/" + confirmation)
			.addHeader("Authorization", auth)
			.addHeader("X-KNL-A", auth)
			.sendRequest(null, wrapper);

	}

	public boolean isAuthenticated() {		
		return currentUser != null;
	}

	public UserInfoTO getUserInfo() {
		return currentUser;
	}
	
	public boolean isPlatformAdmin() {
		return hasRole(RoleType.platformAdmin);
	}
	
	public boolean isInstitutionAdmin() {
		return hasRole(RoleType.institutionAdmin) || isPlatformAdmin();
	}
	
	public boolean isCourseClassAdmin() {
		return hasRole(RoleType.courseClassAdmin) || isInstitutionAdmin();
	}

	public boolean hasRole(RoleType type) {
		if (currentUser == null)
			return false;
		for (Role role : currentUser.getRoles()) {
			switch (role.getRoleType()) {
			case user: 
				if (RoleType.user.equals(type))
					return true;
				break;
			case courseClassAdmin:
				if (Dean.getInstance().getCourseClassTO() != null
						&& RoleType.courseClassAdmin.equals(type)
						&& role.getCourseClassAdminRole().getCourseClassUUID()
								.equals(Dean.getInstance().getCourseClassTO().getCourseClass().getUUID()))
					return true;
				break;
			case institutionAdmin:
				if (RoleType.institutionAdmin.equals(type)
						&& role.getInstitutionAdminRole().getInstitutionUUID()
								.equals(Dean.getInstance().getInstitution().getUUID()))
					return true;
				break;
			case platformAdmin:
				if (RoleType.platformAdmin.equals(type))
					return true;
				break;
			default:
				break;
			}
		}
		return false;
	}

}
