package pl.edu.agh.tensia

import contraction.order.OrderFinder
import tensor.Tensor

import scala.concurrent.Future

/**
  * Created by mathek on 02/09/2017.
  */
case class TensorNetwork[T](tensors:Seq[Tensor[T]]) {
  def contract(implicit orderFinder: OrderFinder): Future[Tensor[T]] = {
    val order = orderFinder findContractionOrder tensors
    computation run order.toComputationTree
  }
  def contract(lockedTensors:Seq[Tensor[T]])(implicit orderFinder: OrderFinder): Future[Seq[Tensor[T]]] = {
    val order = orderFinder findContractionOrder (tensors, lockedTensors)
    val compTrees = order map (_.toComputationTree)
    computation run compTrees
  }
}
