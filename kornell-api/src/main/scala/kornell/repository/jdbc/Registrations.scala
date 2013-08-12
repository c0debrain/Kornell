package kornell.repository.jdbc

import kornell.repository.TOs
import kornell.core.shared.to.RegistrationsTO
import kornell.repository.jdbc.SQLInterpolation._ 
import kornell.repository.Beans
import java.sql.ResultSet
import kornell.core.shared.data.Registration
import kornell.core.shared.data.Institution
import scala.collection.JavaConverters._
import kornell.core.shared.data.Person

object Registrations extends TOs with Beans {
  def toRegistration(rs:ResultSet) = newRegistration(
      rs.getString("person_uuid"),
      rs.getString("institution_uuid"),
      rs.getDate("termsAcceptedOn")
  )
  
  def toInstitution(rs:ResultSet) = newInstitution(
      rs.getString("institution_uuid"),
      rs.getString("name"),
      rs.getString("terms")
  )
  
  //TODO: Use map instead of foreach
  def unsigned(implicit person:Person): RegistrationsTO = {  
    val registrationsWithInstitutions = sql"""
	select r.person_uuid, r.institution_uuid, r.termsAcceptedOn, 
		i.name, i.terms
	from Registration r
	join Institution i  on r.institution_uuid = i.uuid
	where r.termsAcceptedOn is null
		and r.person_uuid=${person.getUUID}
	""".map { rs =>
	   val r = toRegistration(rs)
	   val i = toInstitution(rs)
	   (r, i)
    }.toMap
    
    newRegistrationsTO(registrationsWithInstitutions)
  }

  def signingNeeded(implicit person:Person):Boolean = 
	sql"""select count(*) 
	from Registration r
	join Institution i on r.institution_uuid=i.uuid
	where 
		i.terms is not null
	  and r.termsAcceptedOn is null
	  and r.person_uuid = ${person.getUUID}""".isPositive
    
    
}