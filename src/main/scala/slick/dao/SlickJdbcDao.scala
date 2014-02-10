package slick.dao

import scala.languageFeature.implicitConversions
import scala.slick.jdbc.JdbcBackend
import scala.slick.driver.JdbcProfile
import scala.slick.lifted.Column
import scala.slick.lifted


trait IdentifiableTable[I] {
  def id: Column[I]
}

trait SlickDao[R, I] {


  def extractId(row: R): Option[I]
  def withId(row: R, id: I): R

  def count: Int
  def save(row: R): R

  def delete(row: R): Boolean = {
    extractId(row) match {
      case Some(id) => deleteById(id)
      case None => false
    }
  }

  def deleteById(id: I): Boolean

  def findOptionById(id: I): Option[R]
  def findById(id: I): R = findOptionById(id).get

  def list : List[R]
  def pagedList(pageIndex: Int, limit: Int): List[R]
}

abstract class SlickJdbcDao[R, I:JdbcProfile#BaseColumnType]  extends SlickDao[R, I] {

  val profile:JdbcProfile
  implicit val session:JdbcBackend#Session

  import profile.simple._

  def query: lifted.TableQuery[_ <: Table[R] with IdentifiableTable[I]]

  def queryById(id: I): Query[Table[R], R] =
    query.filter(_.id === id)

  def count: Int = query.length.run

  def add(row: R): I =
    query.returning(query.map(_.id)).insert(row)


  def save(row: R): R =
    extractId(row) match {
      case Some(id) => queryById(id).update(row); row
      case None => withId(row, add(row))
    }



  def deleteById(id: I): Boolean = queryById(id).delete == 1

  def findOptionById(id: I): Option[R] = queryById(id).firstOption


  def list: List[R] = query.list

  def pagedList(pageIndex: Int, limit: Int): List[R] =
    query.drop(pageIndex).take(limit).run.toList

}
