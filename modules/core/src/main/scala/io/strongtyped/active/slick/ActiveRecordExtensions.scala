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

import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

trait ActiveRecordExtensions { this: TableQueries with Profile =>

  trait ActiveRecord[M] {

    type TableQuery = ActiveTableQuery[M, _]

    def tableQuery: TableQuery
    def model: M

    def save()(implicit exc: ExecutionContext): DBIO[M] = tableQuery.save(model)

    def update()(implicit exc: ExecutionContext): DBIO[M] = tableQuery.update(model)

    def delete()(implicit exc: ExecutionContext): DBIO[Unit] = tableQuery.delete(model)
  }
}
