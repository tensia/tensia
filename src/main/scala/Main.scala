/**
  * Created by mathek on 31/03/2017.
  */

import akka.actor.{ActorSystem, Props}
import pl.edu.agh.tensia.computation.ComputationNode
import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.tensor._

object Main extends App {
  val d:Seq[Dimension] = Seq(2, 3, 4, 5)
  val tensors = Seq(Tensor.rand(d(1), d(2)), Tensor.rand(d(2), d(3)), Tensor.rand(d(0), d(1), d(3)))
  val contractionTree = BFSOrderFinder findContractionOrder tensors
  val computationTree = contractionTree toCompTree
  val system = ActorSystem("system")

  system.actorOf(Props(new ComputationNode(computationTree)))
}
