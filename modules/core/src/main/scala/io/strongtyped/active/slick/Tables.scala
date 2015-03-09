/*
 * Copyright 2014 Renato Guerra Cavalcanti (@renatocaval)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.strongtyped.active.slick

import io.strongtyped.active.slick.models.Identifiable

/**
 * Defines Slick table extensions.
 * To be mixed-in into a cake.
 */
trait Tables {
  this: Profile =>

  import driver.api._

  trait IdColumn[I] {

    def id: Rep[I]
  }

  trait VersionColumn {

    def version: Rep[Long]
  }

  /** Table extension to be used with a Model that has an Id. */
  abstract class IdTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)(implicit val colType: BaseColumnType[I])
    extends Table[M](tag, schemaName, tableName) with IdColumn[I] {

    /** Constructor without schemaName */
    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)
  }

  /** Table extension to be used with a Model that has an Id and version (optimistic locking). */
  abstract class IdVersionTable[M, I](tag: Tag, schemaName: Option[String], tableName: String)(override implicit val colType: BaseColumnType[I])
    extends IdTable[M, I](tag, schemaName, tableName)(colType) with VersionColumn {

    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)
  }

  /**
   * Type alias for [[IdTable]]s mapping [[Identifiable]]s
   * Id type is mapped via type projection of Identifiable#Id
   */
  type EntityTable[M <: Identifiable] = IdTable[M, M#Id]

  /**
   * Type alias for [[IdTable]]s mapping [[Identifiable]]s with version.
   * Id type is mapped via type projection of Identifiable#Id
   */
  type VersionableEntityTable[M <: Identifiable] = IdVersionTable[M, M#Id]

}
