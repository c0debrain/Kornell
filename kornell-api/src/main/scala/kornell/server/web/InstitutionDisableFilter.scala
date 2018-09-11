package kornell.server.web

import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class InstitutionDisableFilter extends Filter {

  val DOMAIN_HEADER = "X-KNL-DOMAIN"

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain): Unit =
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        if (hreq.getRequestURI.startsWith("/api") && !hreq.getRequestURI.equals("/api")) {
          doFilter(hreq, hres, chain)
        } else {
          chain.doFilter(hreq, hres)
        }
      }
    }

  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain): Unit = {
    val institution = FilterUtils.getInstitution(req, DOMAIN_HEADER)

    if (institution.isDefined && institution.get.isDisabled) {
      // shortcut request, institution is disabled
      resp.setStatus(503)
      resp.addHeader("X-KNL-MAINTENANCE", "true")
    } else {
      chain.doFilter(req, resp)
    }
  }



  override def init(cfg: FilterConfig): Unit = {}

  override def destroy(): Unit = {}
}