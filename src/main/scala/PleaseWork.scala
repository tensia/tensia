import pl.edu.agh.tensia.TensorNetwork
import pl.edu.agh.tensia.contraction.order.BFSOrderFinder
import pl.edu.agh.tensia.tensor.{Dimension, NDTensor}

import scala.concurrent.Await
import scala.concurrent.duration.Duration


/**
  * Created by mathek on 24/10/2017.
  */
object PleaseWork extends App {
  implicit val orderFinder = BFSOrderFinder

  val ih1 #:: ic1 #:: ih3 #:: c1h1 #:: c1h2 #:: c1c2 #:: c2h3 #:: c2h4 #:: _ = Stream continually Dimension(2)

  val initialState = NDTensor(Array(1,0, 0,0, 0,0, 0,0), ih1, ic1, ih3)

  val h1 = hadamard(ih1, c1h1)
  val h2 = hadamard(c1h2, 2)
  val h3 = hadamard(ih3, c2h3)
  val h4 = hadamard(c2h4, 2)

  val c1 = cnot(c1h1, c1h2, ic1, c1c2)
  val c2 = cnot(c1c2, c2h4, c2h3, 2)

  val tensors = Seq(initialState, h1, h2, h3, h4, c1, c2)

  println(Await.result(TensorNetwork(tensors).contract, Duration.Inf))

  def hadamard(d1: Dimension, d2: Dimension) = NDTensor(Array(1,1,1,-1) map (_ / Math.sqrt(2)), d1, d2)

  def cnot(d1: Dimension, d2: Dimension, d3: Dimension, d4: Dimension) =
    NDTensor(Array(1,0,0,0, 0,1,0,0, 0,0,0,1, 0,0,1,0), d1, d2, d3, d4)
}
