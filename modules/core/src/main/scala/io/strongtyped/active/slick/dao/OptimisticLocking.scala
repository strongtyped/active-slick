package io.strongtyped.active.slick.dao

import io.strongtyped.active.slick.DBIOExtensions._
import io.strongtyped.active.slick.exceptions.{StaleObjectStateException, NoRowsAffectedException}
import io.strongtyped.active.slick.{SimpleLens, Identifiable}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait OptimisticLocking[M <: Identifiable] {
  dao: EntityDao[M] =>

  import dao.jdbcProfile.api._

  def $version(table:EntityTable): Rep[Long]

  def versionLens: SimpleLens[M, Long]

  override protected def update(id: M#Id, versionable: M)(implicit exc: ExecutionContext): DBIO[M] = {

    // extract current version
    val currentVersion = versionLens.get(versionable)

    // build a query selecting entity with current version
    val queryByIdAndVersion = dao.filterById(id).filter($version(_) === currentVersion)

    // model with incremented version
    val modelWithNewVersion = versionLens.set(versionable, currentVersion + 1)

    val tryUpdate = queryByIdAndVersion.update(modelWithNewVersion).mustAffectOneSingleRow.asTry

    // in case of failure, we want a more meaningful exception ie: StaleObjectStateException
    tryUpdate.flatMap {
      case Success(_) => DBIO.successful(modelWithNewVersion)
      case Failure(NoRowsAffectedException) =>
        findById(id).flatMap { currentOnDb =>
          DBIO.failed(new StaleObjectStateException(versionable, currentOnDb))
        }
      case Failure(e) => DBIO.failed(e)
    }
  }

  override def save(versionable: M)(implicit exc: ExecutionContext): DBIO[M] = {
    dao.idLens.get(versionable) match {
      // if has an Id, try to update it
      case Some(id) => update(id, versionable)

      // if has no Id, try to add it
      case None =>
        // initialize versioning
        val modelWithVersion = versionLens.set(versionable, 1)
        dao.insert(modelWithVersion).map { id => idLens.set(modelWithVersion, Option(id)) }
    }
  }
}