package io.strongtyped.active.slick

import io.strongtyped.active.slick.exceptions.{NoRowsAffectedException, StaleObjectStateException}
import io.strongtyped.active.slick.models.Identifiable
import shapeless.Lens
import scala.util.{Failure, Try}

trait EntityTableQueries {
  this: Profile with Tables with TableQueries =>

  import jdbcDriver.simple._



  class EntityTableQuery[M <: Identifiable, T <: EntityTable[M]]
                        (cons: Tag => T, idLens:Lens[M, Option[M#Id]])
                        (implicit bct: BaseColumnType[M#Id]) extends TableWithIdQuery[M, M#Id, T](cons, idLens)


  object EntityTableQuery {
    def apply[M <: Identifiable, T <: EntityTable[M]]
             (cons: Tag => T, idLens:Lens[M, Option[M#Id]])
             (implicit ev1: BaseColumnType[M#Id]) = new EntityTableQuery[M, T](cons, idLens)
  }



  class VersionableEntityTableQuery[M <: Identifiable, T <: VersionableEntityTable[M]]
                                   (cons: Tag => T, idLens:Lens[M, Option[M#Id]], versionLens:Lens[M, Long])
                                   (implicit bct: BaseColumnType[M#Id]) extends EntityTableQuery[M, T](cons, idLens) {

    override protected def tryUpdate(id: M#Id, versionable: M)(implicit sess: Session): Try[M] = {

      // extract current version
      val currentVersion = versionLens.get(versionable)

      // build a query selecting entity with current version
      val queryById = filter(_.id === id)
      val queryByIdAndVersion = queryById.filter(_.version === currentVersion)

      // model with incremented version
      val modelWithNewVersion = versionLens.set(versionable)(currentVersion + 1)

      mustAffectOneSingleRow {
        queryByIdAndVersion.update(modelWithNewVersion)

      }.recoverWith {
        // no updates?
        case NoRowsAffectedException =>
          // if row exists we have a stale object
          // all other failures must be propagated
          tryFindById(id).flatMap { currentOnDb =>
            Failure(StaleObjectStateException(versionable, currentOnDb))
          }

      }.map { _ =>
        modelWithNewVersion // return the versionable entity with an updated version
      }
    }

    override def trySave(versionable: M)(implicit sess: Session): Try[M] = {
      rollbackOnFailure {
        idLens.get(versionable) match {
          // if has an Id, try to update it
          case Some(id) => tryUpdate(id, versionable)

          // if has no Id, try to add it
          case None =>
            // initialize versioning
            val modelWithVersion = versionLens.set(versionable)(1)
            tryAdd(modelWithVersion).map { id => idLens.set(modelWithVersion)(Option(id)) }
        }
      }
    }

  }


  object VersionableEntityTableQuery {

    def apply[M <: Identifiable, T <: VersionableEntityTable[M]](cons: Tag => T, idLens:Lens[M, Option[M#Id]], versionLens:Lens[M, Long])
                                                                (implicit ev1: BaseColumnType[M#Id]) = {

      new VersionableEntityTableQuery[M, T](cons, idLens, versionLens)

    }
  }
}
