package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.{ChatThreadType, Institution}
import kornell.core.entity.role.Roles
import kornell.core.to.{InstitutionEmailWhitelistTO, InstitutionHostNamesTO, InstitutionRegistrationPrefixesTO, RolesTO}
import kornell.server.jdbc.repository.{ChatThreadsRepo, InstitutionEmailWhitelistRepo, InstitutionHostNameRepo, InstitutionRepo, RolesRepo}
import kornell.server.service.ContentService
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class InstitutionResource(uuid: String) {

  @GET
  @Produces(Array(Institution.TYPE))
  def get: Institution = {
    InstitutionRepo(uuid).get
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .or(isControlPanelAdmin, AccessDeniedErr()).get

  @PUT
  @Consumes(Array(Institution.TYPE))
  @Produces(Array(Institution.TYPE))
  def update(institution: Institution): Institution = {
    InstitutionRepo(uuid).update(institution)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .or(isControlPanelAdmin, AccessDeniedErr()).get

  @GET
  @Produces(Array(InstitutionRegistrationPrefixesTO.TYPE))
  @Path("registrationPrefixes")
  def getRegistrationPrefixes: InstitutionRegistrationPrefixesTO = {
    InstitutionRepo(uuid).getInstitutionRegistrationPrefixes
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr()).get

  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("admins")
  def updateAdmins(roles: Roles): Roles = {
    val r = new RolesRepo().updateInstitutionAdmins(uuid, roles)
    ChatThreadsRepo.updateParticipantsInCourseClassSupportThreadsForInstitution(uuid, ChatThreadType.SUPPORT)
    ChatThreadsRepo.updateParticipantsInCourseClassSupportThreadsForInstitution(uuid, ChatThreadType.INSTITUTION_SUPPORT)
    ChatThreadsRepo.updateParticipantsInCourseClassSupportThreadsForInstitution(uuid, ChatThreadType.PLATFORM_SUPPORT)
    r
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("admins")
  def getAdmins(@QueryParam("bind") bindMode: String): RolesTO = {
    new RolesRepo().getInstitutionAdmins(uuid, bindMode)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("publishers")
  def updatePublishers(roles: Roles): Roles = {
    new RolesRepo().updatePublishers(uuid, roles)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("publishers")
  def getPublishers(@QueryParam("bind") bindMode: String): RolesTO = {
    new RolesRepo().getPublishers(uuid, bindMode)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(InstitutionHostNamesTO.TYPE))
  @Produces(Array(InstitutionHostNamesTO.TYPE))
  @Path("hostnames")
  def updateHostnames(hostnames: InstitutionHostNamesTO): InstitutionHostNamesTO = {
    InstitutionHostNameRepo(uuid).updateHostnames(hostnames)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @GET
  @Produces(Array(InstitutionHostNamesTO.TYPE))
  @Path("hostnames")
  def getHostnames: InstitutionHostNamesTO = {
    InstitutionHostNameRepo(uuid).get
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(InstitutionEmailWhitelistTO.TYPE))
  @Produces(Array(InstitutionEmailWhitelistTO.TYPE))
  @Path("emailWhitelist")
  def updateEmailWhitelist(domains: InstitutionEmailWhitelistTO): InstitutionEmailWhitelistTO = {
    InstitutionEmailWhitelistRepo(uuid).updateDomains(domains)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @GET
  @Produces(Array(InstitutionEmailWhitelistTO.TYPE))
  @Path("emailWhitelist")
  def getEmailWhitelist: InstitutionEmailWhitelistTO = {
    InstitutionEmailWhitelistRepo(uuid).get
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get

  @GET
  @Path("uploadUrl")
  @Produces(Array("text/plain"))
  def getUploadUrl(@QueryParam("filename") filename: String): String = {
    ContentService.getInstitutionUploadUrl(uuid, filename)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
    .or(isInstitutionAdmin(uuid), AccessDeniedErr())
    .get
}

object InstitutionResource {
  def apply(uuid: String) = new InstitutionResource(uuid)
}
