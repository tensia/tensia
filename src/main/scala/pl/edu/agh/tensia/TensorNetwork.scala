package pl.edu.agh.tensia

import contraction.order.OrderFinder
import tensor.Tensor

/**
  * Created by mathek on 02/09/2017.
  */
case class TensorNetwork[T](tensors:Seq[Tensor[T]]) {
  def contract(implicit orderFinder: OrderFinder) = {
    val order = orderFinder findContractionOrder tensors
    computation run order.toComputationTree
  }
}
