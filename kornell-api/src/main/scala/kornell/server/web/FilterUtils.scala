package kornell.server.web

import javax.servlet.http.HttpServletRequest
import kornell.core.entity.Institution
import kornell.server.jdbc.repository.InstitutionsRepo

object FilterUtils {

  def getInstitution(req: HttpServletRequest, headerName: String): Option[Institution] = {
    if (req.getHeader(headerName) != null) {
      InstitutionsRepo.getByHostName(req.getHeader(headerName))
    } else if (req.getHeader("Referer") != null) {
      getInstitutionFromHeader(req.getHeader("Referer"))
    } else {
      None
    }
  }

  def getInstitutionFromHeader(header: String): Option[Institution] = {
    val pattern = """institution=([a-z]+)$""".r
    val institutionName = pattern findFirstIn header match {
      case Some(pattern(c)) => Option(c)
      case None => None
    }
    if (institutionName.isDefined) {
      InstitutionsRepo.getByName(institutionName.get)
    } else {
      None
    }
  }
}
