package io.strongtyped.active.slick

import io.strongtyped.active.slick.exceptions.{NoRowsAffectedException, RowNotFoundException}
import slick.ast.BaseTypedType
import slick.dbio.{FailureAction, SuccessAction}
import slick.driver.JdbcProfile
import io.strongtyped.active.slick.DBIOExtensions._

import scala.concurrent.ExecutionContext
import scala.language.{existentials, higherKinds, implicitConversions}
import scala.util.{Failure, Success}

abstract class EntityActions(val jdbcProfile: JdbcProfile)
  extends EntityActionsLike with JdbcProfileProvider {

  import jdbcProfile.api._

  implicit def baseTypedType: BaseTypedType[Id]

  type EntityTable <: Table[Entity]

  def tableQuery: TableQuery[EntityTable]

  def $id(table: EntityTable): Rep[Id]

  def idLens: Lens[Entity, Option[Id]]

  override def count: DBIO[Int] = tableQuery.size.result

  override def findById(id: Id): DBIO[Entity] =
    filterById(id).result.head

  override def findOptionById(id: Id): DBIO[Option[Entity]] =
    filterById(id).result.headOption

  override def save(entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    idLens.get(entity) match {
      // if has an Id, try to update it
      case Some(id) => update(entity)

      // if has no Id, try to add it
      case None => insert(entity).map { id =>
        idLens.set(entity, Option(id))
      }
    }
  }

  /**
   * Before insert interceptor method. This method is called just before record insertion.
   * The default implementation returns a successful DBIO wrapping the passed entity.
   *
   * Override this method if you need to add extract validation or modify the entity before insert.
   *
   * See examples bellow:
   * {{{
   * // simple validation example
   * override def beforeInsert(foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
   *    if (foo.name.trim.isEmpty) {
   *      DBIO.failed(new RuntimeException("Name can't be empty!!!")
   *    } else {
   *      DBIO.successful(foo)
   *    }
   * }
   * }}}
   *
   * {{{
   * // simple audit example
   * override def beforeInsert(foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
   *    // ensure that created and lastUpdate fields are updated just before insert
   *    val audited = foo.copy(created = DateTime.now, lastUpdate = DateTime.now)
   *    DBIO.successful(audited)
   * }
   * }}}
   */
  def beforeInsert(entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    // default implementation does nothing
    DBIO.successful(entity)
  }


  /**
   * Before update interceptor method. This method is called just before record update.
   * The default implementation returns a successful DBIO wrapping the passed entity.
   *
   *
   * Override this method if you need to add extract validation or modify the entity before update.
   *
   * See examples bellow:
   *
   * {{{
   * // simple validation example
   * override def beforeUpdate(id: Int, foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
   *    findById(id).flatMap { oldFoo =>
   *      if (oldFoo.name != foo.name) {
   *        DBIO.failed(new RuntimeException("Can't modify name!!!")
   *      } else {
   *        DBIO.successful(foo)
   *      }
   *    }
   * }
   * }}}
   *
   * {{{
   * // simple audit example
   * override def beforeUpdate(id: Int, foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
   *    // ensure that lastUpdate fields are updated just before update
   *    val audited = foo.copy(lastUpdate = DateTime.now)
   *    DBIO.successful(audited)
   * }
   * }}}
   */
  def beforeUpdate(id: Id, entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    // default implementation does nothing
    DBIO.successful(entity)
  }

  override def insert(entity: Entity)(implicit exc: ExecutionContext): DBIO[Id] = {
    val action = beforeInsert(entity).flatMap { preparedModel =>
      tableQuery.returning(tableQuery.map($id)) += preparedModel
    }
    // beforeInsert and '+=' must run on same tx
    action.transactionally
  }

  override def fetchAll(fetchSize: Int = 100)(implicit exc: ExecutionContext): StreamingDBIO[Seq[Entity], Entity] = {
    tableQuery
      .result
      .transactionally
      .withStatementParameters(fetchSize = fetchSize)
  }

  override def update(entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {
    val action =
      for {
        id <- tryExtractId(entity)
        preparedModel <- beforeUpdate(id, entity)
        updatedModel <- update(id, preparedModel)
      } yield updatedModel

    // beforeUpdate and update must run on same tx
    action.transactionally
  }


  protected def update(id: Id, entity: Entity)(implicit exc: ExecutionContext): DBIO[Entity] = {

    val triedUpdate = filterById(id).update(entity).mustAffectOneSingleRow.asTry

    triedUpdate.flatMap {
      case Success(_)                       => DBIO.successful(entity)
      case Failure(NoRowsAffectedException) => DBIO.failed(new RowNotFoundException(entity))
      case Failure(ex)                      => DBIO.failed(ex)
    }

  }

  override def delete(entity: Entity)(implicit exc: ExecutionContext): DBIO[Int] = {
    tryExtractId(entity).flatMap { id =>
      deleteById(id)
    }
  }

  def deleteById(id: Id)(implicit exc: ExecutionContext): DBIO[Int] = {
    filterById(id).delete.mustAffectOneSingleRow
  }


  private def tryExtractId(entity: Entity): DBIO[Id] = {
    idLens.get(entity) match {
      case Some(id) => SuccessAction(id)
      case None     => FailureAction(new RowNotFoundException(entity))
    }
  }

  def filterById(id: Id) = tableQuery.filter($id(_) === id)


}