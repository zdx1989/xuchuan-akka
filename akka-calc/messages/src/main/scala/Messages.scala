/**
  * Created by zhoudunxiong on 2018/10/7.
  */
object Messages {
  sealed trait MathOps
  case class Num(n: Double) extends MathOps
  case class Add(n: Double) extends MathOps
  case class Sub(n: Double) extends MathOps
  case class Mul(n: Double) extends MathOps
  case class Div(n: Double) extends MathOps

  sealed trait CalcOps
  case object Clear extends CalcOps
  case object GetAnswer extends CalcOps
}
