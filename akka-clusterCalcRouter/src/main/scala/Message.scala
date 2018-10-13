import akka.routing.ConsistentHashingRouter.ConsistentHashable

/**
  * Created by zhoudunxiong on 2018/10/13.
  */
object Message {
  class MathOps(role: String) extends ConsistentHashable with Serializable {
    override def consistentHashKey: Any = role
  }

  case class Add(x: Int, y: Int) extends MathOps("adder")
  case class Sub(x: Int, y: Int) extends MathOps("subor")
  case class Mul(x: Int, y: Int) extends MathOps("muler")
  case class Div(x: Int, y: Int) extends MathOps("diver")

}
