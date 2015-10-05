package io.strongtyped.active.slick.docexamples

import io.strongtyped.active.slick.docexamples.ActiveSlickWithCodegen.ComputersRepo
import io.strongtyped.active.slick.docexamples.ActiveSlickWithCodegen.ComputersRepo.EntryExtensions
import io.strongtyped.active.slick.test.H2Suite
import org.scalatest._
import ComputersRepo.EntryExtensions
import io.strongtyped.active.slick.docexamples.codegen.Tables.ComputersRow
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class ActiveSlickWithCodegenTest extends FlatSpec with H2Suite with OptionValues {

  "ActiveSlickWithCodegen" should "provide crud and active record semantics for generated tables" in {

    // At the beginning the Computers DB should be completely empty
    query(ComputersRepo.findOptionById(1)) shouldBe None
    query(ComputersRepo.count) shouldBe 0

    // Add a Computer without default ID 0. Make sure it gets an ID and all data are stored
    val newComputer = ComputersRow(id = 0L, name = "Macbook Pro 15")
    val savedComputer = commit(newComputer.save())
    savedComputer.id should be(1L)
    savedComputer.name should be("Macbook Pro 15")
    query(ComputersRepo.count) shouldBe 1

    // Query the Computer we saved, update it, and delete it.
    query(ComputersRepo.findById(1L)) shouldBe savedComputer

    val updatedComputer = commit(savedComputer.copy(name = "MBP 15").save())
    updatedComputer.id should be(savedComputer.id)
    updatedComputer.name should be("MBP 15")

    commit(updatedComputer.delete()) shouldBe 0
    query(ComputersRepo.findOptionById(updatedComputer.id)) shouldBe None

  }

  override def setupDb: jdbcProfile.backend.DatabaseDef = {
    import jdbcProfile.api._
    val db = Database.forURL(
      url = s"jdbc:h2:mem:${this.getClass.getSimpleName};AUTOCOMMIT=TRUE;INIT=runscript from 'modules/samples/src/main/resources/codegen_schema.sql'",
      driver = "org.h2.Driver"
    )
    db.createSession().conn.setAutoCommit(true)
    db
  }


}

