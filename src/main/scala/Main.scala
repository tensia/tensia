/**
  * Created by mathek on 31/03/2017.
  */

import pl.edu.agh.tensia.contraction_order.BFSAlg
import pl.edu.agh.tensia.helpers._
import pl.edu.agh.tensia.tensorflow._

object Main extends App {
  val t = Seq(TensorRef.rand(3, 4), TensorRef.rand(4, 5), TensorRef.rand(2, 3, 5))
  val Seq(tensorA, tensorB, tensorC) = t
  val contractedDims = mkContractedDims(
    (tensorA, tensorB) -> Seq((1, 0)),
    (tensorA, tensorC) -> Seq((0, 1)),
    (tensorB, tensorC) -> Seq((1, 2))
  )
  val contTree = BFSAlg.findContractionOrder(t, contractedDims)
  /*val compTree = contTree toCompTree contractedDims
  val system = ActorSystem("system")

  system.actorOf(Props(new ComputationNode(compTree)))
  */
}
