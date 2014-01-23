package kornell.server.repository.jdbc
import kornell.server.repository.jdbc.SQLInterpolation.SQLHelper
import kornell.core.event.ActomEntered
import kornell.core.event.EnrollmentStateChanged
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.event.EventFactory
import kornell.core.event.EnrollmentStateChanged
import java.util.Date
import kornell.core.entity.EnrollmentState
import kornell.server.util.EmailService
import kornell.core.entity.CourseClass
import kornell.core.entity.Enrollment

object Events {
  val events = AutoBeanFactorySource.create(classOf[EventFactory])
  
  def newEnrollmentStateChanged = events.newEnrollmentStateChanged.as
  
  def logActomEntered(event: ActomEntered) = sql"""
    insert into ActomEntered(uuid,courseClass_uuid,person_uuid,actomKey,eventFiredAt)
    values(${event.getUUID},
  		   ${event.getCourseUUID},
           ${event.getFromPersonUUID},
		   ${event.getActomKey},
		   ${event.getEventFiredAt});
	""".executeUpdate

  /*
  def findActomsEntered(p: Person) = sql"""
  	
  """
  */
	
  def logEnrollmentStateChanged(uuid: String, eventFiredAt: Date, fromPersonUUID: String, 
      enrollmentUUID: String, fromState: EnrollmentState, toState: EnrollmentState) = {
	  
	  sql"""insert into EnrollmentStateChanged(uuid,eventFiredAt,person_uuid,enrollment_uuid,fromState,toState)
	    values(${uuid},
			   ${eventFiredAt},
	           ${fromPersonUUID},
	           ${enrollmentUUID},
	           ${fromState.toString},
			   ${toState.toString});
		""".executeUpdate
		
	  sql"""update Enrollment set state = ${toState.toString} where uuid = ${enrollmentUUID};
		""".executeUpdate
		
	  if(EnrollmentState.preEnrolled.equals(toState) || EnrollmentState.enrolled.equals(toState)){
	    val enrollment = Enrollments.byUUID(enrollmentUUID).get
	    val courseClass = CourseClasses(enrollment.getCourseClassUUID).get
	    val course = Courses.byCourseClassUUID(courseClass.getUUID).get
	    val institution = Institutions.byUUID(courseClass.getInstitutionUUID).get
	    EmailService.sendEmailEnrolled(enrollment.getPerson, institution, course)
	  }	
	  
  }
	
  def logEnrollmentStateChanged(event: EnrollmentStateChanged):Unit = 
    logEnrollmentStateChanged(event.getUUID,event.getEventFiredAt,event.getFromPersonUUID,
        event.getEnrollmentUUID,event.getFromState,event.getToState)
	
}