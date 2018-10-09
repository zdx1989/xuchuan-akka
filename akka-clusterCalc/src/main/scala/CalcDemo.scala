import Messages._

/**
  * Created by zhoudunxiong on 2018/10/9.
  */
object CalcDemo extends App {
  CalcRouter.create()

  CalcSupervisor.create("adder")
  CalcSupervisor.create("subor")
  CalcSupervisor.create("muler")
  CalcSupervisor.create("diver")

  Thread.sleep(2000)

  val router = CalcRouter.getRoute()

  router ! Add(1, 2)
  router ! Sub(3, 2)
  router ! Mul(4, 2)
  router ! Div(1, 0)
  router ! Div(6, 2)


}
