package pl.edu.agh.tensia

import pl.edu.agh.tensia.contraction.order.OrderFinder
import pl.edu.agh.tensia.tensor.Tensor

/**
  * Created by mathek on 02/09/2017.
  */
case class TensorNetwork[T](tensors:Seq[Tensor[T]]) {
  def contract(implicit orderFinder: OrderFinder) = {
    val order = orderFinder findContractionOrder tensors
    computation run order.toComputationTree
  }
}
