package pl.edu.agh.tensia

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import computation.tree._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by mathek on 03/09/2017.
  */
package object computation {
  def run[T](tree:Tree[T]): Future[T] = {

    val system = ActorSystem("tensia_computation_system")
    val resultGetter: ActorRef = system actorOf Props(new Actor {
      override def receive = {
        case t: Tree[T@unchecked] =>
          context actorOf ComputationNode.props(t)
          context become waiting(sender)
      }

      def waiting(respondTo: ActorRef): Receive = {
        case Result(res: T@unchecked) =>
          respondTo ! res
          context stop self
      }
    })
    implicit val timeout = Timeout(100 hours)
    resultGetter ? tree map {case res:T @unchecked => res}
  }

  def runWait[T](tree:Tree[T]): T = Await.result(run(tree), Duration.Inf)
}
