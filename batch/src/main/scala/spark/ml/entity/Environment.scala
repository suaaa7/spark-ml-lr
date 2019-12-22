package spark.ml.entity

import com.twitter.util._

sealed abstract class Environment(s: String) {
  override val toString: String = s
}

object Environment {
  case object Dev  extends Environment("dev")
  case object Prod extends Environment("prod")

  def fromString(s: String): Try[Environment] =
    s match {
      case Dev.toString  => Return(Dev)
      case Prod.toString => Return(Prod)
      case _             => Throw(new Exception(s"Invalid environment: $s"))
    }
}
