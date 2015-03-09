package io.strongtyped.active.slick

import io.strongtyped.active.slick.exceptions.{StaleObjectStateException, NoRowsAffectedException}
import io.strongtyped.active.slick.models.Identifiable
import shapeless.Lens
import slick.ast.BaseTypedType

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait EntityTableQueries {
  this: Profile with Tables with TableQueries =>

  import driver.api._


  class EntityTableQuery[M <: Identifiable, T <: EntityTable[M]](cons: Tag => T, idLens: Lens[M, Option[M#Id]])
                                                                (implicit ev: BaseTypedType[M#Id])
    extends TableWithIdQuery[M, M#Id, T](cons, idLens)

  object EntityTableQuery {

    def apply[M <: Identifiable, T <: EntityTable[M]]
    (cons: Tag => T, idLens: Lens[M, Option[M#Id]])(implicit ev: BaseTypedType[M#Id]) = new EntityTableQuery[M, T](cons, idLens)
  }

  class VersionableEntityTableQuery[M <: Identifiable, T <: VersionableEntityTable[M]](cons: Tag => T, idLens: Lens[M, Option[M#Id]], versionLens: Lens[M, Long])
                                                                                      (implicit ev: BaseTypedType[M#Id])
    extends EntityTableQuery[M, T](cons, idLens) {

    override protected def update(id: M#Id, versionable: M)(implicit exc: ExecutionContext): DBIO[M] = {

      // extract current version
      val currentVersion = versionLens.get(versionable)

      // build a query selecting entity with current version
      val queryByIdAndVersion = filterById(id).map { query =>
        query.filter(_.version === currentVersion)
      }

      // model with incremented version
      val modelWithNewVersion = versionLens.set(versionable)(currentVersion + 1)

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
      idLens.get(versionable) match {
        // if has an Id, try to update it
        case Some(id) => update(id, versionable)

        // if has no Id, try to add it
        case None =>
          // initialize versioning
          val modelWithVersion = versionLens.set(versionable)(1)
          add(modelWithVersion).map { id => idLens.set(modelWithVersion)(Option(id)) }
      }
    }

  }

  object VersionableEntityTableQuery {

    def apply[M <: Identifiable, T <: VersionableEntityTable[M]](cons: Tag => T, idLens: Lens[M, Option[M#Id]], versionLens: Lens[M, Long])
                                                                (implicit ev1: BaseColumnType[M#Id]) = {

      new VersionableEntityTableQuery[M, T](cons, idLens, versionLens)

    }
  }

}
