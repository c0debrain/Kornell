package kornell.server.repository.jdbc

import javax.naming.InitialContext
import java.sql.ResultSet
import java.sql.Connection
import javax.naming.Context
import java.util.Date
import java.sql.{ Date => SQLDate }
import javax.sql.DataSource
import java.sql.PreparedStatement
import scala.collection.mutable.ListBuffer
import java.sql.{Date => SQLDate}

class PreparedStmt(query: String, params: List[Any]) {
  type ConnectionFactory = () => Connection

  lazy val JNDI: ConnectionFactory = () => {
    //TODO: lazy cache?
    val context = new InitialContext()
      .lookup("java:comp/env")
      .asInstanceOf[Context]
    context.lookup("jdbc/KornellDS")
      .asInstanceOf[DataSource]
      .getConnection
  }

  //TODO: Parameterize (Cake?) to allow testing outside container 
  lazy val connect = JNDI

  def connected[T](fun: Connection => T): T = {
    val conn = connect()
    try fun(conn)
    finally conn.close
  }

  def prepared[T](fun: PreparedStatement => T): T =
    connected { conn =>
      val pstmt = conn.prepareStatement(query.stripMargin.trim)

      def setQueryParam(param: Tuple2[Any, Int]) = param match {
        case (p: String, i) => pstmt.setString(i + 1, p)
        case (p: Integer, i) => pstmt.setInt(i + 1, p)
        case (p: Double, i) => pstmt.setDouble(i + 1, p)
        case (p: Date, i) => pstmt.setDate(i + 1, new SQLDate(p.getTime))
        //TODO: make this work: case (p: Entity, i) => pstmt.setString(i, p.getUUID) 
        case (p, i) => throw new RuntimeException(s"How should i set param ($p:${p.getClass})")
      }

      params
        .zipWithIndex
        .foreach(setQueryParam)

      try fun(pstmt)
      finally pstmt.close
    }

  def executeUpdate: Int = prepared { stmt => stmt.executeUpdate }

  def executeQuery[T](fun: ResultSet => T): T = prepared { stmt =>
    val rs = stmt.executeQuery
    try fun(rs)
    finally rs.close
  }

  def foreach(fun: ResultSet => Unit): Unit = executeQuery { rs =>
    while (rs.next) fun(rs)
  }

  //TODO: Consider converting to a Stream for lazy processing
  def map[T](fun: ResultSet => T): List[T] = {
    val xs:ListBuffer[T] = ListBuffer()
    foreach { rs => xs += fun(rs) }
    xs.toList
  }

  def first[T](implicit conversion:ResultSet => T):Option[T] = prepared { stmt =>
    stmt.setMaxRows(1)
  	val rs = stmt.executeQuery
  	if (! rs.next) None
  	else
  		try Option(conversion(rs))
  		finally rs.close
    
  }
  
  def isPositive:Boolean = executeQuery { rs =>
    rs.next && rs.getInt(1) > 0
  }
} 