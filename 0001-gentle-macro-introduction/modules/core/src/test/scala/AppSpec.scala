import zio.test.*
import zio.*

object AppSpec extends ZIOSpecDefault:

  val spec =
    suiteAll("BatchProcessor") {
      test("successfully batch processes target entities") {
        for response <- BatchProcessor.process("Venus", "Mars")
        yield assertTrue(
          response == List("Venus destroyed! :)", "Mars destroyed! :)")
        )
      }
    }.provide(
      BatchProcessor.layer,
      GPT4.layer,
      ZLayer.succeed(GPT4Config("hunter2"))
    )
