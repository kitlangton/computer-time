import zio.test.*
import zio.*
import stubby.*

object AppSpec extends ZIOSpecDefault:

  val spec =
    suiteAll("BatchProcessor") {

      // val llmStub = magicStub[LLM]

      test("successfully batch processes target entities") {
        for
          // _ <- stub[LLM](_.launchMissiles)("hello")

          // SOURCE SYNTAX
          ///           (_: LLM) => ((plant: String) => _.lauchMissles(plant))
          _ <- stub[LLM](_.launchMissiles)(ZIO.unit)

          // TARGET SYNTAX
          // _        <- Stubbed.insertImpl[LLM]("launchMissiles", ZIO.unit)
          response <- BatchProcessor.process("Venus", "Mars")
        yield assertTrue(
          response == List("Venus destroyed! :)", "Mars destroyed! :)")
        )
      }

      test("fails if a target in invalid") {
        for
          _        <- stub[LLM](_.launchMissiles)(ZIO.fail(LLMError.InvalidTarget("Earth")))
          response <- BatchProcessor.process("Earth")
        yield assertTrue(
          response == List("Invalid target: Earth")
        )
      }

      test("fails with self destruct") {
        for
          _        <- stub[LLM](_.launchMissiles)(ZIO.fail(LLMError.SelfDestructed))
          response <- BatchProcessor.process("Earth")
        yield assertTrue(
          response == List("Ya blew it.")
        )
      }

      test("fails with unauthorized") {
        for
          _        <- stub[LLM](_.launchMissiles)(ZIO.fail(LLMError.Unauthorized))
          response <- BatchProcessor.process("Earth")
        yield assertTrue(
          response == List("As a Large Lunar Missile array, I'm unable to authenticate you.")
        )
      }

    }
      .provide(
        BatchProcessor.layer,
        LLMStub.layer
        // ZLayer.succeed(123)
      )

// class $TraitStub(impls: Map[String, Any]) extends $Trait:
//    <for each method>
//    def $method(args: $ArgType): IO[$Error, $ReturnType] =
//      impls.get.flatMap { impls =>
//        impls.getOrElse($method.name, throw new IllegalStateException("${method.name} not set"))
//        .asInstanceOf[$method.returnType]
//      }

case class LLMStub(impls: Ref[Map[String, Any]]) extends LLM with Stubbed[LLM]:
  def insertImpl(methodName: String, impl: Any): UIO[Unit] =
    impls.update(_.updated(methodName, impl))

  override def launchMissiles(planet: String): IO[LLMError, Unit] =
    impls.get.flatMap { impls =>
      impls
        .getOrElse("launchMissiles", throw new IllegalStateException("launchMissiles not set"))
        .asInstanceOf[IO[LLMError, Unit]]
    }

  override def destroyCity(city: String): IO[LLMError, Unit] =
    impls.get.flatMap { impls =>
      impls
        .getOrElse("destroyCity", throw new IllegalStateException("destroyCity not set"))
        .asInstanceOf[IO[LLMError, Unit]]
    }

  override def obliterateContinent(continent: String): IO[LLMError, Unit] =
    impls.get.flatMap { impls =>
      impls
        .getOrElse("obliterateContinent", throw new IllegalStateException("obliterateContinent not set"))
        .asInstanceOf[IO[LLMError, Unit]]
    }

  override def annihilateCountry(country: String): IO[LLMError, Unit] =
    impls.get.flatMap { impls =>
      impls
        .getOrElse("annihilateCountry", throw new IllegalStateException("annihilateCountry not set"))
        .asInstanceOf[IO[LLMError, Unit]]
    }

  override def devastateRegion(region: String): IO[LLMError, Unit] =
    impls.get.flatMap { impls =>
      impls
        .getOrElse("devastateRegion", throw new IllegalStateException("devastateRegion not set"))
        .asInstanceOf[IO[LLMError, Unit]]
    }

object LLMStub:
  val layer = ZLayer {
    Ref.make(Map.empty[String, Any]).map(LLMStub(_))
  }

  def insertImpl(methodName: String, impl: Any): ZIO[LLMStub, Nothing, Unit] =
    ZIO.serviceWithZIO[LLMStub](_.insertImpl(methodName, impl))
