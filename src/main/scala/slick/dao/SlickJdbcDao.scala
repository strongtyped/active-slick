package slick.dao

import scala.languageFeature.implicitConversions
import scala.slick.jdbc.JdbcBackend
import scala.slick.driver.JdbcProfile
import scala.slick.lifted.Column
import scala.slick.{profile, lifted}


trait IdentifiableTable[I] {
  def id: Column[I]
}

trait SlickDao[M, I] {

  def count: Int
  def save(model: M): M

  def delete(model: M): Boolean

  def deleteById(id: I): Boolean

  def findOptionById(id: I): Option[M]
  def findById(id: I): M = findOptionById(id).get

  def list : List[M]
  def pagedList(pageIndex: Int, limit: Int): List[M]
}

trait SlickJdbcDao[M, I]  extends SlickDao[M, I] {

  val profile:JdbcProfile

  import profile.simple._

  implicit val session:JdbcBackend#Session

  def query: TableQuery[_ <: Table[M] with IdentifiableTable[I]]


  /**
   * Extracts the model Id of a arbitrary database model.
   * @param model a mapped model
   * @return an Some[I] if Id is filled, None otherwise
   */
  def extractId(model: M): Option[I]

  /**
   *
   * @param model a mapped model (usually without an assigned id).
   * @param id an id, usually generate by the database
   * @return a model M with an assigned Id.
   */
  def withId(model: M, id: I): M


  /**
   * Defined the base query to find object by id.
   *
   * @param id
   * @return
   */
  def queryById(id: I): Query[Table[M], M]

  def count: Int = query.length.run

  def add(model: M): I =
    query.returning(query.map(_.id)).insert(model)


  def save(model: M): M =
    extractId(model) match {
      case Some(id) => queryById(id).update(model); model
      case None => withId(model, add(model))
    }

  def delete(model:M) : Boolean =
    extractId(model) match {
      case Some(id) => deleteById(id)
      case None => false
    }


  def deleteById(id: I): Boolean = queryById(id).delete == 1

  def findOptionById(id: I): Option[M] = queryById(id).firstOption


  def list: List[M] = query.list

  def pagedList(pageIndex: Int, limit: Int): List[M] =
    query.drop(pageIndex).take(limit).run.toList

}
