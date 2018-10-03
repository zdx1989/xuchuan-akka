import akka.actor.{ActorSystem, PoisonPill}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config
import WalletActor._

/**
  * Created by zhoudunxiong on 2018/10/3.
  */
object ApplicationMain extends App {
  val system = ActorSystem("walletSystem")
  val wallet = system.actorOf(
    WalletActor.props.withDispatcher("prio-dispatcher"),
    "wallet")

  wallet ! UnZip
  wallet ! PutIn(10)
  wallet ! PutIn(20)
  wallet ! DrawOut(10)
  wallet ! ZipUp
  wallet ! PutIn(100)
  wallet ! CheckBalance
  scala.io.StdIn.readLine()
  system.shutdown()

}

class PriorityMailbox(settings: ActorSystem.Settings, config: Config)
  extends UnboundedPriorityMailbox (
  PriorityGenerator {
    case ZipUp => 0
    case UnZip => 0
    case PutIn(_) => 0
    case DrawOut(_) => 2
    case CheckBalance => 4
    case PoisonPill => 4
    case otherwise => 4
  }
)