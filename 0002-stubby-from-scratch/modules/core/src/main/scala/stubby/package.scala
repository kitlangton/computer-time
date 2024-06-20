package stubby

import zio.*
import scala.quoted.*

trait Stubbed[Service]:
  def insertImpl(methodName: String, impl: Any): UIO[Unit]

object Stubbed:
  def insertImpl[Service: Tag](methodName: String, impl: Any): ZIO[Stubbed[Service], Nothing, Unit] =
    ZIO.serviceWithZIO[Stubbed[Service]](_.insertImpl(methodName, impl))

// Source Syntax => Target Syntax
// Source Syntax ==macroExpand=> Target Syntax
inline def stub[Service](inline selector: Service => Any)(result: Any): ZIO[Stubbed[Service], Nothing, Unit] =
  ${ stubImpl[Service]('selector, 'result) }

def stubImpl[Service: Type](
    selector: Expr[Service => Any],
    result: Expr[Any]
)(using Quotes): Expr[ZIO[Stubbed[Service], Nothing, Unit]] =
  import quotes.reflect.*
  // Report.debug(selector)

  selector.asTerm match
    case Inlined(
          _,
          _,
          Lambda(
            _,
            Lambda(
              _, //
              Apply(term @ Select(_, methodName), _)
            )
          )
        ) =>
      // report.errorAndAbort(
      //   s"result: ${result.asTerm.tpe.widenTermRefByName.show}"
      // )
      // "hello" -> Literal(Constant("Hello"))
      '{ Stubbed.insertImpl[Service](${ Expr(methodName) }, $result) }
