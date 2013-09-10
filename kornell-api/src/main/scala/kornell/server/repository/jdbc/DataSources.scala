package kornell.server.repository.jdbc

import java.sql.DriverManager
import javax.naming.NoInitialContextException
import javax.naming.InitialContext
import javax.naming.Context
import javax.sql.DataSource

object DataSources {
  lazy val KornellDS = {
    val context = new InitialContext()
      .lookup("java:comp/env")
      .asInstanceOf[Context]
    context.lookup("jdbc/KornellDS")
      .asInstanceOf[DataSource]
  }

  lazy val JNDI: ConnectionFactory = () => {
    try {
      KornellDS.getConnection
    } catch {
      case e: NoInitialContextException => null
    }
  }

  lazy val LOCAL: ConnectionFactory = () => {
    DriverManager.getConnection("jdbc:mysql:///ebdb", "kornell", "42kornell73")
  }
}