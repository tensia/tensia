package pl.edu.agh.tensia.computation

import akka.actor._
import akka.pattern.pipe
import tree._

import scala.concurrent.Future

case class Result[T](r: T)

object ComputationNode {
  def props[T](tree: Tree[T]): Props = Props(new ComputationNode[T](tree))
}

case class ComputationNode[T](tree: Tree[T]) extends Actor {
  import context.dispatcher

  override def receive = tree match {
    case Node(op, l, r) =>
      List(l, r) map {subTree => context actorOf ComputationNode.props(subTree)}
      node(op)
    case Leaf(provider) =>
      Future {
        val res = Result[T](provider.get)
        println("provided", res)
        res
      } pipeTo self
      forwardingResult
    case Empty =>
      context stop self
      PartialFunction.empty
  }

  def forwardingResult: Receive = {
    case res:Result[T @unchecked] =>
      context.parent ! res
      context stop self
    case Status.Failure(ex) => throw ex
  }

  def node(op:BinOp[T], results: List[T] = List()): Receive = {
    case Result(res: T @unchecked) =>
      res :: results match {
        case List(l, r) =>
          Future {
            val res = Result(op(l, r))
            println("contraction_result", res)
            res
          } pipeTo self
          context become forwardingResult
        case l =>
          context become node(op, l)
      }
  }

}
