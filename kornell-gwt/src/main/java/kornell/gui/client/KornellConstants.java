package kornell.gui.client;

import com.google.gwt.i18n.client.Constants;

public interface KornellConstants extends Constants {
	
	@DefaultStringValue("skins/first/icons/")
	String imagesPath();

	/**
	 * 
	 * GenericWelcomeView
	 * 
	 */
	@DefaultStringValue("All Courses")
	String allCourses();

	@DefaultStringValue("Courses finished:")
	String coursesFinished();

	@DefaultStringValue("Finished")
	String finished();

	@DefaultStringValue("Courses to acquire:")
	String coursesToAcquire();

	@DefaultStringValue("To Acquire")
	String toAcquire();

	@DefaultStringValue("Courses to start:")
	String coursesToStart();

	@DefaultStringValue("To Start")
	String toStart();

	@DefaultStringValue("Courses in progress:")
	String coursesInProgress();

	@DefaultStringValue("In progress")
	String inProgress();



	/**
	 * 
	 * GenericMenuLeftItemView
	 * 
	 */
	@DefaultStringValue(":")
	String colon();

	@DefaultStringValue("Network Activities")
	String networkActivities();

	@DefaultStringValue("Messages")
	String messages();

	@DefaultStringValue("Forums")
	String forums();

	@DefaultStringValue("Complete")
	String complete();

	@DefaultStringValue("Courses")
	String courses();

	@DefaultStringValue("Notifications")
	String notifications();

	@DefaultStringValue("My Participation")
	String myParticipation();

	@DefaultStringValue("Profile")
	String profile();



	/**
	 * 
	 * GenericCourseSummaryView
	 * 
	 */
	@DefaultStringValue("Course finished.")
	String courseFinished();
	@DefaultStringValue("Certificate")
	String certificate();



	/**
	 * 
	 * GenericCourseDetailsView
	 * 
	 */
	@DefaultStringValue("Course details: ")
	String detailsHeader();
	
	@DefaultStringValue("About the course")
	String btnAbout();
	
	@DefaultStringValue("Topics")
	String btnTopics();
	
	@DefaultStringValue("Certification")
	String btnCertification();
	
	@DefaultStringValue("General view")
	String btnAboutInfo();
	
	@DefaultStringValue("Main topics covered on this course")
	String btnTopicsInfo();
	
	@DefaultStringValue("Evaluations and tests")
	String btnCertificationInfo();
	
	@DefaultStringValue("Close Details")
	String closeDetails();

	@DefaultStringValue("Topic")
	String topic();

	
	/**
	 * 
	 * GenericBarView
	 */
	
	@DefaultStringValue("course")
	String course();
	
	@DefaultStringValue("details")
	String details();
	
	@DefaultStringValue("library")
	String library();
	
	@DefaultStringValue("forum")
	String forum();
	
	@DefaultStringValue("chat")
	String chat();
	
	@DefaultStringValue("specialists")
	String specialists();
	
	@DefaultStringValue("notes")
	String notes();
	
	@DefaultStringValue("back")
	String back();
	
	@DefaultStringValue("next")
	String next();
	
	@DefaultStringValue("previous")
	String previous();

	


}