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

import scala.slick.jdbc.JdbcBackend
import scala.util.Try

trait ActiveRecordExtensions { this: TableQueries =>

  trait ActiveRecord[M] {

    type TableQuery = ActiveTableQuery[M, _]

    def table: TableQuery
    def model: M

    def save(implicit session: JdbcBackend#Session): M = table.save(model)
    def trySave(implicit session: JdbcBackend#Session): Try[M] = table.trySave(model)

    def delete(implicit session: JdbcBackend#Session): Boolean = table.delete(model)
    def tryDelete(implicit session: JdbcBackend#Session): Try[Boolean] = table.tryDelete(model)
  }
}
