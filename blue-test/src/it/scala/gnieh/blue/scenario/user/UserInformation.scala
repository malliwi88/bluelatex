/*
 * This file is part of the \BlueLaTeX project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gnieh.blue
package scenario
package user

import org.scalatest._
import OptionValues._

import http.ErrorResponse
import couch.User

import gnieh.diffson.JsonDiff

/** Scenarios to test user data management:
 *   - getting user data for authenticated user
 *   - saving user own data for authenticated user
 *   - saving some other user data for authenticated user
 *   - getting user data for unauthenticated user
 *   - saving user data for unauthenticated user
 *
 *  @author Lucas Satabin
 */
class UserInformationSpec extends BlueScenario with SomeUsers {

  val predefinedPeople =
    List(gerard, prince)

  feature("Any authenticated user must be able to access user information") {

    scenario("successfully getting own user data") {

      Given("an authenticated person")
      val (loggedin, _) = post[Boolean](List("session"), Map("username" -> gerard.username, "password" -> gerard.password))

      loggedin should be(true)

      When("he asks for his user data")
      val (data, _) = get[User](List("users", gerard.username, "info"))

      Then("he should receive his personal data")
      data.name should be(gerard.username)
      data.first_name should be(gerard.first_name)
      data.last_name should be(gerard.last_name)
      data.email should be(gerard.email_address)
      data.affiliation should be(gerard.affiliation)

      val (loggedout, _) = delete[Boolean](List("session"))

      loggedout should be(true)

    }

    scenario("successfully getting other user data") {

      Given("an authenticated person")
      val (loggedin, _) = post[Boolean](List("session"), Map("username" -> gerard.username, "password" -> gerard.password))

      loggedin should be(true)

      When("he asks for some other user data")
      val (data, _) = get[User](List("users", prince.username, "info"))

      Then("he should receive the other user data")
      data.name should be(prince.username)
      data.first_name should be(prince.first_name)
      data.last_name should be(prince.last_name)
      data.email should be(prince.email_address)
      data.affiliation should be(prince.affiliation)

      val (loggedout, _) = delete[Boolean](List("session"))

      loggedout should be(true)

    }

    scenario("getting unknown user data") {

      Given("an authenticated person")
      val (loggedin, _) = post[Boolean](List("session"), Map("username" -> gerard.username, "password" -> gerard.password))

      loggedin should be(true)

      When("he asks for some unknown user data")
      val exc = evaluating {
        get[User](List("users", "unknown_user", "info"))
      } should produce[BlueErrorException]
      exc.status should be(404)

      val (loggedout, _) = delete[Boolean](List("session"))

      loggedout should be(true)

    }

  }

  feature("Any authenticated user must be able to save his own user information") {

    scenario("successfully saving personal information") {

      Given("an authenticated person")
      val (loggedin, _) = post[Boolean](List("session"), Map("username" -> gerard.username, "password" -> gerard.password))

      loggedin should be(true)

      When("he asks for his user data")
      val (savedData, headers) = get[User](List("users", gerard.username, "info"))

      headers.get("ETag") should be('defined)
      headers.get("ETag").value.size should be(1)

      And("he modifies and saves his own user data")
      val etag = headers("ETag").head
      val p = JsonDiff.diff(savedData, savedData.copy(affiliation = None))
      val (saved, _) = patch[Boolean](List("users", gerard.username, "info"), p, etag)

      saved should be(true)

      Then("he can retrieve the new settings")
      val (data, _) = get[User](List("users", gerard.username, "info"))
      data.name should be(gerard.username)
      data.first_name should be(gerard.first_name)
      data.last_name should be(gerard.last_name)
      data.email should be(gerard.email_address)
      data.affiliation should be(None)

      val (loggedout, _) = delete[Boolean](List("session"))

      loggedout should be(true)

    }

  }

}

