package io.strongtyped.active.slick.dao

import io.strongtyped.active.slick.DBIOExtensions._
import io.strongtyped.active.slick._
import io.strongtyped.active.slick.exceptions.{NoRowsAffectedException, RowNotFoundException}
import slick.ast.BaseTypedType
import slick.dbio.{FailureAction, SuccessAction}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.{existentials, higherKinds, implicitConversions}
import scala.util.{Failure, Success}

abstract class EntityDao[M <: Identifiable, T <: Tables#EntityTable[M]](val jdbcProfile: JdbcProfile)(implicit ev: BaseTypedType[M#Id])
  extends EntityDaoLike[M] with JdbcProfileProvider {

  import jdbcProfile._
  import jdbcProfile.api._

  def tableQuery: TableQueries#EntityTableQuery[M, T]

  def idLens: SimpleLens[M, Option[M#Id]]

  override def count: DBIO[Int] = tableQuery.size.result

  override def findById(id: M#Id): DBIO[M] =
    filterById(id).result.head

  override def findOptionById(id: M#Id): DBIO[Option[M]] =
    filterById(id).result.headOption

  override def save(model: M)(implicit exc: ExecutionContext): DBIO[M] = {
    idLens.get(model) match {
      // if has an Id, try to update it
      case Some(id) => update(model)

      // if has no Id, try to add it
      case None => insert(model).map { id =>
        idLens.set(model, Option(id))
      }
    }
  }

  /**
   * Before insert interceptor method. This method is called just before record insertion.
   * The default implementation returns a successful DBIO wrapping the passed model.
   *
   * Override this method if you need to add extract validation or modify the model before insert.
   *
   * See examples bellow:
   * {{{
   * // simple validation example
   * override def beforeInsert(foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
   *    if (foo.name.trim.isEmpty) {
   *      DBIO.failed(new RuntimeException("Name can't be empty!!!")
   *    } else {
   *      DBIO.successful(model)
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
  def beforeInsert(model: M)(implicit exc: ExecutionContext): DBIO[M] = {
    // default implementation does nothing
    DBIO.successful(model)
  }


  /**
   * Before update interceptor method. This method is called just before record update.
   * The default implementation returns a successful DBIO wrapping the passed model.
   *
   *
   * Override this method if you need to add extract validation or modify the model before update.
   *
   * See examples bellow:
   *
   * {{{
   * // simple validation example
   * override def beforeUpdate(id: Int, foo: Foo)(implicit exc: ExecutionContext): DBIO[Foo] = {
   *    findById(id).flatMap { oldFoo =>
   *      if (oldFoo.name != model.name) {
   *        DBIO.failed(new RuntimeException("Can't modify name!!!")
   *      } else {
   *        DBIO.successful(model)
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
  def beforeUpdate(id: M#Id, model: M)(implicit exc: ExecutionContext): DBIO[M] = {
    // default implementation does nothing
    DBIO.successful(model)
  }

  override def insert(model: M)(implicit exc: ExecutionContext): DBIO[M#Id] = {
    beforeInsert(model).flatMap { preparedModel =>
      tableQuery.returning(tableQuery.map(_.id)) += preparedModel
    }
  }


  override def update(model: M)(implicit exc: ExecutionContext): DBIO[M] = {
    for {
      id <- tryExtractId(model)
      preparedModel <- beforeUpdate(id, model)
      updatedModel <- update(id, preparedModel)
    } yield updatedModel
  }



  protected def update(id: M#Id, model: M)(implicit exc: ExecutionContext): DBIO[M] = {

    val triedUpdate = filterById(id).update(model).mustAffectOneSingleRow.asTry

    triedUpdate.map {
      case Success(_)                       => model
      case Failure(NoRowsAffectedException) => throw new RowNotFoundException(model)
      case Failure(ex)                      => throw ex
    }
  }

  override def delete(model: M)(implicit exc: ExecutionContext): DBIO[Int] = {
    tryExtractId(model).flatMap { id =>
      deleteById(id)
    }
  }

  def deleteById(id: M#Id)(implicit exc: ExecutionContext): DBIO[Int] = {
    filterById(id).delete.mustAffectOneSingleRow
  }


  private def tryExtractId(model: M): DBIO[M#Id] = {
    idLens.get(model) match {
      case Some(id) => SuccessAction(id)
      case None     => FailureAction(new RowNotFoundException(model))
    }
  }

  def filterById(id: M#Id) = tableQuery.filter(_.id === id)

  implicit def queryDeleteActionExtensionMethodsFixed[C[_]](q: Query[_ <: Tables#EntityTable[_], _, C]): DeleteActionExtensionMethods =
    createDeleteActionExtensionMethods(deleteCompiler.run(q.toNode).tree, ())

}