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
package io.strongtyped.active.slick.models

/**
 * Base trait to define a model having an ID (i.e.: Entity).
 * The ID is defined as a type alias as it needs to
 * be accessed by ActiveSlick via type projection when mapping to databse tables.
 */
trait Identifiable[E <: Identifiable[E]] {

  /** The type of this Entity ID */
  type Id

  /**
   * The Entity ID wrapped in an Option.
   * Expected to be None when Entity not yet persisted, otherwise Some[Id]
   */
  def id: Option[E#Id]

  /** Provide the means to assign an ID to the entity
    * @return A copy of this Entity with an ID.
    */
  def withId(id: E#Id): E
}