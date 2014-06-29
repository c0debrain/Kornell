package kornell.gui.client.presentation.admin.home;

import java.util.List;

import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminHomeView extends IsWidget {
	public interface Presenter extends IsWidget {
		void changeEnrollmentState(EnrollmentTO object, EnrollmentState state);
		void changeCourseClassState(CourseClassTO courseClassTO, CourseClassState toState);
		boolean showActionButton(String actionName, EnrollmentTO enrollmentTO);
		void onAddEnrollmentButtonClicked(String fullName, String email);
		void onAddEnrollmentBatchButtonClicked(String txtAddEnrollmentBatch);
		void onGoToCourseButtonClicked();
		void onModalOkButtonClicked();
		void onUserClicked(EnrollmentTO enrollmentTO);
		void updateCourseClass(String courseClassUUID);
		List<EnrollmentTO> getEnrollments();
		void deleteEnrollment(EnrollmentTO enrollmentTO);
		void onGenerateCertificate(EnrollmentTO enrollmentTO);
	}

	void setPresenter(Presenter presenter);
	void setEnrollmentList(List<EnrollmentTO> enrollments);
	void showModal();
	void setModalErrors(String errors);
	void setCourseClassName(String courseClassName);
	void setCourseName(String courseName);
	void setCourseClasses(List<CourseClassTO> courseClasses);
	void setUserEnrollmentIdentificationType(Boolean enrollWithCPF);
	void setSelectedCourseClass(String uuid);
	void setHomeTabActive();
	void showEnrollmentsPanel(boolean visible);
	void buildAdminsView();
	void buildConfigView(boolean isCreationMode);
	void buildReportsView();
	void prepareAddNewCourseClass(boolean addingNewCourseClass);
}