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

  def run[T](trees:Seq[Tree[T]]): Future[Seq[T]] = {
    implicit val system = ActorSystem("tensia_computation_system")
    Future.sequence(trees map runTree)
  }
  def run[T](tree:Tree[T]):Future[T] = {
    run(Seq(tree)) map {case Seq(t) => t}
  }

  private def runTree[T](tree:Tree[T])(implicit system:ActorSystem) = {
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
    implicit val timeout = Timeout(100 days)
    resultGetter ? tree map {case res:T @unchecked => res}
  }

  def runWait[T](trees:Seq[Tree[T]]): Seq[T] = Await.result(run(trees), Duration.Inf)
  def runWait[T](tree:Tree[T]): T = Await.result(run(tree), Duration.Inf)
}
