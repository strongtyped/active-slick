package models

import scala.slick.driver.JdbcProfile
import slick.dao.IdentifiableTable

case class FooEntity(name:String, id:Option[Int] = None) extends Entity[FooEntity, Int] {
  override def withId(id: Int): FooEntity = copy(id = Option(id))
}

trait FooComponent extends EntityComponent {

  import profile.simple._

  object FooDao extends EntityDao[FooEntity, Int] {

    def query = TableQuery[Foos]

    class Foos(tag: Tag) extends profile.simple.Table[FooEntity](tag, "person") with IdentifiableTable[Int] {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def name = column[String]("name")
      def * = (name, id.?) <> (FooEntity.tupled, FooEntity.unapply)
    }
  }

}