package stubby

import scala.quoted.*

extension (self: String)
  def blue    = Console.BLUE + self + Console.RESET
  def red     = Console.RED + self + Console.RESET
  def cyan    = Console.CYAN + self + Console.RESET
  def green   = Console.GREEN + self + Console.RESET
  def yellow  = Console.YELLOW + self + Console.RESET
  def magenta = Console.MAGENTA + self + Console.RESET

  def dim        = s"\u001b[2m$self\u001b[0m"
  def bold       = Console.BOLD + self + Console.RESET
  def underlined = Console.UNDERLINED + self + Console.RESET

object Report:
  def debug(using Quotes)(expr: Expr[?]): Nothing =
    import quotes.reflect.*
    debug(expr.asTerm)

  def debug(using
      Quotes
  )(term: quotes.reflect.Term, showType: Boolean = false, position: Option[quotes.reflect.Position] = None): Nothing =
    import quotes.reflect.*

    val sb = StringBuilder()

    sb.append("SHOW TERM\n".blue.underlined)
    sb.append(term.show)
    sb.append("\n\n")
    sb.append("TERM\n".blue.underlined)
    sb.append(pprint(term).toString)
    sb.append("\n\n")
    if showType then
      sb.append("SHOW TYPE\n".blue.underlined)
      sb.append(term.tpe.show)
      sb.append("\n\n")
      sb.append("TYPE\n".blue.underlined)
      sb.append(pprint(term.tpe).toString)

    report.errorAndAbort(sb.toString, pos = position.getOrElse(Position.ofMacroExpansion))
