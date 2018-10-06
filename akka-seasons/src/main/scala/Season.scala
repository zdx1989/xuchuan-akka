
/**
  * Created by zhoudunxiong on 2018/10/5.
  */
sealed trait Season
case object Spring extends Season
case object Summer extends Season
case object Fall extends Season
case object Winter extends Season

case class SeasonInfo(talk: Int, month: Int)
case object BeginSeason extends SeasonInfo(0,1)

