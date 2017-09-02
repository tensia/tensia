/**
  * Created by mathek on 31/03/2017.
  */

import akka.actor.{ActorSystem, Props}
import pl.edu.agh.tensia.TensorNetwork
import pl.edu.agh.tensia.computation.ComputationNode
import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.tensor._

object Main extends App {
  val d:Seq[Dimension] = Seq(2, 3, 4, 5)
  val tensors = Seq(ScalaTensor.rand(d(1), d(2)), ScalaTensor.rand(d(2), d(3)), ScalaTensor.rand(d(0), d(1), d(3)))
  implicit val orderFinder = BFSOrderFinder
  TensorNetwork(tensors).contract
//  val contractionTree = BFSOrderFinder findContractionOrder tensors
//  val computationTree = contractionTree toComputationTree
//  val system = ActorSystem("system")
//
//  system.actorOf(Props(new ComputationNode(computationTree)))
}
