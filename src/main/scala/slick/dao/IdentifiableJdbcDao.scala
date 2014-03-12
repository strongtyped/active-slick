package slick.dao

trait IdentifiableJdbcDao[M, I] extends SlickJdbcDao[M, I]{
  import profile.simple._

  def query: TableQuery[_ <: Table[M] with IdentifiableTable[I]]
  
  def add(model: M): I = 
    query.returning(query.map(_.id)).insert(model)
}