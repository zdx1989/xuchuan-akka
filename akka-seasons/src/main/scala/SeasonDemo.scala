import FillSeasons.{HowYouFeel, NextMonth}
import akka.actor.ActorSystem

/**
  * Created by zhoudunxiong on 2018/10/7.
  */
object SeasonDemo extends App {
  val system = ActorSystem("seasonSystem")
  val seasonActor = system.actorOf(FillSeasons.props, "fillSeason")
//  seasonActor ! HowYouFeel
//  seasonActor ! HowYouFeel
//  seasonActor ! HowYouFeel
  seasonActor ! HowYouFeel
  seasonActor ! NextMonth
  seasonActor ! NextMonth
  seasonActor ! NextMonth
  seasonActor ! HowYouFeel
  seasonActor ! NextMonth
  seasonActor ! NextMonth
  seasonActor ! NextMonth
  seasonActor ! HowYouFeel
  scala.io.StdIn.readLine()
  system.terminate()
}
