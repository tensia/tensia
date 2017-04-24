package pl.edu.agh.tensia

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import pl.edu.agh.tensia.comptree._

import scala.collection.mutable

case object GetResult
case class Result[T](r: T)

object ComputationNode {
  def props[T](tree: Tree[T]): Props = Props(new ComputationNode[T](tree))
}

class ComputationNode[T](tree: Tree[T]) extends Actor with Stash with ActorLogging {
  private var childrenRes = mutable.LinkedHashMap.empty[ActorRef, Option[T]]

  tree match {
    case Node(op, children @_*) =>
      childrenRes ++= children.map(subTree => (context.actorOf(ComputationNode.props(subTree)), None))
    case Leaf(provider) =>
      context.parent ! Result[T](provider.get)
      context stop self
    case Empty =>
      context stop self
  }

  override def receive: Receive = {
    case Result(res: T) =>
      childrenRes put(sender, Some(res))
      // TODO: make computation async
      if (childrenRes.values.forall(_.nonEmpty)) {
        val nodeValue = tree.asInstanceOf[Node].op(childrenRes.values.map(_.get))
        context.parent ! Result(nodeValue)
      }
  }
}
