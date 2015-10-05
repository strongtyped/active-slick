package io.strongtyped.active.slick.docexamples

import org.scalatest.concurrent.ScalaFutures
import org.scalatest._
import org.scalatest.time.{Seconds, Millis, Span}
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import ActiveSlickWithCodegen.ComputersRepo
import ComputersRepo.EntryExtensions
import io.strongtyped.active.slick.docexamples.codegen.Tables.ComputersRow
import org.scalatest.time.SpanSugar._

import scala.language.postfixOps

class ActiveSlickWithCodegenTest extends FlatSpec with Matchers with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(timeout = scaled(Span(5, Seconds)))

  "ActiveSlickWithCodegen" should "provide crud and active record semantics for generated tables" in {


    val db = createDb

    try {
      // At the beginning the Computers DB should be completely empty
      db.run(ComputersRepo.findOptionById(1)).futureValue should be(None)
      db.run(ComputersRepo.count).futureValue should be(0)

      // Add a Computer without default ID 0. Make sure it gets an ID and all data are stored
      val newComputer = ComputersRow(id = 0L, name = "Macbook Pro 15")
      val savedComputer = db.run(newComputer.save()).futureValue
      savedComputer.id should be(1L)
      savedComputer.name should be("Macbook Pro 15")
      db.run(ComputersRepo.count).futureValue should be(1)

      // Query the Computer we saved, update it, and delete it.
      db.run(ComputersRepo.findById(1L)).futureValue should be(savedComputer)

      val updatedComputer = db.run(savedComputer.copy(name = "MBP 15").update()).futureValue
      updatedComputer.id should be(savedComputer.id)
      updatedComputer.name should be("MBP 15")

      db.run(updatedComputer.delete()).futureValue should be(0)
      db.run(ComputersRepo.findOptionById(updatedComputer.id)).futureValue should be(None)

    } finally {
      db.close()
    }
  }

  def createDb = {
    val db = Database.forURL(
      url = s"jdbc:h2:mem:${this.getClass.getSimpleName};AUTOCOMMIT=TRUE;INIT=runscript from 'modules/samples/src/main/resources/codegen_schema.sql'",
      driver = "org.h2.Driver"
    )

    db.createSession().conn.setAutoCommit(true)

    db
  }
}

