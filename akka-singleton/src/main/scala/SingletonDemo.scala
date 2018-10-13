/**
  * Created by zhoudunxiong on 2018/10/13.
  */
object SingletonDemo extends App {
  SingletonActor.create(2551)

  SingletonActor.create()
  SingletonActor.create()
  SingletonActor.create()
  SingletonActor.create()

  SingletonUser.create()
}
