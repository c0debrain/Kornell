package kornell.server.jdbc
import scala.language.implicitConversions
import java.sql.ResultSet
import java.util.logging.Logger
import kornell.core.entity.Assessment
import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseClassState
import kornell.core.entity.CourseVersion
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.RoleType
import kornell.core.to.ChatThreadMessageTO
import kornell.core.to.CourseClassTO
import kornell.core.to.EnrollmentTO
import kornell.core.to.UnreadChatThreadTO
import kornell.server.repository.Entities._
import kornell.server.repository.Entities
import kornell.server.repository.TOs._
import kornell.server.repository.TOs
import kornell.core.entity.RegistrationEnrollmentType
import kornell.core.to.CourseVersionTO
import kornell.core.entity.BillingType

/**
 * Classes in this package are Data Access Objects for JDBC Databases
 *
 * This is the naming convention for methods in this package:
 *
 * first() => Return Option[T], when the result may not exist
 * get() => Returns T, presuming the result exists
 * find() => Return Collection[T], as the result of a query
 */
package object repository {
  val logger = Logger.getLogger("kornell.server.jdbc.repository")
  
  //TODO:  Move to Repositories
  implicit def toInstitution(rs:ResultSet):Institution = 
    newInstitution(rs.getString("uuid"), 
        rs.getString("name"),  
        rs.getString("fullName"), 
        rs.getString("terms"),
        rs.getString("assetsURL"),
        rs.getString("baseURL"),
        rs.getBoolean("demandsPersonContactDetails"),
        rs.getBoolean("validatePersonContactDetails"),
        rs.getBoolean("allowRegistration"),
        rs.getBoolean("allowRegistrationByUsername"),
        rs.getDate("activatedAt"),
        rs.getString("skin"),
        BillingType.valueOf(rs.getString("billingType")))
  
  implicit def toCourseClass(r: ResultSet): CourseClass = 
    newCourseClass(r.getString("uuid"), r.getString("name"), 
        r.getString("courseVersion_uuid"), r.getString("institution_uuid"),
        r.getBigDecimal("requiredScore"), r.getBoolean("publicClass"), 
        r.getBoolean("overrideEnrollments"),
        r.getBoolean("invisible"), r.getInt("maxEnrollments"), 
        r.getDate("createdAt"), r.getString("createdBy"), 
        CourseClassState.valueOf(r.getString("state")), 
        RegistrationEnrollmentType.valueOf(r.getString("registrationEnrollmentType")),
        r.getString("institutionRegistrationPrefix")) 

  implicit def toCourse(rs: ResultSet): Course = newCourse(
    rs.getString("uuid"),
    rs.getString("code"),
    rs.getString("title"),
    rs.getString("description"),
    rs.getString("infoJson"),
    rs.getString("institutionUUID"))    

  implicit def toCourseVersion(rs: ResultSet): CourseVersion = newCourseVersion(
    rs.getString("uuid"), 
    rs.getString("name"), 
    rs.getString("course_uuid"), 
    rs.getString("repository_uuid"), 
    rs.getDate("versionCreatedAt"),
    rs.getString("distributionPrefix"),
    rs.getString("contentSpec"),
    rs.getBoolean("disabled"))    
  
  implicit def toCourseClassTO(rs: ResultSet): CourseClassTO = {
    val course = newCourse(
        rs.getString("courseUUID"), 
		    rs.getString("code"), 
		    rs.getString("title"),
		    rs.getString("description"), 
		    rs.getString("infoJson"));

    val version = newCourseVersion(
        rs.getString("courseVersionUUID"), 
		    rs.getString("courseVersionName"), 
		    rs.getString("courseUUID"), 
		    rs.getString("repositoryUUID"), 
		    rs.getDate("versionCreatedAt"),
		    rs.getString("distributionPrefix"),
		    rs.getString("contentSpec"),
		    rs.getBoolean("disabled"));

    val clazz = newCourseClass(
        rs.getString("courseClassUUID"),
		    rs.getString("courseClassName"), 
		    rs.getString("courseVersionUUID"), 
		    rs.getString("institutionUUID"),
		    rs.getBigDecimal("requiredScore"),
		    rs.getBoolean("publicClass"),
        rs.getBoolean("overrideEnrollments"),
        rs.getBoolean("invisible"),
		    rs.getInt("maxEnrollments"),
		    rs.getDate("createdAt"),
		    rs.getString("createdBy"), 
        CourseClassState.valueOf(rs.getString("state")), 
        RegistrationEnrollmentType.valueOf(rs.getString("registrationEnrollmentType")),
		    rs.getString("institutionRegistrationPrefix"));
    		
    TOs.newCourseClassTO(course, version, clazz)
  }
  
  implicit def toCourseVersionTO(rs: ResultSet): CourseVersionTO = {
    val courseVersion = newCourseVersion(
        rs.getString("courseVersionUUID"), 
        rs.getString("courseVersionName"), 
        rs.getString("courseUUID"), 
        rs.getString("repositoryUUID"), 
        rs.getDate("versionCreatedAt"), 
        rs.getString("distributionPrefix"), 
        rs.getString("contentSpec"), 
        rs.getBoolean("courseVersionDisabled"))
        
    val course = newCourse(
        rs.getString("courseUUID"), 
        rs.getString("courseCode"), 
        rs.getString("courseTitle"), 
        rs.getString("courseDescription"), 
        rs.getString("infoJson"),
        rs.getString("institutionUUID"))
        
    TOs.newCourseVersionTO(course, courseVersion)
  }
  
  implicit def toEnrollment(rs: ResultSet): Enrollment = {
    newEnrollment(
      rs.getString("uuid"),
      rs.getDate("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getInt("progress"),
      rs.getString("notes"),      
      EnrollmentState.valueOf(rs.getString("state")),
      rs.getString("lastProgressUpdate"),
      Option(rs.getString("assessment"))
      	.map(Assessment.valueOf)
      	.getOrElse(null),
      rs.getString("lastAssessmentUpdate"),
      rs.getBigDecimal("assessmentScore"),
      rs.getString("certifiedAt")
    )
  }
    

  implicit def toEnrollmentTO(rs: ResultSet): EnrollmentTO = {
    val enrollment = newEnrollment(
      rs.getString("uuid"),
      rs.getDate("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getInt("progress"),
      rs.getString("notes"),      
      EnrollmentState.valueOf(rs.getString("state")),
      rs.getString("lastProgressUpdate"),
      Option(rs.getString("assessment"))
      	.map(Assessment.valueOf)
      	.getOrElse(null),
      rs.getString("lastAssessmentUpdate"),
      rs.getBigDecimal("assessmentScore"),
      rs.getString("certifiedAt")
    )
    
    TOs.newEnrollmentTO(enrollment, rs.getString("personUUID"), rs.getString("fullName"), rs.getString("username"))
  }
	
	implicit def toPerson(rs:ResultSet):Person = newPerson(
	    rs.getString("uuid"),
	    rs.getString("fullName"), 
	    rs.getString("lastPlaceVisited"),
	    rs.getString("email"),
	    rs.getString("company"),
	    rs.getString("title"),
	    rs.getString("sex"),
	    rs.getDate("birthDate"),
	    rs.getString("confirmation"),
	    rs.getString("telephone"),
	    rs.getString("country"),
	    rs.getString("state"),
	    rs.getString("city"),
	    rs.getString("addressLine1"),
	    rs.getString("addressLine2"),
	    rs.getString("postalCode"),
	    rs.getString("cpf"),
	    rs.getString("institutionUUID"),
	    rs.getString("termsAcceptedOn"))

  implicit def toRole(rs: java.sql.ResultSet): kornell.core.entity.Role = {
    val roleType = RoleType.valueOf(rs.getString("role"))
    val role = roleType match {
      case RoleType.user => Entities.newUserRole
      case RoleType.platformAdmin => Entities.newRoleAsPlatformAdmin(rs.getString("person_uuid"))
      case RoleType.institutionAdmin => Entities.newInstitutionAdminRole(rs.getString("person_uuid"), rs.getString("institution_uuid"))
      case RoleType.courseClassAdmin => Entities.newCourseClassAdminRole(rs.getString("person_uuid"), rs.getString("course_class_uuid"))
    }
    role
  }
	
	implicit def toUnreadChatThreadTO(rs:ResultSet):UnreadChatThreadTO = newUnreadChatThreadTO(
	    rs.getString("unreadMessages"),
	    rs.getString("chatThreadUUID"), 
	    rs.getString("chatThreadName"),
	    rs.getString("courseClassUUID"))
	
	implicit def toChatThreadMessageTO(rs:ResultSet):ChatThreadMessageTO = newChatThreadMessageTO(
	    rs.getString("senderFullName"),
	    rs.getString("sentAt"), 
	    rs.getString("message"))
  

}
