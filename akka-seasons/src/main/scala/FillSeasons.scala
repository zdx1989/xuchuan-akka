import akka.actor.{ActorLogging, FSM}

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class FillSeasons extends FSM[Season, SeasonInfo] with ActorLogging {

}

object FillSeasons {
  sealed trait Message
  case object HowYouFeel extends Message
  case object NextMonth extends Message
}
