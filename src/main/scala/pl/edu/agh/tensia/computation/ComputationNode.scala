package pl.edu.agh.tensia.computation

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import akka.pattern.pipe
import pl.edu.agh.tensia.computation.comptree._

import scala.concurrent.Future

case object GetResult
case class Result[T](r: T)

object ComputationNode {
  def props[T](tree: Tree[T]): Props = Props(new ComputationNode[T](tree))
}

class ComputationNode[T](tree: Tree[T]) extends Actor with Stash with ActorLogging {
  import context.dispatcher
  private var childrenRes = List.empty[(ActorRef, Option[T])]

  tree match {
    case Node(op, l, r) =>
      childrenRes = List(l, r).map(subTree => {
        val childActor = context.actorOf(ComputationNode.props(subTree))
        (childActor, None)
      })
    case Leaf(provider) =>
      val res = Future {
        try {
          val res = Result[T](provider.get)
          println("provided", res)
          res
        } catch {
          case e:Error => e.printStackTrace()
        }
      }
      res pipeTo context.parent
      context stop self
    case Empty =>
      context stop self
  }

  override def receive: Receive = {
     case Result(res: T @unchecked) =>
      val List(l @ (lc, _), r @ (rc, _)) = childrenRes
      childrenRes =
        if (lc == sender) List((lc, Some(res)),r)
        else List(l, (rc, Some(res)))


      if (childrenRes.forall(_._2.nonEmpty)) {
        val node = tree.asInstanceOf[Node[T]]
        val List(lv, rv) = childrenRes.map(_._2.get)
        //todo improve error handling
        val nodeValue = Future {
          try {
            val res = Result(node.op(lv, rv))
            println("contraction_result", res)
            res
          }catch {
            case e:Error => e.printStackTrace()
          }
        }
        nodeValue pipeTo context.parent
        context stop self
      }
  }
}
