package test

import akka.actor.ActorSystem
import akka.cluster.sharding.ClusterSharding
import com.typesafe.config.ConfigFactory
import test.AddActor._

/**
  * Created by zhoudunxiong on 2018/10/17.
  */
object AddActorDemo extends App {

  AddShard.create(2551)
  AddShard.create(0)
  AddShard.create(0)

  Thread.sleep(1000)
  val system = ActorSystem("addSystem", ConfigFactory.load("sharding"))
  AddShard.startShard(0, system)

  Thread.sleep(1000)

  val addRegion = ClusterSharding(system).shardRegion(AddActor.shardName)

  addRegion ! Command("1012", Add(1))
  addRegion ! Command("1012", GetRequest)

  addRegion ! Command("2012", Add(5))
  addRegion ! Command("2012", GetRequest)
  addRegion ! Command("2012", DisConnect)

  Thread.sleep(15000)
  addRegion ! Command("2012", GetRequest)


}
