/**
  * Created by mathek on 31/03/2017.
  */

import akka.actor.{ActorSystem, Props}
import pl.edu.agh.tensia.computation.ComputationNode
import pl.edu.agh.tensia.tensor._
import pl.edu.agh.tensia.helpers._
import pl.edu.agh.tensia.contraction_order.{BFSAlg, ContractedDims}

object Main extends App {
  val d = Seq(Dimension(2), Dimension(3), Dimension(4), Dimension(5))
  val t = Seq(Tensor.rand(d(1), d(2)), Tensor.rand(d(2), d(3)), Tensor.rand(d(0), d(1), d(3)))
  val contractedDims = mkContractedDims((t(0), t(1)) -> Seq((1, 0)), (t(0), t(2)) -> Seq((0, 1)), (t(1), t(2)) -> Seq((1, 2)))
  val contTree = BFSAlg.findContractionOrder(t, contractedDims)
  val compTree = contTree toCompTree contractedDims
  val system = ActorSystem("system")

  system.actorOf(Props(new ComputationNode(compTree)))
}
