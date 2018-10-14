import CalcActor._
import CalculatorShard.CalcCommands
import akka.actor.ActorSystem
import akka.cluster.sharding.ClusterSharding
import com.typesafe.config.ConfigFactory

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
object CalcShardingDemo extends App {
  CalcShards.create(2551)
  CalcShards.create()
  CalcShards.create()

  Thread.sleep(1000)

  val system = ActorSystem("shardingSystem", ConfigFactory.load("sharding"))
  CalcShards.startupSharding(0, system)

  Thread.sleep(1000)

  val calcRegion = ClusterSharding(system).shardRegion(CalculatorShard.shardName)

  calcRegion ! CalcCommands("1012", Num(13.0))   //shard 1, entity 1012
  calcRegion ! CalcCommands("1012", Add(12.0))
  calcRegion ! CalcCommands("1012", ShowResult)  //shows address too
  calcRegion ! CalcCommands("1012", DisConnect)   //disengage cluster

  calcRegion ! CalcCommands("2012", Num(10.0))   //shard 2, entity 2012
  calcRegion ! CalcCommands("2012", Mul(3.0))
  calcRegion ! CalcCommands("2012", Div(2.0))
  calcRegion ! CalcCommands("2012", Div(0.0))   //divide by zero


  Thread.sleep(15000)
  calcRegion ! CalcCommands("1012", ShowResult)   //check if restore result on another node
  calcRegion ! CalcCommands("2012", ShowResult)
}
