import zio.*

// Large Lunar Missile array
trait LLM:
  def launchMissiles(planet: String): IO[LLMError, Unit]

  def destroyCity(city: String): IO[LLMError, Unit]
  def obliterateContinent(continent: String): IO[LLMError, Unit]
  def annihilateCountry(country: String): IO[LLMError, Unit]
  def devastateRegion(region: String): IO[LLMError, Unit]

enum LLMError extends Exception:
  case SelfDestructed
  case InvalidTarget(target: String)
  case Unauthorized

case class GPT4Config(apiKey: String)

trait Kafka
trait Database
trait DogService

// Galvanized Planetary Thrasher 4
// class GPT4(
//     config: GPT4Config,
//     kafka: Kafka,
//     database: Database,
//     dogService: DogService
// ) extends LLM:

//   private def authenticate: IO[LLMError, Unit] =
//     if config.apiKey == "hunter2" then ZIO.unit
//     else ZIO.fail(LLMError.Unauthorized)

//   def launchMissiles(planet: String): IO[LLMError, Unit] =
//     for
//       _ <- authenticate
//       _ <- planet match
//              case "Earth" => ZIO.fail(LLMError.InvalidTarget("Earth"))
//              case "GPT4"  => ZIO.fail(LLMError.SelfDestructed)
//              case _       => ZIO.unit
//     yield ()

// object GPT4:
//   val layer = ZLayer.derive[GPT4]

class BatchProcessor(llm: LLM):

  def process(targets: String*): Task[List[String]] =
    for
      _ <- Console.printLine(s"Launching missiles on ${targets.mkString(", ")}")
      response <- ZIO.foreach(targets.toList) { target =>
                    llm
                      .launchMissiles(target)
                      .as(s"$target destroyed! :)")
                      .catchAll {
                        case LLMError.SelfDestructed =>
                          ZIO.succeed("Ya blew it.")
                        case LLMError.InvalidTarget(target) =>
                          ZIO.succeed(s"Invalid target: $target")
                        case LLMError.Unauthorized =>
                          ZIO.succeed("As a Large Lunar Missile array, I'm unable to authenticate you.")
                      }
                  }
    yield response

object BatchProcessor:
  val layer                     = ZLayer.derive[BatchProcessor]
  def process(targets: String*) = ZIO.serviceWithZIO[BatchProcessor](_.process(targets*))
