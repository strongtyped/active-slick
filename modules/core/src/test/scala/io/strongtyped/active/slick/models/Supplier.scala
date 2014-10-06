package io.strongtyped.active.slick.models

case class Supplier(name: String, version: Long = 0, id: Option[Int] = None) extends Identifiable[Supplier] with Versionable[Supplier] {
  override type Id = Int

  override def withId(id: Id): Supplier = copy(id = Option(id))

  override def withVersion(version: Long): Supplier = copy(version = version)

}
