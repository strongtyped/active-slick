package slick.dao

import scala.languageFeature.implicitConversions
import scala.slick.jdbc.JdbcBackend
import scala.slick.driver.JdbcProfile
import scala.slick.lifted.Column
import scala.slick.{profile, lifted}


trait IdentifiableTable[I] {
  def id: Column[I]
}

trait BaseDaoComponent {

  val profile: JdbcProfile

  import profile.simple._

  abstract class SlickJdbcDao[R, I: JdbcProfile#BaseColumnType] {

    type Session = JdbcBackend#Session

    def query: lifted.TableQuery[_ <: Table[R] with IdentifiableTable[I]]


    def extractId(row: R): Option[I]

    def withId(row: R, id: I): R

    def queryById(id: I): Query[Table[R], R] =
      query.filter(_.id === id)

    def count(implicit session: Session): Int = query.length.run

    def add(row: R)(implicit session: Session): I =
      query.returning(query.map(_.id)).insert(row)


    def save(row: R)(implicit session: Session): R =
      extractId(row) match {
        case Some(id) => queryById(id).update(row); row
        case None => withId(row, add(row))
      }

    def deleteById(id: I)(implicit session: Session): Boolean =
      queryById(id).delete == 1

    def findOptionById(id: I)(implicit session: Session): Option[R] =
      queryById(id).firstOption

    def findById(id: I)(implicit session: Session): R =
      findOptionById(id).get


    def pages(pageIndex: Int, limit: Int)(implicit session: Session): Seq[R] =
      query.drop(pageIndex).take(limit).run.toList

    abstract class ActiveRecord(val row: R) {
      def save(implicit s: Session): R = SlickJdbcDao.this.save(row)

      def remove(implicit s: Session): Boolean = {
        extractId(row) match {
          case Some(id) => SlickJdbcDao.this.deleteById(id)
          case None => false
        }

      }
    }

  }


}