package com.hyperplan.test.domain.repositories

import com.hyperplan.domain.repositories._
import com.hyperplan.test.{
  AlgorithmGenerator,
  ProjectGenerator,
  TaskChecker,
  TestDatabase
}
import org.scalatest._

class ProjectsRepositorySpec
    extends FlatSpec
    with Matchers
    with TaskChecker
    with TestDatabase {

  val domainRepository = new DomainRepository()(xa)
  val projectRepository = new ProjectsRepository(domainRepository)(xa)

  it should "insert, read and delete projects correctly" in {
    withInMemoryDatabase { _ =>
      val project = ProjectGenerator.withLocalBackend()
      val insertIO = projectRepository.insertProjectQuery(project)
      val readIO = projectRepository.readProjectQuery(project.id)
      val readAllIO = projectRepository.readAllProjectsQuery
      val deleteIO = projectRepository.deleteProjectQuery(project.id)
      check(insertIO)
      check(readIO)
      check(readAllIO)
      check(deleteIO)
      assert(true)
    }
  }

}
