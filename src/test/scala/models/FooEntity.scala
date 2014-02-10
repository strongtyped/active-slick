package models

import slick.dao.IdentifiableTable
import scala.slick.driver.JdbcProfile

case class FooEntity(name:String, id:Option[Int] = None) extends Entity[FooEntity, Int] {
  override def withId(id: Int): FooEntity = copy(id = Option(id))
}

trait FooComponent  {

  val profile:JdbcProfile

  import profile.simple._

  class FooDao(implicit val session:Session) extends EntityDao[FooEntity, Int] {

    val profile = FooComponent.this.profile

    class Foos(tag: Tag) extends profile.simple.Table[FooEntity](tag, "person") with IdentifiableTable[Int] {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def name = column[String]("name")
      def * = (name, id.?) <> (FooEntity.tupled, FooEntity.unapply)
    }

    def query = TableQuery[Foos]
  }
}