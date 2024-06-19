package stubby

import scala.quoted.* // <-

// 1. macro definition
inline def debug(value: String): String = ${ debugImpl('value) }
// inline keyword
// splice body
// all quote arguments

// 2. macro implementation
// - essentially mirrors the structure of the FACE/definition
//    everything is wrapped in Expr
// - using Quotes

def debugImpl(value: Expr[String])(using Quotes): Expr[String] =
  import quotes.reflect.* // <-- import this thing...

  value.asTerm.underlyingArgument match
    case Ident(name) =>
      val nameExpr: Expr[String] = Expr("HELLO")
      report.errorAndAbort(s"HUH: ${nameExpr.asTerm}")
      '{ ${ nameExpr } + " = " + $value }
    case _ => value
