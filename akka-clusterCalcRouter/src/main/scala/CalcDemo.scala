import Message._

/**
  * Created by zhoudunxiong on 2018/10/13.
  */
object CalcDemo extends App {
  CalcSupervisor.create(2551)
  CalcSupervisor.create(0)
  CalcSupervisor.create(0)
  CalcSupervisor.create(0)


  Thread.sleep(2000)

  CalcRouter.create()

  Thread.sleep(2000)

  val router = CalcRouter.getRouter()

  router ! Add(1, 4)
  router ! Sub(3, 1)
  router ! Mul(6, 3)
  router ! Div(2, 0)
  router ! Div(4, 1)

}
