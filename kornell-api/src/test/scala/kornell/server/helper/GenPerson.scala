package kornell.server.helper

import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.repository.TOs
import kornell.server.api.UserResource

trait GenPerson 
	extends GenInstitution
	with AuthSpec{
/*
  val ur:UserResource = ???
  
  def newPerson() = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, randName, randEmail, randPassword,randCPF)
    ur.createUser(regreq).getPerson
  }
  
  val person = newPerson()
  
  val personUUID = person.getUUID
  
  def asPerson[T](fun : => T):T = asIdentity(personUUID)(fun)
*/
}