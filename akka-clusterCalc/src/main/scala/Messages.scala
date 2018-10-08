/**
  * Created by zhoudunxiong on 2018/10/8.
  */
object Messages {
  sealed trait MathOps
  case class Add(x: Int, y: Int) extends MathOps
  case class Sub(x: Int, y: Int) extends MathOps
  case class Mul(x: Int, y: Int) extends MathOps
  case class Div(x: Int, y: Int) extends MathOps
}
