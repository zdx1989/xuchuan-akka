import FillSeasons.{HowYouFeel, NextMonth}
import akka.actor.{ActorLogging, FSM, Props}

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class FillSeasons extends FSM[Season, SeasonInfo] with ActorLogging {
  startWith(Spring, SeasonInfo(0, 1))

  when(Spring) {
    case Event(HowYouFeel, SeasonInfo(talk, month)) =>
      val numTalks = talk + 1
      log.info(s"It's ${stateName.toString}, feel so goooood, you ask me $numTalks times")
      stay().using(SeasonInfo(numTalks, month))
  }

  when(Summer) {
    case Event(HowYouFeel, seasonInfo) =>
      val numTalks = seasonInfo.talk + 1
      log.info(s"It's ${stateName.toString}, it's so hot, you ask me $numTalks times")
      stay().using(seasonInfo.copy(talk = numTalks))
  }

  when(Fall) {
    case Event(HowYouFeel, _) =>
      val numTalks = stateData.talk + 1
      log.info(s"It's ${stateName.toString}, it's not so bad, you ask me $numTalks times")
      stay().using(stateData.copy(talk = numTalks))
  }

  when(Winter) {
    case Event(HowYouFeel, s @ SeasonInfo(talk, _)) =>
      val numTalks = stateData.talk + 1
      log.info(s"It's ${stateName.toString}, it's freezing cold, you ask me $numTalks times")
      stay().using(s.copy(talk = numTalks))
  }

  whenUnhandled {
    case Event(NextMonth, s @ SeasonInfo(talk, month)) =>
      if (month < 3) {
        log.info(s"It's month ${month + 1} of ${stateName.toString}")
        stay().using(s.copy(month = month + 1))
      } else {
        goto(nextMonth(stateName)).using(SeasonInfo(0, 1))
      }

  }

  def nextMonth(season: Season): Season = season match {
    case Spring => Summer
    case Summer => Fall
    case Fall => Winter
    case Winter => Spring
  }

  log.info(s"It's month 1 of ${stateName.toString}")
  initialize()
}

object FillSeasons {
  sealed trait Message
  case object HowYouFeel extends Message
  case object NextMonth extends Message

  def props: Props = Props[FillSeasons]
}
