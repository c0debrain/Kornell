package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.server.jdbc.SQL._
import kornell.core.util.UUID
import kornell.core.entity.CourseDetailsLibrary
import kornell.core.entity.CourseDetailsEntityType
import kornell.server.repository.TOs
import java.text.Normalizer

object CourseDetailsLibrariesRepo {

  def create(courseDetailsLibrary: CourseDetailsLibrary): CourseDetailsLibrary = {
    if (courseDetailsLibrary.getUUID == null){
      courseDetailsLibrary.setUUID(UUID.random)
    }
    val normalizedTitle = Normalizer.normalize(courseDetailsLibrary.getTitle, Normalizer.Form.NFC)
    sql"""
    | insert into CourseDetailsLibrary (uuid,title,entityType,entityUUID,`index`,description,size,path,uploadDate,fontAwesomeClassName) 
    | values(
    | ${courseDetailsLibrary.getUUID},
    | ${normalizedTitle},
    | ${courseDetailsLibrary.getEntityType.toString}, 
    | ${courseDetailsLibrary.getEntityUUID},
    | ${courseDetailsLibrary.getIndex},
    | ${courseDetailsLibrary.getDescription},
    | ${courseDetailsLibrary.getSize},
    | ${courseDetailsLibrary.getPath},
    | ${courseDetailsLibrary.getUploadDate},
    | ${courseDetailsLibrary.getFontAwesomeClassName})""".executeUpdate
    
    courseDetailsLibrary
  }
  
  def getForEntity(entityUUID: String, entityType: CourseDetailsEntityType) = {
    TOs.newCourseDetailsLibrariesTO(sql"""
      select * from CourseDetailsLibrary where entityUUID = ${entityUUID} and entityType = ${entityType.toString} order by `index`
    """.map[CourseDetailsLibrary](toCourseDetailsLibrary))
  }
  
  def moveUp(entityUUID: String, entityType: CourseDetailsEntityType, index: Int) = {
    val courseDetailsLibraries = CourseDetailsLibrariesRepo.getForEntity(entityUUID, entityType).getCourseDetailsLibraries
    if(index >= 0 && courseDetailsLibraries.size > 1){
      val currentLibrary = courseDetailsLibraries.get(index)
      val previousLibrary = courseDetailsLibraries.get(index - 1)
      
      currentLibrary.setIndex(index - 1)
      previousLibrary.setIndex(index)
      
      CourseDetailsLibraryRepo(currentLibrary.getUUID).update(currentLibrary)
      CourseDetailsLibraryRepo(previousLibrary.getUUID).update(previousLibrary)
    }
    ""
  }
  
  def moveDown(entityUUID: String, entityType: CourseDetailsEntityType, index: Int) = {
    val courseDetailsLibraries = CourseDetailsLibrariesRepo.getForEntity(entityUUID, entityType).getCourseDetailsLibraries
    if(index < (courseDetailsLibraries.size - 1) && courseDetailsLibraries.size > 1){
      val currentLibrary = courseDetailsLibraries.get(index)
      val nextLibrary = courseDetailsLibraries.get(index + 1)
      
      currentLibrary.setIndex(index + 1)
      nextLibrary.setIndex(index)
      
      CourseDetailsLibraryRepo(currentLibrary.getUUID).update(currentLibrary)
      CourseDetailsLibraryRepo(nextLibrary.getUUID).update(nextLibrary)
    }
    ""
  }
}